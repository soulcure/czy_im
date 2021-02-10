package com.youmai.hxsdk.service.download.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fylder on 2017/4/27.
 */

public class FileQueue implements Parcelable {

    private String path;//下载链接
    private Long mid;
    private int pro;//进度
    private String phone;

    public FileQueue() {
    }


    protected FileQueue(Parcel in) {
        path = in.readString();
        if (in.readByte() == 0) {
            mid = null;
        } else {
            mid = in.readLong();
        }
        pro = in.readInt();
        phone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        if (mid == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(mid);
        }
        dest.writeInt(pro);
        dest.writeString(phone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileQueue> CREATOR = new Creator<FileQueue>() {
        @Override
        public FileQueue createFromParcel(Parcel in) {
            return new FileQueue(in);
        }

        @Override
        public FileQueue[] newArray(int size) {
            return new FileQueue[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public int getPro() {
        return pro;
    }

    public void setPro(int pro) {
        this.pro = pro;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "FileQueue{" +
                "path='" + path + '\'' +
                ", mid=" + mid +
                ", pro=" + pro +
                ", phone='" + phone + '\'' +
                '}';
    }
}
