package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;


/**
 * Created by colin on 2016/7/22.
 */
public class ContentLocation implements Parcelable {


    private String longitudeStr;
    private String latitudeStr;
    private String scaleStr;
    private String labelStr;
    private String barTime;


    public ContentLocation(IMContentUtil parser) {
        IMContentType type;

        while ((type = parser.hasNext()) != null) {
            switch (type) {
                case CONTEXT_LONGITUDE:
                    longitudeStr = parser.readNext();
                    break;
                case CONTEXT_LAITUDE:
                    latitudeStr = parser.readNext();
                case CONTEXT_SCALE:
                    scaleStr = parser.readNext();
                    break;
                case CONTEXT_LABEL:
                    labelStr = parser.readNext();
                case CONTEXT_BAR_TIME:
                    barTime = parser.readNext();
                default:
                    parser.readNext();
                    break;

            }
        }
    }

    public String getLongitudeStr() {
        return longitudeStr;
    }

    public String getLatitudeStr() {
        return latitudeStr;
    }

    public String getScaleStr() {
        return scaleStr;
    }

    public String getLabelStr() {
        return labelStr;
    }

    public String getBarTime() {
        return barTime;
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


        dest.writeString(longitudeStr);
        dest.writeString(latitudeStr);
        dest.writeString(scaleStr);
        dest.writeString(labelStr);
        dest.writeString(barTime);
    }


    public static final Parcelable.Creator<ContentLocation> CREATOR = new Parcelable.Creator<ContentLocation>() {
        public ContentLocation createFromParcel(Parcel in) {
            return new ContentLocation(in);
        }

        public ContentLocation[] newArray(int size) {
            return new ContentLocation[size];
        }
    };

    private ContentLocation(Parcel in) {
        /*下面是协议字段*/
        longitudeStr = in.readString();
        latitudeStr = in.readString();
        scaleStr = in.readString();
        labelStr = in.readString();
        barTime = in.readString();
    }
    /**上面是android 序列号传送对象*/
}
