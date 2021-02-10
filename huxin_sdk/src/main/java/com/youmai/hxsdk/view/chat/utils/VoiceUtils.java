package com.youmai.hxsdk.view.chat.utils;

import android.media.MediaRecorder;
import android.util.Log;

import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.utils.LogUtils;

import java.io.File;
import java.util.Date;

/**
 * 录音工作
 * Created by fylder on 2015/9/10.
 */
public class VoiceUtils {

    private MediaRecorder mMediaRecorder;

    public static final String ERROR = "error";

    private static VoiceUtils voiceUtils;

    private String voiceFilePath;
    private String audioPath;
    private long startTime;
    private long endTime;
    private long prepareStartTime;
    private long prepareFinishTime;

    private boolean isRecording = false;


    public static VoiceUtils getInstance() {
        if (voiceUtils == null) {
            voiceUtils = new VoiceUtils();
        }
        return voiceUtils;
    }

    public VoiceUtils() {
        initPath();
    }

    public MediaRecorder getmMediaRecorder() {
        return mMediaRecorder;
    }

    private void initPath() {
        voiceFilePath = FileConfig.getAudioDownLoadPath();
        File file = new File(voiceFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 开始录音
     */
    public int startRecord() {
        int flag;

        try {
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            }

            audioPath = getVoicePath();

            prepareStartTime = System.currentTimeMillis();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setOutputFile(audioPath);
            mMediaRecorder.prepare();//此处会打开权限询问,个别系统不同，有些会在start()询问(vivo在setAudioSource就弹权限询问框)
            mMediaRecorder.start();
            prepareFinishTime = System.currentTimeMillis();
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            flag = 1;
            isRecording = true;
            if (prepareFinishTime - prepareStartTime > 500) {
                flag = 2;//允许打开语音权限后的情况
                isRecording = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("voice", "获取不到录音权限:" + e.getMessage());
            audioPath = ERROR;
            flag = -1;
            isRecording = false;
        }

        return flag;
    }

    /**
     * 结束录音
     */
    public String stopRecord() {
        isRecording = false;
        if (mMediaRecorder != null) {
            try {
                endTime = System.currentTimeMillis();
                // mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
                return audioPath;
            } catch (Exception e) {
                Log.e("123", e.toString());
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }

    }

    public boolean isRuning() {
        return isRecording;
    }

    /**
     * 创建录音文件名
     *
     * @return 文件路径
     */
    private String getVoicePath() {
        return voiceFilePath + "/audio" + new Date().getTime() + ".m4a";
    }

    /**
     * 删除录音文件
     */
    public void delete(String audioPath) {
        File file = new File(audioPath);
        if (file.exists()) {
            file.delete();
        }
    }


}
