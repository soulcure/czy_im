package com.youmai.hxsdk.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.youmai.hxsdk.receiver.HuxinReceiver;


/**
 * Created by ZhouXin on 2016/8/12.
 * create for server start himself
 */
@TargetApi(21)
public class HuxinStartJobService extends JobService {

    private static final String TAG = HuxinStartJobService.class.getSimpleName();

    private static final int START_HUXIN_SERVER = 1;


    private Handler mJobHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case START_HUXIN_SERVER:
                    /*Intent intent = new Intent(getApplicationContext(), HuxinService.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(HuxinService.BOOT_SERVICE);
                    startService(intent);*/

                    Intent intent = new Intent(getApplicationContext(), HuxinReceiver.class);
                    intent.setAction(HuxinReceiver.ACTION_START_SERVICE);
                    intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    sendBroadcast(intent);

                    break;
            }
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });


    /**
     * 服务启动时系统将会调用本方法
     *
     * @param params
     * @return false表示后台设置的方法执行完毕，
     * true表示后台设置的方法未执行完毕，一般设为false即可
     */

    @Override
    public boolean onStartJob(JobParameters params) {
        Message msg = mJobHandler.obtainMessage();
        msg.what = START_HUXIN_SERVER;
        msg.obj = params;
        mJobHandler.sendMessage(msg);
        return true;

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mJobHandler.removeMessages(START_HUXIN_SERVER);
        return false;
    }
}
