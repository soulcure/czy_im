package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-07 11:51
 * Description:
 */
public class CacheMsgImage implements Parcelable, JsonFormat<CacheMsgImage> {
    public static final int SEND_IS_ORI = 0; // 发送原图
    public static final int SEND_NOT_ORI = 1; // 发送非原图
    public static final int SEND_IS_ORI_RECV_IS_ORI = 2; // 发送原图接收原图
    public static final int SEND_IS_ORI_RECV_NOT_ORI = 3; // 发送原图接收非原图
    public static final int SEND_NOT_ORI_RECV_NOT_ORI = 4; // 发送非原图接收非原图

    private String fid;
    private String filePath;
    private int originalType;


    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    public String getFid() {
        return fid;
    }

    public CacheMsgImage setFid(String fid) {
        this.fid = fid;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public CacheMsgImage setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public int getOriginalType() {
        return originalType;
    }

    public CacheMsgImage setOriginalType(int originalType) {
        this.originalType = originalType;
        return this;
    }

    @Override
    public CacheMsgImage fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            fid = jsonObject.optString("fid");
            filePath = jsonObject.optString("filePath");
            originalType = jsonObject.optInt("originalType");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public CacheMsgImage() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fid);
        dest.writeString(filePath);
        dest.writeInt(originalType);
    }

    protected CacheMsgImage(Parcel in) {
        fid = in.readString();
        filePath = in.readString();
        originalType = in.readInt();
    }

    public static final Creator<CacheMsgImage> CREATOR = new Creator<CacheMsgImage>() {
        @Override
        public CacheMsgImage createFromParcel(Parcel in) {
            return new CacheMsgImage(in);
        }

        @Override
        public CacheMsgImage[] newArray(int size) {
            return new CacheMsgImage[size];
        }
    };

}
