package com.youmai.hxsdk.module.map;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

/**
 * 作者：create by YW
 * 日期：2017.12.26 18:34
 * 描述：第一次接收IM : 位置共享
 */

public interface IReceiveStartListener {
    void onStartLShare(CacheMsgBean cacheMsgBean);
}
