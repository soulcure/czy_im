package com.youmai.hxsdk.module.map;

/**
 * 作者：create by YW
 * 日期：2017.12.15 13:56
 * 描述：被动
 */

public abstract class AnswerOrReject implements IAnswerOrRejectListener {

    @Override
    public void onAnswerOrReject(boolean answerOrReject, String location, int targetUserId) {

    }

    @Override
    public void onQuit() {

    }
}
