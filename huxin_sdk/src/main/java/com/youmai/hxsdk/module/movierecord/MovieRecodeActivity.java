package com.youmai.hxsdk.module.movierecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.ToastUtil;

public class MovieRecodeActivity extends SdkBaseActivity {

    /*
     * Const.
     */
    private static final int MSG_RECORD_FINISH = 1;

    /*
     * UI.
     */
    private MovieRecorderView mRecorderView;
    private Button mShootBtn;
    private View backBtn;
    private View changeBtn;
    private TextView tipTV;

    /*
     * Data.
     */
    private ScreenReceiver screenReceiver = null;
    private boolean isFinish = false;
    private RectF mShootBtnRectF;
    private boolean isContainShootBtn = true; //是否在shoot view里面

    private boolean isPause = false; //是否暂停
    public static MovieRecodeActivity CURRENT_ACTIVITY;//当前Activity

    /*
     * Handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECORD_FINISH:
                    finishActivity();
                    break;
            }

        }
    };

    /**
     * 转换摄像头按钮事件
     *
     * @param view
     */
    public void onChangeCamera(View view) {
        if (mRecorderView.isSupportChangeCamera()) {
            mRecorderView.changeCamera();
        } else {
            ToastUtil.showToast(this, getResources().getString(R.string.hxs_mr_change_camera_error));
        }
    }

    /**
     * 关闭摄像头按钮事件
     *
     * @param view
     */
    public void onBack(View view) {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unReqScreenREceiver();
        mRecorderView.stop();
    }

    /**
     * 完成录制
     */
    private void finishActivity() {
        if (isFinish) {
            mRecorderView.stop();
            // 返回到播放页面
            String filePath = mRecorderView.getmRecordFile().getAbsolutePath();
            LogUtils.e(Constant.SDK_DATA_TAG, "filePath = " + filePath);

        }
        isFinish = false;
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxs_mr_activity_main);

        CURRENT_ACTIVITY = this;

        tipTV = (TextView) findViewById(R.id.mr_tip_tv);
        mRecorderView = (MovieRecorderView) findViewById(R.id.mr_movieRecorderView);
        mShootBtn = (Button) findViewById(R.id.mr_shoot_button);
        backBtn = findViewById(R.id.mr_btn_back);
        changeBtn = findViewById(R.id.mr_btn_change);
        changeBtn.setVisibility(View.GONE);//隐藏前置摄像头转换按钮

        //按住拍照
        mShootBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { //按下录制
                    if (!mRecorderView.isTimerCanel) { //fixme_k:修复权限访问的bug
                        //松开取消  或者 录制时间太短
                        cancelRecord();
                        //重新打开
                        mRecorderView.openCurrentCamera();
                    }
                    isFinish = false;
                    isContainShootBtn = true;
                    //隐藏按钮
                    hideButtonLayout();
                    //显示tip
                    showRecodeTip(true);
                    //开始录制
                    mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {
                        @Override
                        public void onRecordFinish() {
                            if (!isFinish) {
                                isFinish = true;
                                handler.sendEmptyMessage(MSG_RECORD_FINISH);
                            }
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) { //按上
                    if (!isFinish) {
                        isFinish = true;
                        //显示按钮
                        displayButtonLayout();
                        //隐藏tip
                        hideRecodeTip();

                        //condition 1  made
                        if (isContainShootBtn && mRecorderView.getTimeCount() > 100) {  //超过一秒 且确定是录制
                            handler.sendEmptyMessage(MSG_RECORD_FINISH);
                            return true;
                        } else if (isContainShootBtn) {  //录制时间太短 //condition 2  time too short
                            Toast.makeText(MovieRecodeActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                        }

                        //condition 3  cancel
                        //松开取消  或者 录制时间太短
                        cancelRecord();
                        //重新打开
                        mRecorderView.openCurrentCamera();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) { //滑动
                    //判断是否向上和是否在view内
                    float x = event.getRawX();
                    float y = event.getRawY();
                    final boolean isInViewRect = mShootBtnRectF.contains(x, y);

                    if (!isInViewRect && y < mShootBtnRectF.top) { //取消
                        if (isContainShootBtn) {
                            isContainShootBtn = false;
                            showRecodeTip(false);
                        }

                    } else { //继续
                        if (!isContainShootBtn) {
                            isContainShootBtn = true;
                            showRecodeTip(true);
                        }
                    }
                }
                return true;
            }
        });
        //注册广播
        initRegScreenReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.e(Constant.SDK_DATA_TAG, "onPause" + " activity=" + isFinishing());
        if (!isFinishing() && !mRecorderView.isTimerCanel) { //是否结束activity,被暂停了,对话框打开什么的
            //condition 3  cancel
            //松开取消  或者 录制时间太短
            cancelRecord();
            //重新打开
            mRecorderView.openCurrentCamera();
        }
    }

    /**
     * 取消录制.
     */
    private void cancelRecord() {
        mRecorderView.cancelReCord();
        //删除文件
        if (mRecorderView.getmRecordFile() != null) {
            mRecorderView.getmRecordFile().delete();
            mRecorderView.stop();
        }
    }

    /**
     * 显示tip
     *
     * @param isGreen
     */
    private void showRecodeTip(boolean isGreen) {
        tipTV.setVisibility(View.VISIBLE);
        if (isGreen) {
            tipTV.setText(R.string.hxs_mr_tip_1);
            tipTV.setTextColor(getResources().getColor(R.color.hx_main_color));
        } else {
            tipTV.setText(R.string.hxs_mr_tip_2);
            tipTV.setTextColor(getResources().getColor(R.color.hxs_mr_color_red));
        }
    }

    /**
     * 隐藏tip
     */
    private void hideRecodeTip() {
        tipTV.setVisibility(View.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mShootBtnRectF = calcViewScreenLocation(mShootBtn); // 获取控件的坐标
        }
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

    /**
     * 隐藏按钮
     */
    private void hideButtonLayout() {
        backBtn.setVisibility(View.GONE);
        changeBtn.setVisibility(View.GONE);
    }

    /**
     * 显示按钮
     */
    private void displayButtonLayout() {
        backBtn.setVisibility(View.VISIBLE);
        //changeBtn.setVisibility(View.VISIBLE);//暂时屏蔽
    }

    /**
     * 注册广播
     */
    private void initRegScreenReceiver() {
        if (screenReceiver == null) {
            screenReceiver = new ScreenReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            registerReceiver(screenReceiver, filter);
        }
    }

    /**
     * 注销广播
     */
    private void unReqScreenREceiver() {
        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
        }
    }

    /**
     * 监听屏幕的广播
     */
    public class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {  //屏幕关闭
                isPause = true;
                if (!isFinish) { //还未录制完
                    isFinish = true;
                    //显示按钮
                    displayButtonLayout();
                    //隐藏tip
                    hideRecodeTip();
                    //取消录制
                    cancelRecord();
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) { //屏幕打开
                if (isPause) { //已经暂停过
                    mRecorderView.openCurrentCamera();
                    isPause = false;
                }
            }
        }
    }
}
