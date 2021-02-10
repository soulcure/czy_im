package com.youmai.hxsdk.service.sendmsg;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

/**
 * Created by fylder on 2017/12/6.
 */

public class SendMsg implements Parcelable{

    private CacheMsgBean msg;
    private String from;//用于区分从哪来的消息

    public SendMsg(CacheMsgBean msg, String from) {
        this.msg = msg;
        this.from = from;
    }

    protected SendMsg(Parcel in) {
        msg = in.readParcelable(CacheMsgBean.class.getClassLoader());
        from = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(msg, flags);
        dest.writeString(from);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SendMsg> CREATOR = new Creator<SendMsg>() {
        @Override
        public SendMsg createFromParcel(Parcel in) {
            return new SendMsg(in);
        }

        @Override
        public SendMsg[] newArray(int size) {
            return new SendMsg[size];
        }
    };

    public CacheMsgBean getMsg() {
        return msg;
    }

    public void setMsg(CacheMsgBean msg) {
        this.msg = msg;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
