package com.youmai.hxsdk.module.videoplayer.bean;

public class VideoDetailInfo implements IVideoInfo {

    public String title;
    public String videoPath;
    private int videoTime;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public void setVideoTime(int videoTime) {
        this.videoTime = videoTime;
    }

    @Override
    public String getVideoTitle() {
        return title;
    }

    @Override
    public String getVideoPath() {
        return videoPath;
    }

    @Override
    public int getVideoTime() {
        return videoTime;
    }

}
