package com.youmai.hxsdk;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by colin on 2017/1/9.
 */

public class StackAct {


    private static StackAct instance;

    private static Stack<Activity> mActivityStack;


    private StackAct() {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
    }


    public static StackAct instance() {
        if (instance == null) {
            instance = new StackAct();
        }
        return instance;
    }


    /**
     * add Activity 添加Activity到栈
     */
    public synchronized void addActivity(Activity activity) {
        synchronized (mActivityStack) {
            mActivityStack.add(activity);
        }
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (!mActivityStack.empty()) {
            return mActivityStack.lastElement();
        } else {
            return null;
        }
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = mActivityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 退
     */
    public synchronized void removeActivity(Activity activity) {
        if (activity != null && mActivityStack.contains(activity)) {
            mActivityStack.remove(activity);
        }
    }

    /**
     * 结束指定的Activity
     */
    public synchronized void finishActivity(Activity activity) {
        if (activity != null && mActivityStack.contains(activity)) {
            mActivityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public synchronized boolean hasActivity(Class<?> cls) {
        boolean res = false;
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                res = true;
                break;
            }
        }
        return res;
    }


    /**
     * 结束指定类名的Activity
     */
    public synchronized boolean hasActivity(String clsName) {
        boolean res = false;
        for (Activity activity : mActivityStack) {
            if (activity.toString().contains(clsName)) {
                res = true;
                break;
            }
        }
        return res;
    }


    /**
     * 结束指定类名的Activity
     */
    public synchronized void finishActivity(Class<?> cls) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public synchronized void finishAllActivity() {
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 结束所有Activity
     * <p>
     * type = 1 除了HookStrategyActivity、MainAct、IMConnectionActivity、IMGroupActivity
     * type = other 除了HookStrategyActivity、MainAct
     */
    public synchronized void finishAll(int type) {
        try {
            for (int i = 0, size = mActivityStack.size(); i < size; i++) {
                if (null != mActivityStack.get(i)) {
                    if (type == 0x0001) {
                        if (mActivityStack.get(i).toString().contains("FileManagerActivity")
                                || mActivityStack.get(i).toString().contains("FileDownloadActivity")
                                || mActivityStack.get(i).toString().contains("FileDLClassifyActivity")) {
                            mActivityStack.get(i).finish();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
    }

}
