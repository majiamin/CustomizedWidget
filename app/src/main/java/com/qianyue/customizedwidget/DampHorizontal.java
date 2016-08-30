package com.qianyue.customizedwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * 横向阻尼滑动ViewGroup
 * Created by abc on 2016/8/25.
 */
public class DampHorizontal extends ViewGroup {

    private static final String TAG = DampHorizontal.class.getSimpleName();
    private float SCALE_X = 0.7f; // 子view的宽度相对于屏幕宽度的缩放比例,可以设置,不能大于1


    private int bound = 80; // 露出的宽度,可以设置
    private int offset; // 两个子view之间的距离,这个是通过bound和屏幕宽度,以及子view宽度算出的
    private int to_scrollX;// 要划过的距离
    private int TOTAL_WIDTH; // 总宽度,在测量出子view之后算出,滑动时候用

    private int Y_OFFSET = 50;// 子view高度相对父容器的偏移量,可以设置
    private int mScreenWidth;// 屏幕的宽度,在构造方法中算出
    private int realChildWidth;// 子view的实际宽度,在构造方法中算出来

    private int lastX;          // 记录上次事件的x偏移
    private Scroller mScroller;

    private int startX;// 记录mScroller滑动的起点


    public DampHorizontal(Context context) {
        this(context, null);
    }

    public DampHorizontal(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DampHorizontal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context/*, new OvershootInterpolator()*/);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        mScreenWidth = windowManager.getDefaultDisplay().getWidth();
        //子View实际的宽度
        realChildWidth = (int) (mScreenWidth * SCALE_X);
        offset = (mScreenWidth - realChildWidth - 2 * bound) / 2;
        to_scrollX = realChildWidth + offset;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.setBackgroundResource(R.mipmap.backgroud);
            child.setPadding(10, 10, 10, 10);
            if (i != 0) {
                child.setBackgroundResource(R.mipmap.background_withoutcorner);
                child.setScaleY(0.8f);
                child.setScaleX(0.8f);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int realChildHeight = (heightSize - 2 * Y_OFFSET);//子View实际测出的高度
        int newHeightSpec = MeasureSpec.makeMeasureSpec(realChildHeight,
                MeasureSpec.getMode(heightMeasureSpec));


        int newWidthSpec = MeasureSpec.makeMeasureSpec(realChildWidth,
                MeasureSpec.getMode(widthMeasureSpec));


        TOTAL_WIDTH = bound * 2 + childCount * realChildWidth + offset * (childCount + 1);

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, newWidthSpec, newHeightSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount > 0) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) getLayoutParams();
            marginLayoutParams.width = mScreenWidth;
            setLayoutParams(marginLayoutParams);
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                childView.layout(bound + (i + 1) * offset + i * realChildWidth, Y_OFFSET, bound +
                                (i + 1) * offset + (i + 1) * realChildWidth,
                        getMeasuredHeight() - Y_OFFSET);
            }

        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i(TAG, "onTouchEvent: " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                lastX = (int) event.getX();
                return true;

            case MotionEvent.ACTION_MOVE:

                int x = (int) event.getX();
                int deltaX = x - lastX;
                if (getScrollX() < 0 && deltaX > 0) {
                    deltaX = 0;
                }
                if (getScrollX() > (TOTAL_WIDTH - mScreenWidth) && deltaX < 0) {
                    deltaX = 0;
                }

                // 避免两个手指同时按出现的划出界的bug
                if (Math.abs(deltaX)>to_scrollX/3) {
                    return true;
                }

                scrollBy(-deltaX, 0);
                lastX = x;
                return true;
            case MotionEvent.ACTION_UP:

                int endX = getScrollX();
                int dScrollX = endX - startX;
                if (dScrollX > 0) {
                    if (dScrollX > to_scrollX / 3) {
                        mScroller.startScroll(getScrollX(), 0, to_scrollX - dScrollX, 0);
                    } else {
                        mScroller.startScroll(getScrollX(), 0, -dScrollX, 0);
                    }
                } else {
                    if (-dScrollX > to_scrollX / 3) {
                        mScroller.startScroll(getScrollX(), 0, -to_scrollX - dScrollX, 0);
                    } else {
                        mScroller.startScroll(getScrollX(), 0, -dScrollX, 0);
                    }
                }
                postInvalidate();
                return true;

            case MotionEvent.ACTION_SCROLL:
                if (getScrollX() < 0) {
                    setScrollX(0);
                }

                break;
            default:
                break;
        }


        return true;
    }

    @Override
    public void computeScroll() {
        if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                lastX = (int) ev.getX();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    return true;// 这里返回true  因为在滑动动画没有结束时恰好点到了子view中可点击的控件上,
                    // 返回false会导致此控件的Action_UP事件不走,因此返回true
                }
                startX = getScrollX();
                return false;

            case MotionEvent.ACTION_MOVE:
                // 当滑动小于30时不拦截事件 主要是解决和子View的事件冲突
                if (Math.abs(ev.getX() - lastX) < 30) {
                    return false;
                }
                return true;

            case MotionEvent.ACTION_UP:
                return false;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int crisis = getScrollX() - (i - 1) * (realChildWidth + offset);
            if (crisis >= 0 && crisis <= 2 * (realChildWidth + offset)) {
                int temp = realChildWidth + offset;

                childView.setScaleX(crisis <= temp ? (0.8f + crisis * 0.2f / temp) : (1f -
                        (crisis - temp) * 0.2f / temp));
                childView.setScaleY(crisis <= temp ? (0.8f + crisis * 0.2f / temp) : (1f -
                        (crisis - temp) * 0.2f / temp));
                if (Math.abs(crisis - temp) < 25) {
                    childView.setBackgroundResource(R.mipmap.backgroud);
                } else {
                    childView.setBackgroundResource(R.mipmap.background_withoutcorner);
                }
            }
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }


}
