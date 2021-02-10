package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;


/**
 * Created by colin on 2016/7/22.
 */
public class ContentFile implements Parcelable {

    private String fid;
    private String fileName;
    private String fileSize;


    public ContentFile(IMContentUtil parser) {

        IMContentType type;
        while ((type = parser.hasNext()) != null) {
            switch (type) {
                case CONTENT_FILE:
                    fid = parser.readNext();
                    break;
                case CONTENT_FILE_NAME:
                    fileName = parser.readNext();
                    break;
                case CONTENT_FILE_SIZE:
                    fileSize = parser.readNext();
                    break;
                default:
                    parser.readNext();
                    break;
            }
        }
    }


    public String getFid() {
        return fid;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fid);
        dest.writeString(this.fileName);
        dest.writeString(this.fileSize);
    }

    protected ContentFile(Parcel in) {
        this.fid = in.readString();
        this.fileName = in.readString();
        this.fileSize = in.readString();
    }

    public static final Parcelable.Creator<ContentFile> CREATOR = new Parcelable.Creator<ContentFile>() {
        @Override
        public ContentFile createFromParcel(Parcel source) {
            return new ContentFile(source);
        }

        @Override
        public ContentFile[] newArray(int size) {
            return new ContentFile[size];
        }
    };
}
