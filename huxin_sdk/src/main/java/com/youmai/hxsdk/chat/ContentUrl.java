package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;


/**
 * Created by colin on 2016/7/22.
 */
public class ContentUrl implements Parcelable {

    private String urlStr;
    private String titleStr;
    private String descStr;
    private String barTime;

    public ContentUrl(IMContentUtil parser) {
        IMContentType type;

        while ((type = parser.hasNext()) != null) {
            switch (type) {
                case CONTENT_URL:
                    urlStr = parser.readNext();
                    break;
                case CONTEXT_TITLE:
                    titleStr = parser.readNext();
                case CONTEXT_DESCRIBE:
                    descStr = parser.readNext();
                    break;
                case CONTEXT_BAR_TIME:
                    barTime = parser.readNext();
                default:
                    parser.readNext();
                    break;

            }
        }
    }

    /**
     * 以下是android 序列号传送对象
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(urlStr);
        dest.writeString(titleStr);
        dest.writeString(descStr);
        dest.writeString(barTime);
    }


    public static final Parcelable.Creator<ContentUrl> CREATOR = new Parcelable.Creator<ContentUrl>() {
        public ContentUrl createFromParcel(Parcel in) {
            return new ContentUrl(in);
        }

        public ContentUrl[] newArray(int size) {
            return new ContentUrl[size];
        }
    };

    private ContentUrl(Parcel in) {
        /*下面是协议字段*/
        urlStr = in.readString();
        titleStr = in.readString();
        descStr = in.readString();
        barTime = in.readString();
    }
    /**上面是android 序列号传送对象*/
}
