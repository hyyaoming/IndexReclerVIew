package injectview.lym.org.sampleproject;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 *
 * @author yaoming.li
 * @since 2017-09-02 19:10
 */
public class ScrollerAnimation extends Animation {
    private View view;
    private int from;
    private int to;
    public ScrollerAnimation(View view,int from,int to,int duration){
        this.from = from;
        this.to = to;
        this.view = view;
        if (duration <= 0 ) {
            duration = Math.abs(from - to)/2;
        }
        setDuration(duration);
    }
    @Override
    protected void applyTransformation(float interpolatedTime,
                                       Transformation t) {
        if (view!= null) {
            view.scrollTo(0, (int) (from+(to -from)*interpolatedTime));
        }
    }

}
