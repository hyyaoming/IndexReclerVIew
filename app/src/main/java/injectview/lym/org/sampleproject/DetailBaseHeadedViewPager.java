package injectview.lym.org.sampleproject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**
 * Desp.
 *
 * @author yaoming.li
 * @since 2017-09-02 19:02
 */
public class DetailBaseHeadedViewPager extends FrameLayout implements GestureDetector.OnGestureListener {

    protected View headView, headScrollerView;
    protected DetailViewPager mainViewPager;

    private onScrollerListener listener;
    protected onShowTitleListener titleListener;
    protected onScrollerShowHeadBottomListener scrollerListener;
    protected LayoutInflater mInflater;
    protected Context mContext;
    private float mLastMotionX;
    private float mLastMotionY;// 最后点击的点
    private GestureDetector detector;
    protected int move = 0;// 移动距离
    protected int MAXMOVE = 0;// 最大允许的移动距离
    /**
     * 状态栏高度
     */
    private  int statusHeight = 0;
    protected MyScroller mScroller;
    int down_excess_move = 0;// 往下多移的距离
    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private int mTouchState = TOUCH_STATE_REST;
    private int mTouchSlop;

    protected boolean AnimUpIsRun = false;
    protected boolean canPull = false;

    /**
     * 控制SwipeLayout的动作
     */
    private ISwipeActionController mSwipeController;

    public DetailBaseHeadedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public  int getActivityMarginTop(Context context) {   //基于4.4以下不支持overlay模式，4.4以上activity向上距离0，4.4以下为状态栏高度
        if (Build.VERSION.SDK_INT < 19) {
            return getStatusHeight(context);
        }
        return 0;
    }

    public  int getStatusHeight(Context context) {
//		init(context);
//		return statusHeight;
        if (statusHeight == 0) {
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                statusHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
                Rect frame = new Rect();
                ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

                statusHeight = frame.top;
            }
        }
        return statusHeight;
    }

    public  int getScreenHeight(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);


        return metric.heightPixels;
    }


    private void init(final Context context){
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mScroller = new MyScroller(context);
        mScroller.setFlingTime(500);
        detector = new GestureDetector(this);

        // 获得可以认为是滚动的距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        getHeadView();

        mainViewPager = new DetailViewPager(context);
        mainViewPager.setId(R.id.headed_viewpager_id);
        mainViewPager.setOffscreenPageLimit(4);

        addView(mainViewPager, new LayoutParams(LayoutParams.MATCH_PARENT, getScreenHeight(context) - getActivityMarginTop(context)));
        addView(headView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mainViewPager.setBackgroundColor(getResources().getColor(R.color.common_bg));

        headView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (context instanceof BaseActivity){
                            if (!((BaseActivity)context).activityActive){  //如果当前界面不可见，直接返回
                                return;
                            }
                        }
                        int height = headView.getHeight() - getHeadHeight();
                        if (headScrollerView != null){
                            int fullheight = ((ViewGroup)headScrollerView).getChildAt(0).getHeight();
                            if ( fullheight != headView.getHeight()){
                                LayoutParams lp = (LayoutParams) headView.getLayoutParams();
                                lp.height =  fullheight;
                                headView.setLayoutParams(lp);
                                height = fullheight - getHeadHeight();
                            }
                        }
                        if (MAXMOVE != height && height >= 0) {
                            MAXMOVE = height;
                            LayoutParams lp = (LayoutParams) mainViewPager.getLayoutParams();
                            lp.topMargin = MAXMOVE;
                            lp.height = getScreenHeight(mContext) - getActivityMarginTop(mContext);
                            mainViewPager.setLayoutParams(lp);
                            if (move > MAXMOVE){
                                move = MAXMOVE;
                                scrollTo(0, move);
                                if (titleListener != null) {
                                    titleListener.onShowTitle(true,true);
                                }
                            }
                        }
                    }
                });
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        LayoutParams lp = (LayoutParams) mainViewPager.getLayoutParams();
                        int screenHeight = getScreenHeight(mContext)- getActivityMarginTop(mContext);
                        if (lp.height != screenHeight) {
                            lp.height = screenHeight;
                            mainViewPager.setLayoutParams(lp);
                        }
                    }
                });
        initContent();
    }

    protected int getViewPagerHeight() {
        return 0;
    }

    protected void initContent() {

    }
    protected void getHeadView(){

    }
    public  int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + FLOAT_0_5);
    }

    private static final float FLOAT_0_5 = 0.5f;


    protected int getHeadHeight() {
        return dip2px(mContext, 38);//状态栏+actionbar+tab
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (AnimUpIsRun) {
            return true;
        }

        final float y = ev.getY();
        final float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                mLastMotionX = x;
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_MOVE:
                int yDiff = (int) Math.abs(y - mLastMotionY);
                if (yDiff >= mTouchSlop) {
                    int[] location = { 0, 0 };
                    mainViewPager.getLocationInWindow(location);
                    if (mLastMotionY < location[1]) {
                        mTouchState = TOUCH_STATE_SCROLLING;
                        break;
                    }
                    int xDiff = (int) Math.abs(mLastMotionX - x);
                    if (xDiff < yDiff){
                        if ((mLastMotionY - y) > 0 ) {//上移
                            if (move < MAXMOVE) {
                                mTouchState = TOUCH_STATE_SCROLLING;
                            }
                        }else {
                            if (mainViewPager.isStandTop()) {
                                mTouchState = TOUCH_STATE_SCROLLING;
                            }
                        }
                    }else {
                        if (mainViewPager.isStandTop()) {
                            mTouchState = TOUCH_STATE_SCROLLING;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return mTouchState != TOUCH_STATE_REST;
    }
    private boolean isSendEventDown = false;   //向下传递ontouch事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (AnimUpIsRun) {
            return true;
        }

        if(iMoveListener != null)
            iMoveListener.onMoved();

        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mSwipeController != null)
                    mSwipeController.enableSwipe(false);

                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                    move = mScroller.getFinalY();
                }
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if(mSwipeController != null)
                    mSwipeController.enableSwipe(false);

                //通知父控件勿拦截本控件touch事件
                if (ev.getPointerCount() == 1) {
                    // 随手指 拖动的代码
                    int deltaY = 0;
                    deltaY = (int) (mLastMotionY - y);
                    mLastMotionY = y;
                    if (deltaY < 0) {
                        // 下移
                        // 判断上移 是否滑过头
                        if (move > 0) {
                            if (move == MAXMOVE&&isSendEventDown&&!mainViewPager.isStandTop()) {
                                mainViewPager.onMyTouchEvent(ev);
                            }else {
                                int move_this = Math.max(-move, deltaY);
                                move = move + move_this;
                                scrollBy(0, move_this);
                                if (listener != null) {
                                    listener.onScrollerBy(move_this);
                                }
                            }
                        } else if (move == 0) {// 如果已经是最顶端 继续往下拉
                            if (canPull) {
                                down_excess_move = down_excess_move - deltaY / 2;// 记录下多往下拉的值
                                scrollBy(0, deltaY / 2);
                                if (down_excess_move > 0 ) {
                                    setHeadLogoVisibility(false);
                                    //setFocusVisibility(false);
                                }
                            }
                        }
                    } else if (deltaY > 0) {
                        // 上移
                        if (down_excess_move == 0) {
                            if (MAXMOVE - move > 0) {
                                int move_this = Math.min(MAXMOVE - move, deltaY);
                                move = move + move_this;
                                scrollBy(0, move_this);

                                if (listener != null) {
                                    listener.onScrollerBy(move_this);
                                }
                            }else {
                                if (!isSendEventDown) {
                                    ev.setAction(MotionEvent.ACTION_DOWN);
                                    mainViewPager.onMyTouchEvent(ev);
                                    isSendEventDown = true;
                                }else {
                                    mainViewPager.onMyTouchEvent(ev);
                                }
                            }
                        } else if (down_excess_move > 0) {
                            if (down_excess_move >= deltaY) {
                                down_excess_move = down_excess_move - deltaY;
                                scrollBy(0, deltaY);
                            } else {
                                down_excess_move = 0;
                            }
                        }
                    }
                }
                if (move == MAXMOVE) {
                    if (titleListener != null) {
                        titleListener.onShowTitle(true,true);
                    }
                }else {
                    if (titleListener != null) {
                        titleListener.onShowTitle(false,true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isSendEventDown = false;
                if (down_excess_move > 200) {
                    int from = -down_excess_move;
                    int to = -getScreenHeight(getContext());
                    Animation anim = new ScrollerAnimation(this,from, to,-1);
                    AnimUpIsRun = true;
                    startAnimation(anim);
                    if (listener != null) {
                        listener.onScrollerDismiss(Math.abs(from - to)/2);
                    }
                    move = to;
                    down_excess_move = 0;
                }else if (down_excess_move > 0) {
                    // 多滚了  要弹回去
                    startAnimation(new ScrollerAnimation(this,-down_excess_move, 0,-1));
                    move = 0;
                    down_excess_move = 0;
                    setHeadLogoVisibility(true);
                }else {
                    setHeadLogoVisibility(true);
                }
                mTouchState = TOUCH_STATE_REST;

                if(mSwipeController != null)
                    mSwipeController.enableSwipe(true);
                break;
        }
        return this.detector.onTouchEvent(ev);
    }
    protected void setHeadLogoVisibility(boolean bShow){
    }
    protected void setFocusVisibility(boolean bShow){
    }
    public void setAnimUpRun(boolean isRun)
    {
        AnimUpIsRun = isRun;
    }
    public boolean getAnimUpIsRun()
    {
        return AnimUpIsRun;
    }
    public void setMove(int move){
        this.move = move;
    }
    public int getMove() {
        return move;
    }
    public void setCanPull(boolean state) {
        canPull = state;
    }
    public void setOnScrollerListener(onScrollerListener listener){
        this.listener = listener;
    }
    public void setTitleListener(onShowTitleListener listener) {
        this.titleListener = listener;
    }
    public void setOnScrollerShowListener(onScrollerShowHeadBottomListener listener){
        this.scrollerListener = listener;
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (down_excess_move == 0) {
            int slow = -(int) velocityY;
            mScroller.fling(0, move, 0, slow, 0, 0, 0, MAXMOVE);
            move = mScroller.getFinalY();
            computeScroll();
        }
        return false;
    }
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            // 返回当前滚动Y方向的偏移
            if (move == MAXMOVE) {
                if (titleListener != null) {
                    titleListener.onShowTitle(true,true);
                }
            }else {
                if (titleListener != null) {
                    titleListener.onShowTitle(false,true);
                }
            }
            if (listener != null) {
                listener.onScrollerTo(mScroller.getCurrY());
            }
            scrollTo(0,mScroller.getCurrY());
            postInvalidate();
        }
    }
    public interface onScrollerListener{
        public void onScrollerBy(int move);
        public void onScrollerTo(int to);
        public void onScrollerDismiss(int duration);
    }
    public interface onShowTitleListener{
        public void onShowTitle(boolean bshow,boolean withAnim);
    }
    public interface onScrollerShowHeadBottomListener{
        public void onScrollerShow(boolean bShow);
    }
    protected void scrollTop(){
        mScroller.startScroll(0,move,0,MAXMOVE - move,MAXMOVE - move);
        move = MAXMOVE;
    }

    protected void scrollTop(boolean isDelayed){
        move = MAXMOVE;
        if (isDelayed){
            mScroller.startScroll(0,move,0,MAXMOVE - move,MAXMOVE - move);
        }else {
            scrollTo(0,MAXMOVE);
            if (titleListener != null){
                titleListener.onShowTitle(true,false);
            }
        }
    }
    public interface IContentMoveListener {
        public void onMoved();
    }

    private IContentMoveListener iMoveListener;

    public void setIContentMoveListener(IContentMoveListener listener) {
        this.iMoveListener = listener;
    }

    /**
     * see DetailGroupHelper
     * @param first
     */
    public void showDownBtn(boolean first) {

    }

    public interface ISwipeActionController {
        public void enableSwipe(boolean enable);
    }

    public void setISwipeActionController(ISwipeActionController listener) {
        this.mSwipeController = listener;
    }

}
