package com.youmai.hxsdk.view.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.youmai.hxsdk.R;

/**
 * Created by yw on 16/6/20.
 */
public class HorizontalProgressBar extends ProgressBar {
    private static final int DEFAULT_TEXT_SIZE = 13;
    private static final int DEFAULT_TEXT_COLOR = 0xffFC00D1;
    private static final int DEFAULT_Color_UNREACH = 0xFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 2;
    private static final int DEFAULT_COLOR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 2;
    private static final int DEFAULT_TEXT_OFFSET = 13;

    protected int mTextSize = spToPx(DEFAULT_TEXT_SIZE);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mUnreachHeight = dpToPx(DEFAULT_HEIGHT_UNREACH);
    protected int mUnreachColor = DEFAULT_Color_UNREACH;
    protected int mReachHeight = dpToPx(DEFAULT_HEIGHT_REACH);
    protected int mReachColor = DEFAULT_COLOR_REACH;
    protected int mTextOffset = dpToPx(DEFAULT_TEXT_OFFSET);

    protected Paint mPaint = new Paint();

    private int mRealWidth;

    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyleAttrs(attrs);
        mPaint.setTextSize(mTextSize);
    }

    /**
     * 获得自定义参数
     *
     * @param attrs
     */
    private void obtainStyleAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar);
        mTextColor = ta.getColor(R.styleable.HorizontalProgressBar_progress_text_color, mTextColor);
        mTextSize = (int) ta.getDimension(R.styleable.HorizontalProgressBar_progress_text_size, mTextSize);
        mReachColor = ta.getColor(R.styleable.HorizontalProgressBar_progress_reach_color, mReachColor);
        mReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBar_progress_reach_height, mReachHeight);
        mUnreachColor = ta.getColor(R.styleable.HorizontalProgressBar_progress_unreach_color, mUnreachColor);
        mUnreachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBar_progress_unreach_height, mUnreachHeight);
        ta.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthSize, height);

        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        boolean noNeedUnreach = false;
        //draw reach bar
        float radio = getProgress() * 1.0f / getMax();
        float progressX = radio * mRealWidth;
        String text = getProgress() + "%";
        float textWidth = mPaint.measureText(text);
        if (textWidth + progressX > mRealWidth) {
            progressX = mRealWidth - textWidth;
            noNeedUnreach = true;
        }
        float endX = progressX - mTextOffset / 2;
        if (endX > 0) {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }
        //draw text
        mPaint.setColor(mTextColor);
        mPaint.setStrokeWidth(mTextSize);
        int y = (int) (Math.abs(mPaint.descent() + mPaint.ascent()) / 2) ;
        canvas.drawText(text, progressX, y, mPaint);

        //draw unreach bar
        if (!noNeedUnreach) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnreachColor);
            mPaint.setStrokeWidth(mUnreachHeight);
            canvas.drawLine(Math.min(start, mRealWidth), 0, mRealWidth, 0, mPaint);
        }

        canvas.restore();

    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int textSize = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingTop() + getPaddingBottom() + Math.max(Math.max(mUnreachHeight, mReachHeight), Math.abs(textSize));
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(size, result);
            }
        }
        return result;
    }

    protected int spToPx(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }

    protected int dpToPx(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }
}
