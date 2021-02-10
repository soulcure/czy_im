package com.youmai.hxsdk.view.chat.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.view.chat.utils.DisplayUtils;


/**
 * Created by fylder on 2015/9/9.
 */
public class RecordingView extends View {
    private static final String TAG = RecordingView.class.getName();

    private static final int STATE_NORMAL = 0;
    private static final int STATE_PRESSED = 1;
    private static final int STATE_RECORDING = 2;

    private int mState = STATE_NORMAL;

    public static final long DTIME = 50L;


    private Paint mPaint;
    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private float mMinRadius;
    private float mMaxRadius;
    private float mCurrentRadius;


    public RecordingView(Context context) {
        this(context, null);
    }

    public RecordingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecordingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMaxRadius = Math.min(w, h) / 2;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.hx_audio_press_bg));
//        mPaint.setColor(Color.argb(180, 59, 220, 255));

        mMinRadius = DisplayUtils.dp2pxv(getContext(), 68) / 2;
        mCurrentRadius = mMinRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (mCurrentRadius > mMinRadius) {
            canvas.drawCircle(width / 2, height / 2, mCurrentRadius, mPaint);
        }

    }


    public void animateRadius(float radius) {
        if (radius <= mCurrentRadius) {
            return;
        }
        if (radius > mMaxRadius) {
            radius = mMaxRadius;
        } else if (radius < mMinRadius) {
            radius = mMinRadius;
        }
        if (radius == mCurrentRadius) {
            return;
        }
        if (mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
        mAnimatorSet.playSequentially(
                ObjectAnimator.ofFloat(this, "CurrentRadius", getCurrentRadius(), radius).setDuration(DTIME),
                ObjectAnimator.ofFloat(this, "CurrentRadius", radius, mMinRadius).setDuration(600)
        );
        mAnimatorSet.start();
    }

    public float getCurrentRadius() {
        return mCurrentRadius;
    }

    public void setCurrentRadius(float currentRadius) {
        mCurrentRadius = currentRadius;
        invalidate();
    }
}
