/*
 * 位置共享全局标识
 */
package com.youmai.hxsdk.module.map;

import android.util.Log;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

public class ActualLocation {
    private static final int TIMEOUT = 120 * 1000;
    private static long lastTime = 0;
    private static long startTime = 0;
    private static double sendLatitude = 0.0;
    private static double sendLongitude = 0.0;

    private static double recvLatitude = 0.0;
    private static double recvLongitude = 0.0;
    private static String talkPhone = "";

    private static long msgId = 0;
    private static boolean isRunning = false;

    private static int targetId = 0; //别人的UserId
    private static int status = -1; //位置共享的状态
    private static String lSharePhone = ""; //位置共享的对方号码

    private static CacheMsgBean inviteCacheMsgBean; //INVITE状态
    private static CacheMsgBean answerCacheMsgBean; //ANSWER状态
    private static CacheMsgBean resendCacheMsgBean; //resend状态

    public static long getMsgId() {
        return msgId;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static int getTargetId() {
        return targetId;
    }

    public static void setTargetId(int targetId) {
        ActualLocation.targetId = targetId;
    }

    public static int getStatus() {
        return status;
    }

    public static void setStatus(int status) {
        ActualLocation.status = status;
    }

    public static String getLSharePhone() {
        return lSharePhone;
    }

    public static void setLSharePhone(String lSharePhone) {
        ActualLocation.lSharePhone = lSharePhone;
    }

    public static CacheMsgBean getInviteCacheMsgBean() {
        return inviteCacheMsgBean;
    }

    public static void setInviteCacheMsgBean(CacheMsgBean inviteCacheMsgBean) {
        ActualLocation.inviteCacheMsgBean = inviteCacheMsgBean;
    }

    public static CacheMsgBean getAnswerCacheMsgBean() {
        return answerCacheMsgBean;
    }

    public static void setAnswerCacheMsgBean(CacheMsgBean answerCacheMsgBean) {
        ActualLocation.answerCacheMsgBean = answerCacheMsgBean;
    }

    public static CacheMsgBean getResendCacheMsgBean() {
        return resendCacheMsgBean;
    }

    public static void setResendCacheMsgBean(CacheMsgBean resendCacheMsgBean) {
        ActualLocation.resendCacheMsgBean = resendCacheMsgBean;
    }

    public static void onStart(long msgId, String phone) {
        ActualLocation.msgId = msgId;
        startTime = System.currentTimeMillis();
        lastTime = System.currentTimeMillis();
        ActualLocation.talkPhone = phone;
        isRunning = true;
    }

    public static void onEnd(long msgId) {
        if (msgId == 0) {
            ActualLocation.msgId = 0;
            isRunning = false;
            Log.e("ActualLocation", "onEnd msgid=" + msgId);
        } else if (ActualLocation.msgId == msgId) {
            isRunning = false;
            Log.e("ActualLocation", "onEnd msgid=" + msgId);
        } else {
            Log.e("ActualLocation", "onEnd msgid error=" + msgId);
        }
    }

}
