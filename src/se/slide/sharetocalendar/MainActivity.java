
package se.slide.sharetocalendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends FragmentActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    
    public static final String LAST_SELECTED_CALENDAR = "last_selected_calendar";
    
    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
        Calendars._ID,                           // 0
        Calendars.ACCOUNT_NAME,                  // 1
        Calendars.CALENDAR_DISPLAY_NAME,         // 2
        Calendars.OWNER_ACCOUNT                  // 3
    };
       
    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    
    private Calendar            mCalendar;
    private Button              mButtonDate;
    private Button              mButtonTime;
    private Spinner             mSpinner;
    private EditText            mTitle;
    private EditText            mDescription;
    
    private final List<String>  mCalendarName = new ArrayList<String>();
    private final List<Long>    mCalendarIds = new ArrayList<Long>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Crashlytics.start(this);
        EasyTracker.getInstance(this).activityStart(this);
        
        setContentView(R.layout.activity_main);
        
        
        
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        String title = "";
        String description = "";
        
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            description = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        
        Button save = (Button) findViewById(R.id.buttonSave);
        Button cancel = (Button) findViewById(R.id.buttonCancel);
        mSpinner = (Spinner) findViewById(R.id.spinnerCalendar);
        mButtonDate = (Button) findViewById(R.id.buttonDate);
        mButtonTime = (Button) findViewById(R.id.buttonTime);
        mTitle = (EditText) findViewById(R.id.title);
        mDescription = (EditText) findViewById(R.id.description);
        
        // Set initial text and description
        mTitle.setText(title);
        mDescription.setText(description);
        
        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = Calendars.CONTENT_URI;   
        String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
                                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                                + Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {"www.slide.se@gmail.com", "com.google", "www.slide.se@gmail.com"}; 
        // Submit the query and get a Cursor object back. 
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        
        Cursor calCursor = cr.query(uri, EVENT_PROJECTION, Calendars.VISIBLE + " = 1", null, Calendars._ID + " ASC");
        
        
        
        // Use the cursor to step through the returned records
        while (calCursor.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;
              
            // Get the field values
            calID = calCursor.getLong(PROJECTION_ID_INDEX);
            displayName = calCursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = calCursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = calCursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                      
            mCalendarName.add(displayName);
            mCalendarIds.add(calID);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCalendarName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(view.getContext(), calendarName.get(position) + ", " + calendarIds.get(position), Toast.LENGTH_SHORT).show();
                PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().putLong(LAST_SELECTED_CALENDAR, mCalendarIds.get(position)).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                
        }});
        
        long calID = PreferenceManager.getDefaultSharedPreferences(this).getLong(LAST_SELECTED_CALENDAR, 1);
        int index = mCalendarIds.indexOf(calID);
        mSpinner.setSelection(index);
        
        mCalendar = Calendar.getInstance();
        
        mButtonDate.setText(getFormatedDate(mCalendar));
        mButtonTime.setText(getFormatedTime(mCalendar));
        
        mButtonDate.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, MainActivity.this, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        
        mButtonTime.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, MainActivity.this, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(MainActivity.this));
                timePickerDialog.show();
            }
        });
        
        save.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                saveToCalendar();
            }
        });
        
        cancel.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                killApp();
            }
        });
        
        /*
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DateTime dialog = DateTime.newInstance("", "");
        dialog.show(ft, "dialog");
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onStart() {
      super.onStart();
      EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
      super.onStop();
      EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }
    
    @Override
    protected void onPause() {
        killApp();
        super.onPause();
    }

    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
        mCalendar.set(Calendar.YEAR, selectedYear);
        mCalendar.set(Calendar.MONTH, selectedMonth);
        mCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);
        
        mButtonDate.setText(getFormatedDate(mCalendar));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        
        mButtonTime.setText(getFormatedTime(mCalendar));
    }
    
    public String getFormatedTime(Calendar c) {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
        return dateFormat.format(c.getTime());
    }
    
    public String getFormatedDate(Calendar c) {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        return dateFormat.format(c.getTime());
    }
    
    private void killApp() {
        finish();
        //int pid = android.os.Process.myPid();
        //android.os.Process.sendSignal(pid, android.os.Process.SIGNAL_KILL);
        //android.os.Process.killProcess(pid);
    }
    
    private void saveToCalendar() {
        
        boolean alarm = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_alarm", true);
        int hasAlarm = 1;
        if (!alarm)
            hasAlarm = 0;
        
        long start = mCalendar.getTimeInMillis();
        int position = mSpinner.getSelectedItemPosition();
        long calID = mCalendarIds.get(position);
        
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, start);
        values.put(Events.DTEND, start);
        values.put(Events.TITLE, title);
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(Events.DESCRIPTION, description);
        values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        values.put(Events.HAS_ALARM, hasAlarm);
        
        Uri uri =  getContentResolver().insert(Events.CONTENT_URI, values);
        
        String segment = uri.getLastPathSegment();
        if (segment != null) {
            long eventId = new Long(segment);
            killApp();
        }
        else {
            Toast.makeText(this, R.string.error_event, Toast.LENGTH_LONG).show();
        }
        
        
    }
}
