package injectview.lym.org.sampleproject;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author yaoming.li
 * @since 2017-09-02 18:51
 */
public class DetailViewPager extends ViewPager {
    private HashMap<Integer, Object> mObject = new LinkedHashMap<>();

    public DetailViewPager(Context context) {
        super(context);
    }

    public DetailViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setObjectForPosition(Object obj, int position) {
        mObject.put(Integer.valueOf(position), obj);
    }

    public View findViewFromObject(int position) {
        Object o = mObject.get(Integer.valueOf(position));
        if (o == null) {
            return null;
        }
        PagerAdapter a = getAdapter();
        View v;
        for (int i = 0; i < getChildCount(); i++) {
            v = getChildAt(i);
            if (a.isViewFromObject(v, o))
                return v;
        }
        return null;
    }

    public boolean isStandTop() {
            return defaultHelper.mayScrollUp();
    }

    public void onMyTouchEvent(MotionEvent event) {
        View view = findViewFromObject(getCurrentItem());
        if (view != null) {
            view.onTouchEvent(event);
        }
    }


    public interface ScrollHelper {
        boolean mayScrollUp();
    }

    private ScrollHelper defaultHelper = new ScrollHelper() {

        @Override
        public boolean mayScrollUp() {
            View view = findViewFromObject(getCurrentItem());
            if (view != null) {
                if (view instanceof ListView) {
                    ListView listView = (ListView) view;
                    if (listView.getChildCount() == 0) {
                        return true;
                    }
                    if (listView.getPaddingTop() > 0) {
                        if (listView.getFirstVisiblePosition() == 0 && listView.getChildCount() > 0
                                && listView.getChildAt(0).getTop() >= listView.getPaddingTop()) {
                            return true;
                        }
                    } else {
                        if (listView.getFirstVisiblePosition() == 0 && listView.getChildCount() > 0
                                && listView.getChildAt(0).getTop() >= 0) {
                            return true;
                        }
                    }

                }  else if (view instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) view;
                    if (!recyclerView.canScrollVertically(-1)) {
                        return true;
                    }
                } else if (view instanceof ScrollView) {
                    ScrollView scrollView = (ScrollView) view;
                    if (scrollView.getScrollY() == 0) {
                        return true;
                    }
                }  else if (view instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view;
                    for (int i = 0; i < group.getChildCount(); i++) {
                        View child = group.getChildAt(i);
                        if (child instanceof RecyclerView) {
                            RecyclerView recyclerView = (RecyclerView) child;
                            if (recyclerView.canScrollVertically(-1)) {
                                return false;
                            }
                        } else if (child instanceof ScrollView) {
                            ScrollView scrollView = (ScrollView) child;
                            if (scrollView.getScrollY() > 0) {
                                return false;
                            }
                        }
                    }
                    return true;
                } else {
                    return true;
                }
                return false;
            } else {
                return true;
            }
        }
    };
}
