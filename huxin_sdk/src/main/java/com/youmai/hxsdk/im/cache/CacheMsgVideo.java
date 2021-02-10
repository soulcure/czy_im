package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

/**
 * Created by fylder on 2017/10/19.
 */

public class CacheMsgVideo implements Parcelable, JsonFormat<CacheMsgVideo> {

    private String videoId;
    private String frameId;
    private String videoPath;
    private String framePath;
    private String name;
    private String size;
    private long time;//毫秒


    public CacheMsgVideo() {
    }


    protected CacheMsgVideo(Parcel in) {
        videoId = in.readString();
        frameId = in.readString();
        videoPath = in.readString();
        framePath = in.readString();
        name = in.readString();
        size = in.readString();
        time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoId);
        dest.writeString(frameId);
        dest.writeString(videoPath);
        dest.writeString(framePath);
        dest.writeString(name);
        dest.writeString(size);
        dest.writeLong(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CacheMsgVideo> CREATOR = new Creator<CacheMsgVideo>() {
        @Override
        public CacheMsgVideo createFromParcel(Parcel in) {
            return new CacheMsgVideo(in);
        }

        @Override
        public CacheMsgVideo[] newArray(int size) {
            return new CacheMsgVideo[size];
        }
    };

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgVideo fromJson(String jsonStr) {
        return GsonUtil.parse(jsonStr, CacheMsgVideo.class);

    }

    public String getVideoId() {
        return videoId;
    }

    public CacheMsgVideo setVideoId(String videoId) {
        this.videoId = videoId;
        return this;
    }

    public String getFrameId() {
        return frameId;
    }

    public CacheMsgVideo setFrameId(String frameId) {
        this.frameId = frameId;
        return this;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public CacheMsgVideo setVideoPath(String videoPath) {
        this.videoPath = videoPath;
        return this;
    }

    public String getFramePath() {
        return framePath;
    }

    public CacheMsgVideo setFramePath(String framePath) {
        this.framePath = framePath;
        return this;
    }

    public String getName() {
        return name;
    }

    public CacheMsgVideo setName(String name) {
        this.name = name;
        return this;
    }

    public String getSize() {
        return size;
    }

    public CacheMsgVideo setSize(String size) {
        this.size = size;
        return this;
    }

    public long getTime() {
        return time;
    }

    public CacheMsgVideo setTime(long time) {
        this.time = time;
        return this;
    }


    @Override
    public String toString() {
        return "CacheMsgVideo{" +
                "videoId='" + videoId + '\'' +
                ", frameId='" + frameId + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", framePath='" + framePath + '\'' +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", time=" + time +
                '}';
    }

}
