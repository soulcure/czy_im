package com.youmai.hxsdk.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

/**
 * @author XINYE
 */
public class CustomHorizontalScrollView extends HorizontalScrollView {
    private int subChildCount = 0;
    private int downX = 0;
    private int currentPage = 0;
    private ArrayList<Integer> pointList = new ArrayList<>();

    public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomHorizontalScrollView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        receiveChildInfo();
    }

    public void receiveChildInfo() {
        ViewGroup firstChild = (ViewGroup) getChildAt(0);
        if (firstChild != null) {
            subChildCount = firstChild.getChildCount();
            for (int i = 0; i < subChildCount; i++) {
                //if ((firstChild.getChildAt(i)).getWidth() > 0) {
                    pointList.add((firstChild.getChildAt(i)).getLeft());
                //}
            }
        }

    }


    /**
     * 滑动事件
     */
    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 4);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                break;
            case MotionEvent.ACTION_MOVE: {

            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (Math.abs((ev.getX() - downX)) < getWidth() / 2) {
                    if (ev.getX() - downX > 0) {
                        smoothScrollToPrePage();
                    } else {
                        smoothScrollToNextPage();
                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void smoothScrollToCurrent() {
        smoothScrollTo(pointList.get(currentPage), 0);
    }

    private void smoothScrollToNextPage() {
        if (currentPage < subChildCount - 1) {
            currentPage++;
            smoothScrollTo(pointList.get(currentPage), 0);
        }
    }

    private void smoothScrollToPrePage() {
        if (currentPage > 0) {
            currentPage--;
            smoothScrollTo(pointList.get(currentPage), 0);
        }
    }

}