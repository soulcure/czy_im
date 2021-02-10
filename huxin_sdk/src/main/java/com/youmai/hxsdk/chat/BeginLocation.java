package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;


/**
 * Created by colin on 2016/7/22.
 */
public class BeginLocation implements Parcelable {

    private String longitudeStr;
    private String latitudeStr;
    private String location;
    private boolean answerOrReject;


    public BeginLocation(IMContentUtil parser) {
        IMContentType type;

        while ((type = parser.hasNext()) != null) {
            switch (type) {
                case CONTEXT_LONGITUDE:
                    longitudeStr = parser.readNext();
                    break;
                case CONTEXT_LAITUDE:
                    latitudeStr = parser.readNext();
                    break;
                case CONTEXT_ANSWER_REJECT:
                    String aj = parser.readNext();
                    answerOrReject = !aj.equals("0");
                    break;
                case CONTEXT_LOC_ANSWER:
                    location = parser.readNext();
                    break;
                default:
                    parser.readNext();
                    break;

            }
        }
    }

    /**
     * 以下是android 序列号传送对象
     */
    protected BeginLocation(Parcel in) {
        longitudeStr = in.readString();
        latitudeStr = in.readString();
        location = in.readString();
        answerOrReject = in.readByte() != 0;
    }

    public static final Creator<BeginLocation> CREATOR = new Creator<BeginLocation>() {
        @Override
        public BeginLocation createFromParcel(Parcel in) {
            return new BeginLocation(in);
        }

        @Override
        public BeginLocation[] newArray(int size) {
            return new BeginLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(longitudeStr);
        dest.writeString(latitudeStr);
        dest.writeString(location);
        dest.writeByte((byte) (answerOrReject ? 1 : 0));
    }

    /**
     * 上面是android 序列号传送对象
     */

    public String getLongitudeStr() {
        return longitudeStr;
    }

    public String getLatitudeStr() {
        return latitudeStr;
    }

    public String getLocation() {
        return location;
    }

    public boolean isAnswerOrReject() {
        return answerOrReject;
    }

}
