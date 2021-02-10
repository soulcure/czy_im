package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 14:53
 * Description: 声音
 */
public class CacheMsgVoice implements Parcelable, JsonFormat<CacheMsgVoice> {

    public String voiceUrl;

    public String voiceTime;

    public String voicePath;

    public String fid;

    public boolean hasLoad = false;

    private boolean showText = false;//是否显示语音文字
    private String voiceText;//语音文字

    //用于查看是谁的源消息,IM上没有发送，暂不使用
    public int forwardCount;//转发次数,没转发过为0
    public String sourcePhone;//初始消息的发送号码

    public CacheMsgVoice() {
    }


    protected CacheMsgVoice(Parcel in) {
        voiceUrl = in.readString();
        voiceTime = in.readString();
        voicePath = in.readString();
        fid = in.readString();
        hasLoad = in.readByte() != 0;
        showText = in.readByte() != 0;
        voiceText = in.readString();
        forwardCount = in.readInt();
        sourcePhone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(voiceUrl);
        dest.writeString(voiceTime);
        dest.writeString(voicePath);
        dest.writeString(fid);
        dest.writeByte((byte) (hasLoad ? 1 : 0));
        dest.writeByte((byte) (showText ? 1 : 0));
        dest.writeString(voiceText);
        dest.writeInt(forwardCount);
        dest.writeString(sourcePhone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CacheMsgVoice> CREATOR = new Creator<CacheMsgVoice>() {
        @Override
        public CacheMsgVoice createFromParcel(Parcel in) {
            return new CacheMsgVoice(in);
        }

        @Override
        public CacheMsgVoice[] newArray(int size) {
            return new CacheMsgVoice[size];
        }
    };

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    public String getFid() {
        return fid;
    }

    public CacheMsgVoice setFid(String fid) {
        this.fid = fid;
        return this;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public CacheMsgVoice setVoicePath(String voicePath) {
        this.voicePath = voicePath;
        return this;
    }

    public String getVoiceTime() {
        return voiceTime;
    }

    public CacheMsgVoice setVoiceTime(String voiceTime) {
        this.voiceTime = voiceTime;
        return this;
    }

    public boolean isHasLoad() {
        return hasLoad;
    }

    public CacheMsgVoice setHasLoad(boolean hasLoad) {
        this.hasLoad = hasLoad;
        return this;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public String getVoiceText() {
        return voiceText;
    }

    public CacheMsgVoice setVoiceText(String voiceText) {
        this.voiceText = voiceText;
        return this;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public CacheMsgVoice setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
        return this;
    }

    public int getForwardCount() {
        return forwardCount;
    }

    public CacheMsgVoice setForwardCount(int forwardCount) {
        this.forwardCount = forwardCount;
        return this;
    }

    public String getSourcePhone() {
        return sourcePhone;
    }

    public CacheMsgVoice setSourcePhone(String sourcePhone) {
        this.sourcePhone = sourcePhone;
        return this;
    }

    //增加转发次数
    public void addForwardCount() {
        forwardCount++;
    }

    @Override
    public CacheMsgVoice fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            voiceUrl = jsonObject.optString("voiceUrl");
            voiceTime = jsonObject.optString("voiceTime");
            voicePath = jsonObject.optString("voicePath");
            fid = jsonObject.optString("fid");
            hasLoad = jsonObject.optBoolean("hasLoad");
            showText = jsonObject.optBoolean("showText");
            voiceText = jsonObject.optString("voiceText");
            forwardCount = jsonObject.optInt("forwardCount");
            sourcePhone = jsonObject.optString("sourcePhone");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

}
