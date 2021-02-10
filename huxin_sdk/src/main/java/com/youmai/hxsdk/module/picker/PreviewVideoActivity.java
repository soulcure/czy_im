package com.youmai.hxsdk.module.picker;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * Created by colin on 2017/6/8.
 */

public class PreviewVideoActivity extends SdkBaseActivity {

    public static final String TAG = PreviewVideoActivity.class.getSimpleName();
    private static final int UPDATE_CURRPOSITION_DELAY_TIME = 200;

    private Context mContext;

    private SeekBar mProgress;
    private VideoView mVideoView;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;

    private ImageView img_play;
    private ImageView player_pause;

    private LinearLayout linear_bar;

    private Button btn_send;

    private PlayUIHandler mUIHandler;

    private final int UI_EVENT_UPDATE_CURRPOSITION = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.item_preview_video);
        mContext = this;
        mUIHandler = new PlayUIHandler(this);
        initView();
    }

    private void initView() {
        final String path = getIntent().getStringExtra("video");
        final String playTime = getIntent().getStringExtra("time");

        final ImageView img_content = (ImageView) findViewById(R.id.img_content);
        img_play = (ImageView) findViewById(R.id.img_play);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mProgress = (SeekBar) findViewById(R.id.media_progress);


        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playTime.equals("00:00")) {
                    Toast.makeText(mContext, "视频文件已经损坏", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (HuxinSdkManager.instance().getStackAct().hasActivity(PhotoDirectoryActivity.class)) {
                    HuxinSdkManager.instance().getStackAct().finishActivity(PhotoDirectoryActivity.class);
                }

                PhotoPickerManager.getInstance().clear();
                PhotoPickerManager.getInstance().addPath(path);

                ArrayList<String> paths = PhotoPickerManager.getInstance().getPaths();
                returnData(paths);
            }
        });

        player_pause = (ImageView) findViewById(R.id.player_pause);
        player_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPauseResume();
            }
        });


        mDuration = (TextView) findViewById(R.id.time_total);
        mCurrPostion = (TextView) findViewById(R.id.time_current);
        linear_bar = (LinearLayout) findViewById(R.id.linear_bar);

        Glide.with(mContext)
                .load(path)
                .thumbnail(0.5f)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .error(R.drawable.hx_icon_rd)
                        .fitCenter())
                .into(img_content);

        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.setVisibility(View.VISIBLE);
                linear_bar.setVisibility(View.VISIBLE);

                img_play.setVisibility(View.GONE);
                img_content.setVisibility(View.GONE);

                mVideoView.setVideoPath(path);
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                img_play.setVisibility(View.GONE);

                if (!mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION)) {
                    mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
                }
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.setVisibility(View.GONE);
                linear_bar.setVisibility(View.GONE);

                img_play.setVisibility(View.VISIBLE);
                img_content.setVisibility(View.VISIBLE);
            }
        });

        registerCallbackForControl();
    }


    private void doPauseResume() {
        if (mVideoView.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    private void pause() {
        mVideoView.pause();
        player_pause.setImageResource(R.drawable.hx_video_play);
        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
    }

    private void play() {
        mVideoView.start();
        player_pause.setImageResource(R.drawable.hx_video_pause);

        if (!mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION))
            mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
    }


    /**
     * 为控件注册回调处理函数
     */
    private void registerCallbackForControl() {
        mProgress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                        || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {

                        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);

                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        int iseekPos = ((SeekBar) v).getProgress();

                        /**
                         * SeekBark完成seek时执行seekTo操作并更新界面
                         *
                         */
                        seekPos(iseekPos);
                        Log.v(TAG, "seek to " + iseekPos);
                        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
                    }
                }
                return false;
            }
        });

        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (!fromUser) { //fromUser == false //表示为系统更新进度条
                    return;
                }

                int curPlayTime = getCurrentPosition();

                //SeekBark完成seek时,执行seekTo操作
                int iseekPos = seekBar.getProgress();
                Log.v(TAG, "seek to " + iseekPos);

                seekPos(iseekPos);
                //SeekBark完成seek时, 更新界面
                updateTextViewWithTimeFormat(mCurrPostion, progress);


            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                /**
                 * SeekBar开始，seek时停止更新
                 */
                mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                /**
                 * SeekBar结束，seek开始更新
                 */
                mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
            }
        });

    }

    private void updateTextViewWithTimeFormat(TextView view, int second) {
        second /= 1000;
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        view.setText(strTemp);
    }


    private int getCurrentPosition() {
        return mVideoView.getCurrentPosition();
    }

    private int getDuration() {
        return mVideoView.getDuration();
    }


    private void seekPos(int pos) {
        mVideoView.seekTo(pos);
    }


    private void returnData(ArrayList<String> paths) {
        if (HuxinSdkManager.instance().getStackAct().hasActivity(PhotoPreviewActivity.class)) {
            HuxinSdkManager.instance().getStackAct().finishActivity(PhotoPreviewActivity.class);
        }

        Intent intent = new Intent();
        intent.putStringArrayListExtra(PhotoPreviewActivity.KEY_SELECTED_MEDIA, paths);
        setResult(RESULT_OK, intent);
        finish();
    }


    private class PlayUIHandler extends Handler {
        private final WeakReference<PreviewVideoActivity> mTarget;

        PlayUIHandler(PreviewVideoActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 更新进度及时间
                 */
                case UI_EVENT_UPDATE_CURRPOSITION:
                    int currPosition = getCurrentPosition();
                    int duration = getDuration();

                    updateTextViewWithTimeFormat(mCurrPostion, currPosition);
                    updateTextViewWithTimeFormat(mDuration, duration);
                    mProgress.setMax(duration);
                    mProgress.setProgress(currPosition);

                    int percent = mVideoView.getBufferPercentage();
                    int second = (duration * percent) / 100;
                    mProgress.setSecondaryProgress(second);

                    if (!mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION)) {
                        mUIHandler.sendEmptyMessageDelayed(
                                UI_EVENT_UPDATE_CURRPOSITION, UPDATE_CURRPOSITION_DELAY_TIME);
                    }
                    break;

                default:
                    break;
            }
        }

    }
}
