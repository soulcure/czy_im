package com.youmai.hxsdk.module.videoplayer.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.module.videoplayer.bean.VideoDetailInfo;
import com.youmai.hxsdk.module.videoplayer.helper.BDVideoPlayer;
import com.youmai.hxsdk.module.videoplayer.bean.IVideoInfo;
import com.youmai.hxsdk.module.videoplayer.listener.OnVideoControlListener;
import com.youmai.hxsdk.module.videoplayer.utils.NetworkUtils;
import com.youmai.hxsdk.utils.AbFileUtil;

import java.io.File;
import java.util.Locale;

/**
 * 视频控制器，可替换或自定义样式
 */
public class VideoControllerView extends FrameLayout {

    public static final int DEFAULT_SHOW_TIME = 3000;

    private View mControllerBack;
    private View mControllerTitle;
    private ImageView mVideoReplay;
    private View mControllerBottom;
    private SeekBar mPlayerSeekBar;
    private ImageView mVideoPlayState;
    private TextView mVideoProgress;
    /**
     * 时长
     */
    private TextView mVideoDuration;
    private VideoErrorView mErrorView;

    private boolean mShowing;
    private boolean mAllowUnWifiPlay;
    private BDVideoPlayer mPlayer;
    private IVideoInfo videoInfo;
    private OnVideoControlListener onVideoControlListener;

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        this.onVideoControlListener = onVideoControlListener;
    }

    public VideoControllerView(Context context) {
        super(context);
        init();
    }

    public VideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.hx_video_media_controller, this);

        initControllerPanel();
    }

    private void initControllerPanel() {
        // back
        mControllerBack = findViewById(R.id.video_back);
        mControllerBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVideoControlListener != null) {
                    onVideoControlListener.onBack();
                }
            }
        });
        // top
        mControllerTitle = findViewById(R.id.video_controller_title);

        //middle
        mVideoReplay = (ImageView) findViewById(R.id.video_replay);

        // bottom
        mControllerBottom = findViewById(R.id.video_controller_bottom);
        mPlayerSeekBar = (SeekBar) mControllerBottom.findViewById(R.id.player_seek_bar);
        mVideoPlayState = (ImageView) mControllerBottom.findViewById(R.id.player_pause);
        mVideoProgress = (TextView) mControllerBottom.findViewById(R.id.player_progress);
        mVideoDuration = (TextView) mControllerBottom.findViewById(R.id.player_duration);
        mVideoPlayState.setOnClickListener(mOnPlayerPauseClick);
        mVideoPlayState.setImageResource(R.drawable.hx_video_pause);
        mPlayerSeekBar.setOnSeekBarChangeListener(mSeekListener);

        // error
        mErrorView = (VideoErrorView) findViewById(R.id.video_controller_error);
        mErrorView.setOnVideoControlListener(new OnVideoControlListener() {
            @Override
            public void onBack() {

            }

            @Override
            public void onRetry(int errorStatus) {
                retry(errorStatus);
            }
        });

        mVideoReplay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
                mVideoReplay.setVisibility(View.GONE);
            }
        });

        mPlayerSeekBar.setMax(1000);
    }

    public void setMediaPlayer(BDVideoPlayer player) {
        mPlayer = player;
        updatePausePlay();
    }

    public void setVideoInfo(IVideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public void toggleDisplay() {
        if (mShowing) {
            hide();
        } else {
            show();
        }
    }

    public void show() {
        show(DEFAULT_SHOW_TIME);
    }

    public void show(int timeout) {
        setProgress();

        mControllerBack.setVisibility(VISIBLE);
        mControllerTitle.setVisibility(VISIBLE);
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mVideoReplay.setVisibility(VISIBLE);
        }
        mControllerBottom.setVisibility(VISIBLE);

        mShowing = true;

        updatePausePlay();

        post(mShowProgress);

        if (timeout > 0) {
            removeCallbacks(mFadeOut);
            postDelayed(mFadeOut, timeout);
        }
    }

    private void hide() {
        if (!mShowing) {
            return;
        }

        mControllerTitle.setVisibility(GONE);
        mVideoReplay.setVisibility(GONE);
        mControllerBottom.setVisibility(GONE);

        removeCallbacks(mShowProgress);

        mShowing = false;
    }

    private final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private boolean mDragging;
    private long mDraggingProgress;
    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            if (!mDragging && mShowing && mPlayer.isPlaying()) {
                int pos = setProgress();
                postDelayed(mShowProgress, 100);
            }
        }
    };

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        mPlayerSeekBar.setMax(duration);
        if (mPlayerSeekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                //long pos = 1000L * position / duration;
                mPlayerSeekBar.setProgress(position);
            }
            //int percent = mPlayer.getBufferPercentage();
            //mPlayerSeekBar.setSecondaryProgress(percent * 10);
        }

        Log.d("YW", "position: " + position + "\tduration: " + duration);
        mVideoProgress.setText(stringForTime(position, false));
        mVideoDuration.setText(stringForTime(duration, true));

        return position;
    }

    /**
     * 判断显示错误类型
     */
    public void checkShowError(boolean isNetChanged) {
        boolean isConnect = NetworkUtils.isNetworkConnected(getContext());
        boolean isMobileNet = NetworkUtils.isMobileConnected(getContext());
        boolean isWifiNet = NetworkUtils.isWifiConnected(getContext());

        if (isConnect) {
            // 如果已经联网
            if (mErrorView.getCurStatus() == VideoErrorView.STATUS_NO_NETWORK_ERROR && !(isMobileNet && !isWifiNet)) {
                // 如果之前是无网络 TODO 应该提示“网络已经重新连上，请重试”，这里暂不处理
            } else if (videoInfo == null) {
                // 优先判断是否有video数据
                showError(VideoErrorView.STATUS_VIDEO_DETAIL_ERROR);
            } else if (isMobileNet && !isWifiNet && !mAllowUnWifiPlay) {
                // 如果是手机流量，且未同意过播放，且非本地视频，则提示错误
                if (videoInfo.getVideoPath().toLowerCase().contains("http://")) {
                    mErrorView.showError(VideoErrorView.STATUS_UN_WIFI_ERROR);
                }
                mPlayer.pause();
            } else if (isWifiNet && isNetChanged && mErrorView.getCurStatus() == VideoErrorView.STATUS_UN_WIFI_ERROR) {
                // 如果是wifi流量，且之前是非wifi错误，则恢复播放
                playFromUnWifiError();
            } else if (!isNetChanged) {
                showError(VideoErrorView.STATUS_VIDEO_SRC_ERROR);
            }
        } else {
            mPlayer.pause();
            showError(VideoErrorView.STATUS_NO_NETWORK_ERROR);
        }
    }

    public void hideErrorView() {
        mErrorView.hideError();
    }

    private void reload() {
        mPlayer.restart();
    }

    public void release() {
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }

    private void retry(int status) {
        Log.i("DDD", "retry " + status);

        switch (status) {
            case VideoErrorView.STATUS_VIDEO_DETAIL_ERROR:
                // 传递给activity
                if (onVideoControlListener != null) {
                    onVideoControlListener.onRetry(status);
                }
                break;
            case VideoErrorView.STATUS_VIDEO_SRC_ERROR:
                reload();
                break;
            case VideoErrorView.STATUS_UN_WIFI_ERROR:
                allowUnWifiPlay();
                break;
            case VideoErrorView.STATUS_NO_NETWORK_ERROR:
                // 无网络时
                if (NetworkUtils.isNetworkConnected(getContext())) {
                    if (videoInfo == null) {
                        // 如果video为空，重新请求详情
                        retry(VideoErrorView.STATUS_VIDEO_DETAIL_ERROR);
                    } else if (mPlayer.isInPlaybackState()) {
                        // 如果有video，可以直接播放的直接恢复
                        mPlayer.start();
                    } else {
                        // 视频未准备好，重新加载
                        reload();
                    }
                } else {
                    Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(360000);

            mDragging = true;

            removeCallbacks(mShowProgress);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                return;
            }

            long duration = mPlayer.getDuration();
            Log.d("YW", "total: " + (duration * progress) / 1000L + "\tduration: " + duration + "\tprogress: " + progress);
            mDraggingProgress = progress;//(duration * progress) / 1000L;

            if (mVideoProgress != null) {
                mVideoProgress.setText(stringForTime((int) mDraggingProgress, false));
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mPlayer.seekTo((int) mDraggingProgress);
            play();
            mDragging = false;
            mDraggingProgress = 0;

            post(mShowProgress);
        }
    };

    private void showError(int status) {
        mErrorView.showError(status);
        hide();
    }

    private void allowUnWifiPlay() {
        Log.i("DDD", "allowUnWifiPlay");

        mAllowUnWifiPlay = true;

        playFromUnWifiError();
    }

    private void playFromUnWifiError() {
        Log.i("DDD", "playFromUnWifiError");

        // TODO: 2017/6/19 check me
        if (mPlayer.isInPlaybackState()) {
            mPlayer.start();
        } else {
            mPlayer.restart();
        }
    }

    private OnClickListener mOnPlayerPauseClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
        }
    };

    public void updatePausePlay() {
        if (mPlayer.isPlaying()) {
            mVideoPlayState.setImageResource(R.drawable.hx_video_pause);
        } else {
            mVideoPlayState.setImageResource(R.drawable.hx_video_play);
        }
    }

    public void doPauseResume() {
        if (mPlayer.isPlaying()) {
            pause();
            mVideoReplay.setVisibility(View.VISIBLE);
        } else {
            play();
            mVideoReplay.setVisibility(View.GONE);
        }
    }

    public void pause() {
        mPlayer.pause();
        updatePausePlay();
        removeCallbacks(mFadeOut);
        mVideoReplay.setVisibility(View.VISIBLE);
    }

    public void play() {
        mPlayer.start();
        show();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 播完视频监听调用
     */
    public void setSeekBarFull() {
        mPlayerSeekBar.setProgress(24000000/*mPlayer.getDuration()*/);
        mVideoReplay.setVisibility(View.VISIBLE);
        if (mPlayer != null) {
            mPlayer.seekTo(0);
            mVideoProgress.setText(stringForTime(mPlayer.getDuration(), false));
        }
    }

    public void hideRetryIcon() {
        if (mVideoReplay == null) {
            return;
        }
        mVideoReplay.setVisibility(GONE);
    }

    /**
     * Utils Method
     *
     * @param timeMs
     * @return
     */
    private String stringForTime(int timeMs, boolean is) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (totalSeconds <= 0 && is) {
            return "00:01";
        }

        if (totalSeconds <= 0 && timeMs > 500) {
            return "00:01";
        }

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    /**
     * 远程视频下载
     *
     * @param filePath
     */
    public void setVideo(final String filePath) {
        final String absolutePath = FileConfig.getVideoDownLoadPath();
        final AsyncTask<Object, Object, String> task = new AsyncTask<Object, Object, String>() {
            String lastDownload = "";
            long lastTime = 0;

            @Override
            protected String doInBackground(Object... params) {
                long now = System.currentTimeMillis();
                if (now - lastTime < 10 * 1000 && lastDownload.equals(filePath)) {
                    return null;
                }
                lastTime = now;
                lastDownload = filePath;
                return AbFileUtil.downloadFile(filePath /*+ ".mp4"*/, absolutePath);
            }

            @Override
            protected void onPostExecute(String path) {
                super.onPostExecute(path);

                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        loadLocalVideo(path);
                    }
                }
            }
        };
        task.execute();
        loadLocalVideo(filePath);
    }

    /* filePath 本地文件路径 */
    private void loadLocalVideo(String filePath) {
        mPlayer.setVideoPath(filePath);

        VideoDetailInfo info = new VideoDetailInfo();
        info.setVideoPath(filePath);
        setVideoInfo(info);
    }

}
