package injectview.lym.org.sampleproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

/**
 *
 * @author yaoming.li
 * @since 2017-09-03 12:43
 */
public class MyGridView extends GridView implements GridView.OnItemClickListener{

    private static final String [] arr = {"hello","hello","hello","hello","hello","hello","hello","hello",};

    public MyGridView(Context context, AttributeSet attrs) {
        this(context,null,0);
    }

    public MyGridView(Context context) {
        this(context,null);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setNumColumns(4);
        setOnItemClickListener(this);
        initGridView(context);
    }

    private void initGridView(Context context) {
        setAdapter(new ArrayAdapter<>(context,R.layout.text,R.id.tv,arr));
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(adapterView.getContext(),String.valueOf(i),Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                requestDisallowInterceptTouchEvent(true);
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
