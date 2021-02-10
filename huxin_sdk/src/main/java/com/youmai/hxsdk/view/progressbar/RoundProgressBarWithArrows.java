package com.youmai.hxsdk.view.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.youmai.hxsdk.R;

/**
 * Created by yw on 2016/6/23.
 */
public class RoundProgressBarWithArrows extends HorizontalProgressBar {
    private int mRadius = dpToPx(25);

    private int mMaxPaintWidth;

    private Bitmap mArrowsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hx_show_progress_arrows);

    public RoundProgressBarWithArrows(Context context) {
        this(context, null);
    }

    public RoundProgressBarWithArrows(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBarWithArrows(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttributeValue(attrs);
        mReachHeight = mUnreachHeight * 2;

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private RectF mRectF = new RectF(0, 0, mRadius * 2, mRadius * 2);

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //移动坐标到左上角   左上角的位置为除去padding和画笔宽度的一半
        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop() + mMaxPaintWidth / 2);
        //画unReachBar
        mPaint.setColor(mUnreachColor);
        mPaint.setStrokeWidth(mUnreachHeight + 3);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        //画reachBar
        //计算要画出的角度，度数的百分比乘以360
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        //画圆  第一个参数是圆的外接正方形，第二三个参数分别为其实度数以及要画的度数，第四个参数表示是否过圆心
        canvas.drawArc(mRectF, 0, sweepAngle, false, mPaint);
        //第一个参数是图片，第二、三个参数是画文字的左上角的坐标（mRadius是圆心,圆心的左上角的横坐标就是所有文字的宽度/2，高度类似）
        canvas.drawBitmap(mArrowsBitmap, mRadius / 1.5f, mRadius /2, null);
        canvas.restore();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMaxPaintWidth = Math.max(mReachHeight, mUnreachHeight);
        //直径(我们根据用户输入的半径算得的直径)
        int expect = mRadius * 2 + mMaxPaintWidth + getPaddingRight() + getPaddingLeft();
        //将我们的直径传入，根据父控件告诉我们的模式（我自己的模式），算出来我真正的高度和宽度
        int width = resolveSize(expect, widthMeasureSpec);
        int height = resolveSize(expect, heightMeasureSpec);
        //从两者中选出一个小的，作为直径
        int realWidth = Math.min(width, height);
        //直径减去左右边距，减去画笔宽度（画笔在直径上有两个/边，但是直径是在画笔的中间的，也就是画笔画出来的线其实是横跨在直径的那条线上的），最后除2，算出真实的半径
        mRadius = (realWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;

        setMeasuredDimension(realWidth, realWidth);
    }

    private void obtainAttributeValue(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RoundProgressBarWithProgress);
        mRadius = (int) ta.getDimension(R.styleable.RoundProgressBarWithProgress_progress_radius, mRadius);
        ta.recycle();
    }
}
