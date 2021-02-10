package com.youmai.hxsdk.utils;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


import java.lang.ref.WeakReference;


public class CustomDigitalClock extends AppCompatTextView {

    private long mCountMillSecond = 0;
    private TimeHandler mTimeHandler;

    private static final int HANDLER_CLOCK = 0;

    private static final int CALL_DELAY_TIME = 8000;


    public CustomDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTimeHandler = new TimeHandler(this);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTimeHandler == null) {
            mTimeHandler = new TimeHandler(this);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /*pause();

        if (mTimeHandler != null
                && mTimeHandler.hasMessages(HANDLER_CLOCK)) {
            mTimeHandler.removeMessages(HANDLER_CLOCK);
        }
        mTimeHandler = null;*/
    }


    public void start() {
        if (mTimeHandler != null
                && !mTimeHandler.hasMessages(HANDLER_CLOCK)) {
            mTimeHandler.sendEmptyMessageDelayed(HANDLER_CLOCK, 1000);
        }
    }


    public void startDelay() {
        if (mTimeHandler != null
                && !mTimeHandler.hasMessages(HANDLER_CLOCK)) {
            mTimeHandler.sendEmptyMessageDelayed(HANDLER_CLOCK, CALL_DELAY_TIME);
        }
    }


    public void pause() {
        if (mTimeHandler != null
                && mTimeHandler.hasMessages(HANDLER_CLOCK)) {
            mTimeHandler.removeMessages(HANDLER_CLOCK);
        }
    }

    public long getCountMillSecond() {
        return mCountMillSecond;
    }

    private static class TimeHandler extends Handler {
        private final WeakReference<CustomDigitalClock> mTarget;

        TimeHandler(CustomDigitalClock target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            final CustomDigitalClock clock = mTarget.get();
            switch (msg.what) {
                case HANDLER_CLOCK:
                    clock.mCountMillSecond += 1000;
                    clock.setText(TimeUtils.getTimeFromMillisecond(clock.mCountMillSecond));
                    clock.mTimeHandler.sendEmptyMessageDelayed(HANDLER_CLOCK, 1000);
                    break;
                default:
                    break;
            }
        }

    }

}
