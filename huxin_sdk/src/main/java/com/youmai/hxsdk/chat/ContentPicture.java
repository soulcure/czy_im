package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;


/**
 * Created by colin on 2016/7/22.
 */
public class ContentPicture implements Parcelable {

    private String picUrl;
    private String barTime;
    private String describe;

    public ContentPicture(IMContentUtil parser) {
        IMContentType type;

        while ((type = parser.hasNext()) != null) {
            switch (type) {
                case CONTEXT_PICTURE_ID:
                    picUrl = parser.readNext();
                    break;
                case CONTEXT_BAR_TIME:
                    barTime = parser.readNext();
                    break;
                case CONTEXT_DESCRIBE:
                    describe = parser.readNext();
                    break;
                default:
                    parser.readNext();
                    break;

            }
        }
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getBarTime() {
        return barTime;
    }

    public String getDescribe() {
        return describe;
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


        dest.writeString(picUrl);
        dest.writeString(barTime);
        dest.writeString(describe);
    }


    public static final Parcelable.Creator<ContentPicture> CREATOR = new Parcelable.Creator<ContentPicture>() {
        public ContentPicture createFromParcel(Parcel in) {
            return new ContentPicture(in);
        }

        public ContentPicture[] newArray(int size) {
            return new ContentPicture[size];
        }
    };

    private ContentPicture(Parcel in) {
        /*下面是协议字段*/
        picUrl = in.readString();
        barTime = in.readString();
        describe = in.readString();
    }
    /**上面是android 序列号传送对象*/
}
