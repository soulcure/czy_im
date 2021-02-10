package com.youmai.hxsdk.im.voice.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.utils.ToastUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by admin on 10/13/16.
 */
public class VoiceManager {

    private static final String TAG = VoiceManager.class.getSimpleName();

    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private boolean isPrepared = false;

    public static boolean isNull = false;

    private static VoiceManager mInstance;

    private VoiceManager(String dir) {
        this.mDir = dir;
    }

    /**
     * Callback Interface
     */
    public interface AudioStateListener {

        void wellPrepared();
    }
    public AudioStateListener mListener;
    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    /**
     * Singleton Pattern
     * @return
     */
    public static VoiceManager getInstance(String mDir) {
        if (mInstance != null) return mInstance;
        synchronized (AudioManager.class) {
            if (mInstance == null) {
                mInstance = new VoiceManager(mDir);
            }
        }
        return mInstance;
    }

    public void prepareAudio(Context context) {
        isPrepared = false;

        mDir = FileConfig.getAudioDownLoadPath();

        File dir = new File(mDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String fileName = generateFileName();
        File file = new File(dir, fileName);

        mCurrentFilePath = file.getAbsolutePath();
        mMediaRecorder = new MediaRecorder();
        //设置输出文件
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        //设置音频源为麦克风
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置音频格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //设置音频编码
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepared = true;
            isNull = false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(context, context.getResources().getString(R.string.hx_voice_manager_tip));
            isNull = true;
            cancel();
        }

        if (mListener != null) {
            // callback method
            mListener.wellPrepared();
        }
    }

    /**
     * 随机生成文件名称
     * @return
     */
    private String generateFileName() {
        return System.currentTimeMillis() + ".aac";
    }

    /**
     *
     * @param maxLevel
     * @return volume level 1-7
     */
    public int getVolume(int maxLevel) {
        if(isPrepared)
        {
            try {
                //mMediaRecorder.getMaxAmplitude() 1-32767
                return ((mMediaRecorder.getMaxAmplitude() * maxLevel) / 32768) + 1;
            } catch (Exception e) {
            }
        }
        return 1;
    }

    public void release() {
        if(mMediaRecorder == null) return;

        try {
            mMediaRecorder.stop();
            mMediaRecorder.release();
        } catch (Exception e) {
            android.util.Log.e(TAG, e.toString());
        }
        mMediaRecorder = null;
    }

    /**
     * 删除录音文件
     */
    public void cancel() {
        release();
        if(mCurrentFilePath == null) return;

        File file = new File(mCurrentFilePath);
        if(file.exists())
        {
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public String getCurFilePath() {
        return mCurrentFilePath;
    }
}
