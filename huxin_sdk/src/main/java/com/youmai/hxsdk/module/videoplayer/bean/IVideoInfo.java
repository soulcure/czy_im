package com.youmai.hxsdk.module.videoplayer.bean;

import java.io.Serializable;

/**
 * 视频数据类请实现本接口
 */
public interface IVideoInfo extends Serializable {

    /**
     * 视频标题
     */
    String getVideoTitle();

    /**
     * 视频播放路径 url / file path
     */
    String getVideoPath();

    /**
     * 视频时长
     */
    int getVideoTime();

}
