package com.youmai.hxsdk.module.videoplayer.listener;

public abstract class OnVideoControlListener {

    /**
     * 返回
     */
    public abstract void onBack();

    /**
     * 错误后的重试
     *
     * @param errorStatus 当前错误状态
     *                    <ul>
     *                    <li>{@link com.youmai.hxsdk.module.videoplayer.view.VideoErrorView#STATUS_NORMAL}
     *                    <li>{@link com.youmai.hxsdk.module.videoplayer.view.VideoErrorView#STATUS_VIDEO_DETAIL_ERROR}
     *                    <li>{@link com.youmai.hxsdk.module.videoplayer.view.VideoErrorView#STATUS_VIDEO_SRC_ERROR}
     *                    <li>{@link com.youmai.hxsdk.module.videoplayer.view.VideoErrorView#STATUS_UN_WIFI_ERROR}
     *                    <li>{@link com.youmai.hxsdk.module.videoplayer.view.VideoErrorView#STATUS_NO_NETWORK_ERROR}
     *                    </ul>
     */
    public abstract void onRetry(int errorStatus);

}
