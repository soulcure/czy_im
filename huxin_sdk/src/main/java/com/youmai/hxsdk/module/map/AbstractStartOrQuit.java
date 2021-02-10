package com.youmai.hxsdk.module.map;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

/**
 * 作者：create by YW
 * 日期：2017.12.26 11:15
 * 描述：主动
 */

public abstract class AbstractStartOrQuit implements IStartRefreshUIListener, IQuitListener {

    @Override
    public void onRefreshUi(CacheMsgBean cacheMsgBean) {

    }

    @Override
    public void onQuit(CacheMsgBean cacheMsgBean) {

    }
}
