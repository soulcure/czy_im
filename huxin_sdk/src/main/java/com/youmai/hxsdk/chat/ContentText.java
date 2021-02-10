package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by colin on 2016/7/22.
 */
public class ContentText implements Parcelable {

    private String content;
    private String barTime;

    public ContentText(String text, String time) {
        content = text;
        barTime = time;
    }

    public ContentText(IMContentUtil parser) {
        IMContentType type;

        while ((type = parser.hasNext()) != null) {
            switch (type) {
                case CONTENT_TEXT:
                    content = parser.readNext();
                    break;
                case CONTEXT_BAR_TIME:
                    barTime = parser.readNext();
                default:
                    parser.readNext();
                    break;
            }
        }
    }

    public ContentText(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            content = jsonObject.optString(IMContentType.CONTENT_TEXT.name());
            barTime = jsonObject.optString(IMContentType.CONTEXT_BAR_TIME.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String getContent() {
        return content;
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


        dest.writeString(content);
        dest.writeString(barTime);
    }


    public static final Parcelable.Creator<ContentText> CREATOR = new Parcelable.Creator<ContentText>() {
        public ContentText createFromParcel(Parcel in) {
            return new ContentText(in);
        }

        public ContentText[] newArray(int size) {
            return new ContentText[size];
        }
    };

    private ContentText(Parcel in) {
        /*下面是协议字段*/
        content = in.readString();
        barTime = in.readString();
    }
    /**上面是android 序列号传送对象*/
}
