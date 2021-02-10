package com.youmai.hxsdk.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.youmai.hxsdk.config.FileConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 将日志保存到文件
 */
public class LogFile {

    public static final boolean DEBUG = true;
    private static final int HANDLER_SAVE_LOG_TO_FILE = 1;
    private ProcessHandler mProcessHandler;

    private static LogFile instance;

    private LogFile() {
        initHandler();
    }

    public static LogFile inStance() {
        if (instance == null) {
            instance = new LogFile();
        }
        return instance;
    }


    /**
     * 将日志保存到SDCARD
     *
     * @param log
     */
    public void toFile(String log) {
        Message msg = mProcessHandler.obtainMessage(HANDLER_SAVE_LOG_TO_FILE);
        msg.obj = log;
        mProcessHandler.sendMessage(msg);
    }


    /**
     * 线程初始化
     */

    private void initHandler() {
        if (mProcessHandler == null) {
            HandlerThread handlerThread = new HandlerThread(
                    "handler looper Thread");
            handlerThread.start();
            mProcessHandler = new ProcessHandler(handlerThread.getLooper());
        }
    }

    /**
     * 子线程handler,looper
     *
     * @author Administrator
     */
    private class ProcessHandler extends Handler {

        public ProcessHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SAVE_LOG_TO_FILE:
                    String timeStamp = new SimpleDateFormat("MMdd_HHmm", Locale.CHINA).format(new Date());
                    String log = timeStamp + "  :  " + msg.obj + "\n";
                    String path = FileConfig.getLogPaths();
                    String filePath = path + "/Log.txt";

                    if (new File(filePath).length() > 20 * 1024) {
                        FileUtils.writeFile(filePath, log, false);
                    } else {
                        FileUtils.writeFile(filePath, log, true);
                    }

                    break;
                default:
                    break;
            }

        }

    }
}
