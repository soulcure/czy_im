package com.youmai.hxsdk.module.videoplayer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.videoplayer.bean.VideoDetailInfo;
import com.youmai.hxsdk.module.videoplayer.listener.OnVideoControlListener;
import com.youmai.hxsdk.module.videoplayer.view.BDVideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    private BDVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_player_video);

        VideoDetailInfo info = (VideoDetailInfo) getIntent().getSerializableExtra("info");

        videoView = (BDVideoView) findViewById(R.id.vv);
        videoView.setOnVideoControlListener(new OnVideoControlListener() {

            @Override
            public void onRetry(int errorStatus) {
                // get info and call method "videoView.startPlayVideo(info);"
            }

            @Override
            public void onBack() {
                onBackPressed();
            }

        });
        videoView.startPlayVideo(info);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        videoView.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        videoView.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        videoView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
