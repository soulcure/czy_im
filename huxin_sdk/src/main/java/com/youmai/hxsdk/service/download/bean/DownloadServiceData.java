package com.youmai.hxsdk.service.download.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fylder on 2017/6/28.
 */

public class DownloadServiceData implements Parcelable {

    private int type;
    private List<FileQueue> fileQueues;

    public DownloadServiceData() {
    }


    protected DownloadServiceData(Parcel in) {
        type = in.readInt();
        fileQueues = in.createTypedArrayList(FileQueue.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeTypedList(fileQueues);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DownloadServiceData> CREATOR = new Creator<DownloadServiceData>() {
        @Override
        public DownloadServiceData createFromParcel(Parcel in) {
            return new DownloadServiceData(in);
        }

        @Override
        public DownloadServiceData[] newArray(int size) {
            return new DownloadServiceData[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<FileQueue> getFileQueues() {
        return fileQueues;
    }

    public void setFileQueues(List<FileQueue> fileQueues) {
        this.fileQueues = fileQueues;
    }
}
