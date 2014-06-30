package se.slide.sharetocalendar;

import android.view.View;
import android.widget.ImageView;

import se.slide.utils.Statics;
import se.slide.utils.about.AboutLines;
import se.slide.utils.about.KenBurnsView;
import se.slide.utils.about.NoBoringActionBarActivity;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends NoBoringActionBarActivity {

    @Override
    public List<AboutLines> getAboutLines() {
        ArrayList<AboutLines> lines = new ArrayList<AboutLines>();
        
        AboutLines a = new AboutLines();
        a.row1 = "Version";
        a.row2 = Statics.getAppVersion(this);
        lines.add(a);
        
        a = new AboutLines();
        a.row1 = "By";
        a.row2 = "slide.se";
        lines.add(a);
        
        a = new AboutLines();
        a.row1 = "Note";
        a.row2 = "If you have any feedback please email me at mike@slide.se or contact me on Google+. I'd love to hear from you :)";
        lines.add(a);
        
        a = new AboutLines();
        a.row1 = "";
        a.row2 = "";
        lines.add(a);
        
        a = new AboutLines();
        a.row1 = "Crashlytics";
        a.row2 = "https://www.crashlytics.com/";
        lines.add(a);
        
        a = new AboutLines();
        a.row1 = "Google Analytics";
        a.row2 = "https://www.google.com/analytics/";
        lines.add(a);
        
        a = new AboutLines();
        a.row1 = "NotBoringActionBar";
        a.row2 = "https://github.com/flavienlaurent/NotBoringActionBar";
        lines.add(a);
        
        return lines;
    }

    @Override
    public int[] getDrawableResources() {
        return new int[] { R.drawable.sharetocalendar_about };
    }

    @Override
    public void setupLogo(ImageView logo) {
        logo.setImageResource(R.drawable.sharetocalendar_about_nodpi);
    }

    public ImageView[] setupKenBurnsView(KenBurnsView mHeaderPicture) {
        View view = View.inflate(this, R.layout.view_kenburns_one_image, mHeaderPicture);
        
        ImageView[] mImageViews = new ImageView[1];
        mImageViews[0] = (ImageView) view.findViewById(R.id.image0);
        
        return mImageViews;
    }
}
