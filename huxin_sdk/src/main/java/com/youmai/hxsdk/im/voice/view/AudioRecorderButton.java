package com.youmai.hxsdk.im.voice.view;

import android.content.Context;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.im.voice.manager.DialogManager;
import com.youmai.hxsdk.im.voice.manager.VoiceManager;


public class AudioRecorderButton extends AppCompatImageView implements VoiceManager.AudioStateListener {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_RECORDING = 1;  //正在录音
    private static final int STATE_WANT_TO_CANCEL = -1; //取消录取

    private int mCurState = STATE_NORMAL;

    private boolean isRecording = false;

    private DialogManager mDialogManager;
    private VoiceManager mAudioManager;

    private float mTime;

    private boolean mReady;


    //cotain
    private boolean isContainVoiceBtn = true; //是否在shoot view里面

    private boolean isMeasureVoiceBtn = false;

    private RectF mVoiceBtnRectF;

    private static final int MAX_SECOND = 60;

    private int btnBgRes = R.drawable.hx_im_btn_voice;

    private int btnBgOnRes = R.drawable.hx_im_btn_voice_on;

    public interface OnTimeCallback {
        void onTimeDisplay(float time);

        void onResetUI();
    }

    private OnTimeCallback onTimeCallback = null;


    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(final Context context, AttributeSet attrs) {
        super(context, attrs);

        mDialogManager = new DialogManager(context);
        String dir = Environment.getExternalStorageDirectory() + "/Audios";
        mAudioManager = VoiceManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mReady = true;
                mAudioManager.prepareAudio(context);
                return false;
            }
        });
    }

    /**
     * 录音完成后回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filepath);
    }
    private AudioFinishRecorderListener mListener;
    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        this.mListener = listener;
    }

    /**
     * 获取音量大小的Runnable
     */
    private Runnable mGetVolumeRunnable = new Runnable() {
        @Override
        public void run() {
            while(isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    if (mTime < MAX_SECOND) {
                        mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                    } else {
                        isRecording = false;
                        mHandler.sendEmptyMessage(MSG_SEND);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGE = 0X111;
    private static final int MSG_DIALOG_DISMISS = 0X112;
    private static final int MSG_SEND = 0X113;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    isRecording = true;
                    mDialogManager.showRecordingDialog();
                    new Thread(mGetVolumeRunnable).start();
                    break;

                case MSG_VOICE_CHANGE:
                    mDialogManager.updateVolume(mAudioManager.getVolume(7));
                    if (onTimeCallback != null) {
                        if (isRecording) {
                            onTimeCallback.onTimeDisplay(mTime);
                        }
                    }
                    break;

                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
                case MSG_SEND:
                    mDialogManager.dismissDialog();
                    mAudioManager.release();
                    if (mListener != null) {
                        mListener.onFinish(mTime, mAudioManager.getCurFilePath());
                    }
                    reset();
                    break;
            }
        }
    };

    public OnTimeCallback getOnTimeCallback() {
        return onTimeCallback;
    }

    public void setOnTimeCallback(OnTimeCallback onTimeCallback) {
        this.onTimeCallback = onTimeCallback;
    }

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                if (!isMeasureVoiceBtn) {
                    isMeasureVoiceBtn = true;
                    mVoiceBtnRectF = calcViewScreenLocation(this); // 获取控件的坐标
                }
                changeState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    final boolean isInViewRect = mVoiceBtnRectF.contains(x, y);

                    if (!isInViewRect && y < mVoiceBtnRectF.top) { //取消
                        if (isContainVoiceBtn) {
                            isContainVoiceBtn = false;
                            changeState(STATE_WANT_TO_CANCEL);
                        }
                    } else { //继续
                        if (!isContainVoiceBtn) {
                            isContainVoiceBtn = true;
                            changeState(STATE_RECORDING);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:

                if (!isRecording) {  //已经完成
                    break;
                }

                if (!isRecording || mTime < 0.6f) {
                    Toast.makeText(getContext(), "录音时间过短", Toast.LENGTH_SHORT).show();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1300);
                    if (onTimeCallback != null) {
                        onTimeCallback.onResetUI();
                    }
                } else if (mCurState == STATE_RECORDING) { //正常录制结束
                    mDialogManager.dismissDialog();
                    mAudioManager.release();
                    if (mListener != null) {
                        mListener.onFinish(mTime, mAudioManager.getCurFilePath());
                    }
                } else if (mCurState == STATE_WANT_TO_CANCEL) {
                    mDialogManager.dismissDialog();
                    mAudioManager.cancel();
                    if (onTimeCallback != null) {
                        onTimeCallback.onResetUI();
                    }
                }

                reset();
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 计算指定的 View 在屏幕中的坐标。
     */
    private RectF calcViewScreenLocation(View view) {
        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
    }




    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
        mTime = 0;
        mReady = false;
    }

    private void changeState(int state) {
        if (mCurState == state) return;

        mCurState = state;

        switch (state) {

            case STATE_NORMAL:
                setImageResource(btnBgRes);
                break;

            case STATE_RECORDING:
                setImageResource(btnBgOnRes);

                if (isRecording) {
                    mDialogManager.recording();
                }
                break;

            case STATE_WANT_TO_CANCEL:
                setImageResource(btnBgRes);
                mDialogManager.cancel();
                break;
        }
    }


    public void setBtnBgOnRes(int btnBgOnRes) {
        this.btnBgOnRes = btnBgOnRes;
    }

    public void setBtnBgRes(int btnBgRes) {
        this.btnBgRes = btnBgRes;
    }
}
