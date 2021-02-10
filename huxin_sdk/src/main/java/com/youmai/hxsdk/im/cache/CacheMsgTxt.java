package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 14:52
 * Description: 文字
 */
public class CacheMsgTxt implements Parcelable, JsonFormat<CacheMsgTxt> {

    private String msgTxt;
    private String voiceId;//服务端文件序列
    private String voicePath;//本地语音路径

    public CacheMsgTxt() {
    }


    protected CacheMsgTxt(Parcel in) {
        msgTxt = in.readString();
        voiceId = in.readString();
        voicePath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msgTxt);
        dest.writeString(voiceId);
        dest.writeString(voicePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CacheMsgTxt> CREATOR = new Creator<CacheMsgTxt>() {
        @Override
        public CacheMsgTxt createFromParcel(Parcel in) {
            return new CacheMsgTxt(in);
        }

        @Override
        public CacheMsgTxt[] newArray(int size) {
            return new CacheMsgTxt[size];
        }
    };

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgTxt fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            msgTxt = jsonObject.optString("msgTxt");
            voiceId = jsonObject.optString("voiceId");
            voicePath = jsonObject.optString("voicePath");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getMsgTxt() {
        return msgTxt;
    }

    public CacheMsgTxt setMsgTxt(String msgTxt) {
        this.msgTxt = msgTxt;
        return this;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public CacheMsgTxt setVoiceId(String voiceId) {
        this.voiceId = voiceId;
        return this;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

}
