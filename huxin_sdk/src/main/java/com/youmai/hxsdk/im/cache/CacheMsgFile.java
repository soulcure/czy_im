package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 14:59
 * Description:
 */
public class CacheMsgFile implements Parcelable, JsonFormat<CacheMsgFile> {

    private String fileName;

    private long fileSize;

    private String filePath;

    private String fileUrl;

    private String fid;

    private int fileRes;

    public CacheMsgFile() {

    }

    public String getFileName() {
        return fileName;
    }

    public CacheMsgFile setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public long getFileSize() {
        return fileSize;
    }

    public CacheMsgFile setFileSize(long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public CacheMsgFile setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public CacheMsgFile setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public String getFid() {
        return fid;
    }

    public CacheMsgFile setFid(String fid) {
        this.fid = fid;
        return this;
    }


    public int getFileRes() {
        if (fileRes == R.drawable.hx_icon_folder_pdf
                || fileRes == R.drawable.hx_icon_folder_word
                || fileRes == R.drawable.hx_icon_folder_ppt
                || fileRes == R.drawable.hx_icon_folder_xls
                || fileRes == R.drawable.hx_icon_folder_txt
                || fileRes == R.drawable.hx_icon_folder_default
                || fileRes == R.drawable.hx_icon_folder_music
                || fileRes == R.drawable.hx_icon_folder_default
                || fileRes == R.drawable.hx_icon_folder_zip) {
            return fileRes;
        } else {
            return -1;
        }
    }

    public CacheMsgFile setFileRes(int fileRes) {
        this.fileRes = fileRes;
        return this;
    }

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgFile fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            fileName = jsonObject.optString("fileName");
            fileSize = jsonObject.optLong("fileSize");
            filePath = jsonObject.optString("filePath");
            fileRes = jsonObject.optInt("fileRes");
            fileUrl = jsonObject.optString("fileUrl");
            fid = jsonObject.optString("fid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeLong(this.fileSize);
        dest.writeString(this.filePath);
        dest.writeString(this.fileUrl);
        dest.writeString(this.fid);
        dest.writeInt(this.fileRes);
    }

    protected CacheMsgFile(Parcel in) {
        this.fileName = in.readString();
        this.fileSize = in.readLong();
        this.filePath = in.readString();
        this.fileUrl = in.readString();
        this.fid = in.readString();
        this.fileRes = in.readInt();
    }

    public static final Creator<CacheMsgFile> CREATOR = new Creator<CacheMsgFile>() {
        @Override
        public CacheMsgFile createFromParcel(Parcel source) {
            return new CacheMsgFile(source);
        }

        @Override
        public CacheMsgFile[] newArray(int size) {
            return new CacheMsgFile[size];
        }
    };

}
