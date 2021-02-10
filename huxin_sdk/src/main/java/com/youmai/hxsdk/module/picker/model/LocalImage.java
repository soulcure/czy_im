package com.youmai.hxsdk.module.picker.model;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.module.movierecord.MediaStoreUtils;
import com.youmai.hxsdk.utils.TimeUtils;

/**
 * Created by colin on 2017/10/17.
 */

public class LocalImage implements Parcelable {
    /**
     * 原图URI
     */
    private Uri originalUri;
    /**
     * 原图path
     */
    private String path;
    /**
     * 是否处于选中状态
     */
    private boolean check;


    private String playTime = "00:00";


    public Uri getOriginalUri() {
        return originalUri;
    }

    public void setOriginalUri(Uri originalUri) {
        this.originalUri = originalUri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;

        if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".rmvb")
                || path.toLowerCase().endsWith(".avi") || path.toLowerCase().endsWith(".3gp")) {
            try {
                String[] params = MediaStoreUtils.getVideoParams(path);
                long time = Long.parseLong(params[0]);
                playTime = TimeUtils.getTimeFromMillisecond(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }


    public String getPlayTime() {
        return playTime;
    }


    public LocalImage() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.originalUri, flags);
        dest.writeString(this.path);
        dest.writeByte(this.check ? (byte) 1 : (byte) 0);
        dest.writeString(this.playTime);
    }

    protected LocalImage(Parcel in) {
        this.originalUri = in.readParcelable(Uri.class.getClassLoader());
        this.path = in.readString();
        this.check = in.readByte() != 0;
        this.playTime = in.readString();
    }

    public static final Creator<LocalImage> CREATOR = new Creator<LocalImage>() {
        @Override
        public LocalImage createFromParcel(Parcel source) {
            return new LocalImage(source);
        }

        @Override
        public LocalImage[] newArray(int size) {
            return new LocalImage[size];
        }
    };
}
