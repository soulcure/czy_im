package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;


/**
 * 从获取proto的YouMaiChat的body
 * Created by colin on 2016/7/22.
 */
public class ContentVideo implements Parcelable {

    private String videoId;
    private String frameId;
    private String barTime;
    private String name;
    private String size;

    public ContentVideo(IMContentUtil parser) {
        IMContentType type;

        while ((type = parser.hasNext()) != null) {
            switch (type) {
                case CONTEXT_VIDEO_ID:
                    videoId = parser.readNext();
                    break;
                case CONTEXT_PICTURE_ID:
                    frameId = parser.readNext();
                    break;
                case CONTEXT_BAR_TIME:
                    barTime = parser.readNext();
                    break;
                case CONTENT_FILE_NAME:
                    name = parser.readNext();
                    break;
                case CONTENT_FILE_SIZE:
                    size = parser.readNext();
                    break;
                default:
                    parser.readNext();
                    break;

            }
        }
    }

    protected ContentVideo(Parcel in) {
        videoId = in.readString();
        frameId = in.readString();
        barTime = in.readString();
        name = in.readString();
        size = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoId);
        dest.writeString(frameId);
        dest.writeString(barTime);
        dest.writeString(name);
        dest.writeString(size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContentVideo> CREATOR = new Creator<ContentVideo>() {
        @Override
        public ContentVideo createFromParcel(Parcel in) {
            return new ContentVideo(in);
        }

        @Override
        public ContentVideo[] newArray(int size) {
            return new ContentVideo[size];
        }
    };

    public String getVideoId() {
        return videoId;
    }

    public String getFrameId() {
        return frameId;
    }

    public String getBarTime() {
        return barTime;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

}
