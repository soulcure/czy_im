package com.youmai.hxsdk.interfaces;


/**
 * 作者：create by YW
 * 日期：2016.12.27 11:40
 * 描述：
 */

public interface OnFileListener {
    void onProgress(double progress);

    void onSuccess(String fid);

    void onFail(String msg);
}
