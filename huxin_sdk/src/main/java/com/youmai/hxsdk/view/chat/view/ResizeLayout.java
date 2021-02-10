package com.youmai.hxsdk.view.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class ResizeLayout extends RelativeLayout {

    private static final String TAG = ResizeLayout.class.getSimpleName();
    private Context mContext;
    private int mMaxParentHeight = 0;
    private ArrayList<Integer> heightList = new ArrayList<Integer>();

    public ResizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mMaxParentHeight == 0) {
            mMaxParentHeight = h;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureHeight = measureHeight(heightMeasureSpec);
        heightList.add(measureHeight);
        if (mMaxParentHeight != 0) {
            //避免键盘弹出的变化也影响整体的高度计算
            if (Math.abs(measureHeight - mMaxParentHeight) < 200) {
                mMaxParentHeight = measureHeight;
            }
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int expandSpec = MeasureSpec.makeMeasureSpec(mMaxParentHeight, heightMode);
            super.onMeasure(widthMeasureSpec, expandSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (heightList.size() >= 2) {
            int oldh = heightList.get(0);
            int newh = heightList.get(heightList.size() - 1);
            int softHeight = mMaxParentHeight - newh;
             /*
             newh         oldh
             500            max         -> 弹起                softKeyboard = max - 500
             600            500         -> 缩小                softKeyboard = max - 600
             500            600         -> 拉伸               softKeyboard = max - 500
             max           500         -> 关闭               softKeyboard = 0
             */
            /**
             * 弹出软键盘
             */
            if (oldh == mMaxParentHeight) {
                if (mListener != null) {
                    mListener.OnSoftKeyboardPop(softHeight);
                }
            }
            /**
             * 隐藏软键盘
             */
            else if (newh == mMaxParentHeight) {
                if (mListener != null) {
                    mListener.OnSoftKeyboardClose(softHeight);
                }
            }
            /**
             * 调整软键盘高度
             */
            else {
                if (mListener != null) {
                    if (newh + softHeight == mMaxParentHeight) {
                        if (softHeight > 300) {
                            mListener.OnSoftKeyboardPop(softHeight);
                        } else {
                            mListener.OnSoftKeyboardClose(softHeight);
                        }
                    } else {
                        mListener.onSoftChangeHeight(softHeight);
                    }
                }
            }
            heightList.clear();
        } else {
            heightList.clear();
        }
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = heightSize;
                break;
        }
        return result;
    }

    private OnResizeListener mListener;

    public void setOnResizeListener(OnResizeListener l) {
        mListener = l;
    }

    public interface OnResizeListener {

        /**
         * 软键盘弹起
         */
        void OnSoftKeyboardPop(int height);

        /**
         * 软键盘关闭
         */
        void OnSoftKeyboardClose(int height);

        /**
         * 软键盘高度改变
         */
        void onSoftChangeHeight(int height);
    }
}