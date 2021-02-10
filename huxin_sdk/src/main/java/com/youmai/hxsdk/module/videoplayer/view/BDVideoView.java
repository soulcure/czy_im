package com.youmai.hxsdk.module.videoplayer.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.module.videoplayer.bean.VideoDetailInfo;
import com.youmai.hxsdk.module.videoplayer.helper.BDVideoPlayer;
import com.youmai.hxsdk.module.videoplayer.bean.IVideoInfo;
import com.youmai.hxsdk.module.videoplayer.listener.OnVideoControlListener;
import com.youmai.hxsdk.module.videoplayer.listener.SimplePlayerCallback;
import com.youmai.hxsdk.module.videoplayer.utils.NetworkUtils;
import com.youmai.hxsdk.utils.AbFileUtil;
import com.youmai.hxsdk.utils.ScreenUtils;

/**
 * 视频播放器View
 */
public class BDVideoView extends FrameLayout implements GestureDetector.OnGestureListener {

    private SurfaceView mSurfaceView;
    private View mLoading;
    private VideoControllerView mediaController;
    private BDVideoPlayer mMediaPlayer;

    private int initWidth;
    private int initHeight;

    //#
    private GestureDetector mGestureDetector;
    protected Activity activity;
    protected AudioManager am;

    public BDVideoView(Context context) {
        super(context);
        init();
    }

    public BDVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BDVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        Context context = getContext();
        if (context instanceof Activity) {
            mGestureDetector = new GestureDetector(context.getApplicationContext(), this);
            activity = (Activity) context;
            am = (AudioManager) (context.getSystemService(Context.AUDIO_SERVICE));
        } else {
            throw new RuntimeException("VideoBehaviorView context must be Activity");
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.hx_video_view, this);

        mSurfaceView = (SurfaceView) findViewById(R.id.video_surface);
        mLoading = findViewById(R.id.video_loading);
        mediaController = (VideoControllerView) findViewById(R.id.video_controller);

        initPlayer();

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initWidth = getWidth();
                initHeight = getHeight();

                if (mMediaPlayer != null) {
                    mMediaPlayer.setDisplay(holder);
                    if(mMediaPlayer.isInPlaybackState()){
                        mediaController.doPauseResume();
                    }else {
                        mMediaPlayer.openVideo();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        registerNetChangedReceiver();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private void initPlayer() {
        mMediaPlayer = new BDVideoPlayer();
        mMediaPlayer.setCallback(new SimplePlayerCallback() {

            @Override
            public void onStateChanged(int curState) {
                switch (curState) {
                    case BDVideoPlayer.STATE_IDLE:
                        am.abandonAudioFocus(null);
                        break;
                    case BDVideoPlayer.STATE_PREPARING:
                        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                        break;
                }
            }

            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaController.updatePausePlay();
                mediaController.setSeekBarFull();
            }

            @Override
            public void onError(MediaPlayer mp, int what, int extra) {
                mediaController.checkShowError(false);
            }

            @Override
            public void onLoadingChanged(boolean isShow) {
                if (isShow) showLoading();
                else hideLoading();
            }

            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
                mediaController.hideRetryIcon();
                mediaController.show();
                mediaController.hideErrorView();
            }

            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                //按HOME 键崩
                if(width==0||height==0){
                    return;
                }
                int screenWidth = ScreenUtils.getWidthPixels(getContext());
                int screenHeight = ScreenUtils.getHeightPixels(getContext());

                int w, h;

                if (width > height) {
                    w = screenWidth;
                    h = (screenWidth * height) / width;
                } else {
                    w = (width * screenHeight) / height;
                    h = screenHeight;
                }


                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mSurfaceView.setLayoutParams(params);
            }
        });
        mediaController.setMediaPlayer(mMediaPlayer);
    }

    private void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mLoading.setVisibility(View.GONE);
    }

    private boolean isBackgroundPause;

    public void onStop() {
        //if (mMediaPlayer.isPlaying()) {
            // 如果已经开始且在播放，则暂停同时记录状态
            //isBackgroundPause = true;
            mediaController.pause();
        //}
    }

    public void onStart() {
        //if (isBackgroundPause) {
            // 如果切换到后台暂停，后又切回来，则继续播放
            //isBackgroundPause = false;
            mediaController.play();
        //}
    }

    public void onDestroy() {
        mMediaPlayer.stop();
        mediaController.release();
        unRegisterNetChangedReceiver();
    }

    /**
     * 开始播放
     */
    public void startPlayVideo(final IVideoInfo video) {
        if (video == null) {
            return;
        }

        mMediaPlayer.reset();

        String videoPath = video.getVideoPath();

        final String absolutePath = FileConfig.getVideoDownLoadPath();

        if (videoPath.toLowerCase().contains("http://")) {
            String hasFile = AbFileUtil.hasFilePath(videoPath, absolutePath);
            if (AbFileUtil.isEmptyString(hasFile)) {
                mediaController.setVideo(videoPath);
            } else {
                ((VideoDetailInfo) video).setVideoPath(hasFile);
                mediaController.setVideoInfo(video);
                mMediaPlayer.setVideoPath(hasFile);
            }
        } else {
            mediaController.setVideoInfo(video);
            mMediaPlayer.setVideoPath(videoPath);
        }
    }

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        mediaController.setOnVideoControlListener(onVideoControlListener);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getLayoutParams().width = initWidth;
            getLayoutParams().height = initHeight;
        } else {
            getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
            getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mediaController.toggleDisplay();
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    private NetChangedReceiver netChangedReceiver;

    public void registerNetChangedReceiver() {
        if (netChangedReceiver == null) {
            netChangedReceiver = new NetChangedReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            activity.registerReceiver(netChangedReceiver, filter);
        }
    }

    public void unRegisterNetChangedReceiver() {
        if (netChangedReceiver != null) {
            activity.unregisterReceiver(netChangedReceiver);
        }
    }

    private class NetChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Parcelable extra = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (extra != null && extra instanceof NetworkInfo) {
                NetworkInfo netInfo = (NetworkInfo) extra;

                if (NetworkUtils.isNetworkConnected(context) && netInfo.getState() != NetworkInfo.State.CONNECTED) {
                    // 网络连接的情况下只处理连接完成状态
                    return;
                }

                mediaController.checkShowError(true);
            }
        }
    }
}
