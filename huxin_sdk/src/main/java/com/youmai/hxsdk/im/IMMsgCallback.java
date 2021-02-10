package com.youmai.hxsdk.im;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 16:32
 * Description:
 */
public interface IMMsgCallback {

    //普通消息
    void onBuddyMsgCallback(CacheMsgBean cacheMsgBean);

    //社群消息
    void onCommunityMsgCallback(CacheMsgBean cacheMsgBean);

    //客服业主消息
    void onOwnerMsgCallback(CacheMsgBean cacheMsgBean);
}
