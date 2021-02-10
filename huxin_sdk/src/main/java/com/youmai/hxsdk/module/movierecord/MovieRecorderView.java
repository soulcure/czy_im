package com.youmai.hxsdk.module.movierecord;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.utils.ArrayUtils;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 视频播放控件
 *
 * @author liuyinjun
 * @date 2015-2-5
 */
public class MovieRecorderView extends LinearLayout implements OnErrorListener {

    /*
     * Const.
     */
    private static final int TYPE_FRONT = 1;
    private static final int TYPE_BACK = 2;

    /*
     * UI.
     */
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressView mProgressBar;

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;

    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口

    /*
     * Data.
     */
    private int mWidth;// 视频分辨率宽度
    private int mHeight;// 视频分辨率高度
    private final boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private File mRecordFile = null;// 文件
    private int currentCameraType = TYPE_FRONT; //当前摄像头   1代表前置   2代表后置
    private Timer mTimer;// 计时器
    public boolean isTimerCanel = true; //计时器取消

    private Context mContext;


    //建议视频尺寸
    private List<Camera.Size> suggestSizeList = new ArrayList<>();
    private Camera.Size suggestSize = null;

    public MovieRecorderView(Context context) {
        this(context, null);
    }

    public MovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        // 初始化各项组件
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.hxs_mr_MovieRecorderView, defStyle, 0);

        //mWidth = a.getInteger(R.styleable.hxb_mr_MovieRecorderView_video_width, 640);// 默认320
        //mHeight = a.getInteger(R.styleable.hxb_mr_MovieRecorderView_video_height, 360);// 默认240
        mWidth = a.getInteger(R.styleable.hxs_mr_MovieRecorderView_video_width, 1280);// 默认320
        mHeight = a.getInteger(R.styleable.hxs_mr_MovieRecorderView_video_height, 720);// 默认240
        isOpenCamera = a.getBoolean(R.styleable.hxs_mr_MovieRecorderView_is_open_camera, true);// 默认打开
        mRecordMaxTime = a.getInteger(R.styleable.hxs_mr_MovieRecorderView_record_max_time, 6 * 100);// 默认为10

        LayoutInflater.from(context).inflate(R.layout.hxs_mr_movie_recorder_view, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mProgressBar = (ProgressView) findViewById(R.id.progressBar);
        mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量

        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.GONE);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        a.recycle();

    }

    /**
     * 回调
     */
    private class CustomCallBack implements Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isOpenCamera) {
                return;
            }
            initCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera) {
                return;
            }
            //释放摄像头
            freeCameraResource();
        }
    }

    /**
     * 初始化摄像头
     */
    public void initCamera() {
        //释放资源
        if (mCamera != null) {
            freeCameraResource();
        }
        try {
            //初始化摄像头
            mCamera = openCarmera(TYPE_BACK);
            currentCameraType = TYPE_BACK;
            LogUtils.e(Constant.SDK_UI_TAG, "init初始化当前摄像头为后置");
            //设置自动对焦
            setFocusParams();
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            mCamera.unlock();
        } catch (Exception e) {
            LogUtils.e(Constant.SDK_UI_TAG, "initCamera=" + e.toString());
            freeCameraResource();
            if(MovieRecodeActivity.CURRENT_ACTIVITY != null) {
                MovieRecodeActivity.CURRENT_ACTIVITY.finish();
                ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.hx_camera_record_tip));
            }
        }

        if (mCamera == null) {
            return;
        }
    }


    /**
     * 设置自动对焦
     */
    private void setFocusParams() {
        Camera.Parameters parameters = mCamera.getParameters();
        suggestSizeList = parameters.getSupportedVideoSizes();
        LogUtils.e(Constant.SDK_UI_TAG, "sizelist=" + suggestSizeList);
        handleVideoSize();

        try {
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
                mCamera.setParameters(parameters);
                mCamera.cancelAutoFocus();
            }
        } catch (Exception e) {
            LogUtils.e(Constant.SDK_UI_TAG, e.getMessage().toString());
        }
    }

    /**
     * 处理视频尺寸
     */
    private void handleVideoSize() {
        /* 匹配策略:  1.优先找w=1280  h=720
         *        2.找不到的情况下，找w=640 h=360
         *        3.找不到的情况下, 找w=640 h=480
         *        4.找不到的情况下, 找w=480 h=320
         *        5.找不到的情况下, 用默认.
         */
        LogUtils.e(Constant.SDK_UI_TAG, "选择size list");
        if (!ArrayUtils.isEmpty(suggestSizeList)) {
            Map<String, Camera.Size> sizeMap = new HashMap<>();
            for (Camera.Size size : suggestSizeList) {
                sizeMap.put(size.width + "_" + size.height, size);
                LogUtils.e(Constant.SDK_UI_TAG, "size w=" + size.width + " h=" + size.height);
            }
            if (sizeMap.containsKey("1280_720")) {  //16:9
                suggestSize = sizeMap.get("1280_720");
            } else if (sizeMap.containsKey("640_360")) { //16:9
                suggestSize = sizeMap.get("640_360");
            } else if (sizeMap.containsKey("640_480")) { //4:3
                suggestSize = sizeMap.get("640_480");
            } else if (sizeMap.containsKey("480_320")) { //42:1
                suggestSize = sizeMap.get("480_320");
            } else {
                suggestSize = suggestSizeList.get(0);
            }
        } else {
            LogUtils.e(Constant.SDK_UI_TAG, "size list 为空");
        }
    }

    /**
     * 是否支持切换摄像头
     *
     * @return
     */
    public boolean isSupportChangeCamera() {
        return Camera.getNumberOfCameras() > 1;
    }

    /**
     * 切换摄像头
     *
     * @throws IOException
     */
    public void changeCamera() {
        if (!isOpenCamera) {
            return;
        }

        //释放资源
        if (mCamera != null) {
            freeCameraResource();
        }

        try {
            if (currentCameraType == TYPE_FRONT) {
                LogUtils.e(Constant.SDK_UI_TAG, "ch当前摄像头切换为后置");
                mCamera = openCarmera(TYPE_BACK);
            } else if (currentCameraType == TYPE_BACK) {
                LogUtils.e(Constant.SDK_UI_TAG, "ch当前摄像头切换为前置");
                mCamera = openCarmera(TYPE_FRONT);
            }

            //初始化摄像头
            setFocusParams();

            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            mCamera.unlock();
        } catch (Exception e) {
            freeCameraResource();
        }

        if (mCamera == null) {
            return;
        }
    }

    /**
     * 打开当前摄像头.
     */
    public void openCurrentCamera() {
        if (!isOpenCamera) {
            return;
        }

        //释放资源
        if (mCamera != null) {
            freeCameraResource();
        }

        try {
            if (currentCameraType == TYPE_FRONT) {
                LogUtils.e(Constant.SDK_UI_TAG, "oc当前摄像头为前置");
                mCamera = openCarmera(TYPE_FRONT);
            } else if (currentCameraType == TYPE_BACK) {
                mCamera = openCarmera(TYPE_BACK);
                LogUtils.e(Constant.SDK_UI_TAG, "oc当前摄像头为后置");
            }

            //初始化摄像头
            setFocusParams();

            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            mCamera.unlock();
        } catch (Exception e) {
            freeCameraResource();
        }

        if (mCamera == null) {
            return;
        }
    }

    /**
     * 打开摄像头
     *
     * @param type
     */
    private Camera openCarmera(int type) {
        int frontIndex = -1;
        int backIndex = -1;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        LogUtils.e(Constant.SDK_UI_TAG, "当前摄像头个数=" + cameraCount);
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontIndex = i;
                LogUtils.e(Constant.SDK_UI_TAG, "前置摄像机 or=" + cameraInfo.orientation);
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backIndex = i;
                LogUtils.e(Constant.SDK_UI_TAG, "后置摄像机 or=" + cameraInfo.orientation);
            }
        }

        currentCameraType = type;
        Camera camera = null;
        if (type == TYPE_BACK && backIndex != -1) {
            camera = Camera.open(backIndex);
            setCameraDisplayOrientation(((Activity) getContext()), backIndex, camera);
        } else if (type == TYPE_FRONT && frontIndex != -1) {
            camera = Camera.open(frontIndex);
            setCameraDisplayOrientation(((Activity) getContext()), frontIndex, camera);
        }
        return camera;
    }


    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        LogUtils.e(Constant.SDK_UI_TAG, "摄像头旋转=" + result);
        camera.setDisplayOrientation(result);
    }


    /**
     * 释放摄像头资源
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void freeCameraResource() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.lock();
                mCamera.release();
            } catch (Exception e) {
                LogUtils.e(Constant.SDK_UI_TAG, "freeCameraResource=" + e.getMessage().toString());
            }
            mCamera = null;
        }
    }

    /**
     * 开始创建文件目录
     */
    private void createRecordDir() {

        final File recordDir = new File(FileConfig.getVideoDownLoadPath());
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }

        // 创建文件
        try {
            mRecordFile = File.createTempFile("recording", ".mp4", recordDir); //mp4格式
            LogUtils.e(Constant.SDK_UI_TAG, mRecordFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     *
     * @throws IOException
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void initRecord() throws IOException {
        if (suggestSize != null) {
            mWidth = suggestSize.width;
            mHeight = suggestSize.height;
        }

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        LogUtils.e(Constant.SDK_UI_TAG, "录制视频参数 分辨率 w=" + mWidth + " h=" + mHeight + " 文件路径=" + mRecordFile.getAbsolutePath());
        if (mCamera != null)
            mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setVideoSource(VideoSource.CAMERA);// 视频源
        //mMediaRecorder.setAudioSource(AudioSource.MIC);// 音频源
        if (!"HTC T328d".equals(Build.MODEL)) {
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
        } else {
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        }

        //mMediaRecorder.setAudioEncoder(AudioEncoder.AMR_NB);// 音频格式
        if (mWidth > 0 && mHeight > 0) {
            mMediaRecorder.setVideoSize(640, 480);// 设置分辨率：///640, 480
        }

        if (suggestSize != null) {
            mMediaRecorder.setVideoEncodingBitRate(1 * suggestSize.width * suggestSize.height);// 设置帧频率，然后就清晰了
        } else {
            mMediaRecorder.setVideoEncodingBitRate(1 * 1024 * 512);// 设置帧频率，然后就清晰了
        }

        if (currentCameraType == TYPE_BACK) { //后置
            mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        } else {
            String model = Build.MODEL;
            LogUtils.e(Constant.SDK_SCREEN_LOG, "DOOV A6 = " + model);
            if (model.equals("DOOV A6")) { // 朵唯
                mMediaRecorder.setOrientationHint(270);// 输出旋转270度，保持竖屏录制
            } else {
                mMediaRecorder.setOrientationHint(90);// 输出旋转270度，保持竖屏录制
            }
        }

        //mMediaRecorder.setVideoFrameRate(16);// 这个我把它去掉了，感觉没什么用
        mMediaRecorder.setVideoEncoder(VideoEncoder.H264);// 视频录制格式
        mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
        mMediaRecorder.prepare();
        try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            LogUtils.e(Constant.SDK_UI_TAG, "录制视频错误1=" + e.toString());
        } catch (RuntimeException e) {
            e.printStackTrace();
            LogUtils.e(Constant.SDK_UI_TAG, "录制视频错误2=" + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(Constant.SDK_UI_TAG, "录制视频错误3=" + e.toString());
        }
    }

    /**
     * 开始录制视频
     *
     * @param onRecordFinishListener 达到指定时间之后回调接口
     * @author liuyinjun
     * @date 2015-2-5
     */
    public void record(final OnRecordFinishListener onRecordFinishListener) {
        this.mOnRecordFinishListener = onRecordFinishListener;
        createRecordDir();
        try {
            if (!isOpenCamera) {// 如果未打开摄像头，则打开
                initCamera();
            }
            initRecord();
            startChangeProgress();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始改变进度栏
     */
    private void startChangeProgress() {
        mTimeCount = 0;// 时间计数器重新赋值
        isTimerCanel = false;
        mTimer = new Timer();
        mProgressBar.setVisibility(View.VISIBLE);

        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (!isTimerCanel) {
                    mTimeCount += 1;
                    mProgressBar.setProgress(mTimeCount);// 设置进度条

                    if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop();
                        if (mOnRecordFinishListener != null) {
                            mOnRecordFinishListener.onRecordFinish();
                        }
                    }
                } else {  //已经取消
                    stop();
                }
            }
        }, 0, 10);
    }

    /**
     * 取消progress.
     */
    public void cancelReCord() {
        isTimerCanel = true;
        if (mTimer != null) {
            mTimer.cancel();
        }
        post(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 停止拍摄
     */
    public void stop() {
        //progress 隐藏
        cancelReCord();

        //取消timer
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    /**
     * 停止录制
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    public void stopRecord() {
        mProgressBar.setProgress(0);
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.stop();
                mMediaRecorder.setPreviewDisplay(null);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放资源
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    public int getTimeCount() {
        return mTimeCount;
    }

    /**
     * @return the mVecordFile
     */
    public File getmRecordFile() {
        return mRecordFile;
    }

    /**
     * 录制完成回调接口
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    public interface OnRecordFinishListener {
        void onRecordFinish();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}