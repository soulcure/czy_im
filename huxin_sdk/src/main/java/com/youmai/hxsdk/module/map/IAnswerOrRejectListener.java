package com.youmai.hxsdk.module.map;

/**
 * 作者：create by YW
 * 日期：2017.12.13 10:42
 * 描述：
 */

public interface IAnswerOrRejectListener {
    void onAnswerOrReject(boolean answerOrReject, String location, int targetUserId);
    void onQuit();
}
