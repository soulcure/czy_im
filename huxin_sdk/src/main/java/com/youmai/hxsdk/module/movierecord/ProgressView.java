package com.youmai.hxsdk.module.movierecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.youmai.hxsdk.R;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-07-24 23:23
 * Description:
 */
public class ProgressView extends View {

    /** 进度条 */
    private Paint mProgressPaint;

    /** 回删 */
    private Paint mRemovePaint;

    /** 最长时长 */
    private int mMax;

    /** 进度*/
    private int mProgress;

    private boolean isRemove;
    public ProgressView(Context Context, AttributeSet Attr) {
        super(Context, Attr);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ProgressView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mProgressPaint = new Paint();
        mRemovePaint = new Paint();
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mProgressPaint.setColor(getResources().getColor(R.color.hx_main_color));
        mProgressPaint.setStyle(Paint.Style.FILL);
        mRemovePaint.setColor(getResources().getColor(R.color.hxs_mr_color_red));
        mRemovePaint.setStyle(Paint.Style.FILL);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        int progressLength = (int) ((mProgress / (mMax * 1.0f)) * (width / 2));
        canvas.drawRect(progressLength, 0, width - progressLength, height, isRemove ? mRemovePaint : mProgressPaint);
        canvas.restore();
    }


    public void setMax(int max){
        this.mMax = max;
    }
    public void setProgress(int progress){
        this.mProgress = progress;
        postInvalidate();
    }


    public void setRemove(boolean isRemove){
        this.isRemove = isRemove;
    }
}