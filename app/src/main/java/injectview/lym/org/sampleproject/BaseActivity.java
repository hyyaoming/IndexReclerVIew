package injectview.lym.org.sampleproject;

import android.support.v7.app.AppCompatActivity;

/**
 * Desp.
 *
 * @author yaoming.li
 * @since 2017-09-02 19:08
 */
public class BaseActivity extends AppCompatActivity {
    public boolean activityActive = false;


    @Override
    protected void onResume() {
        super.onResume();

        activityActive = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        activityActive = false;
    }

}
