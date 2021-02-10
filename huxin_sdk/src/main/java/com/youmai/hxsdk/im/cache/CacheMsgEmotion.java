package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 14:27
 * Description: 表情
 */
public class CacheMsgEmotion implements Parcelable, JsonFormat<CacheMsgEmotion> {

    public int emotionRes = -1;
    public String emotionContent;


    public CacheMsgEmotion() {

    }

    public int getEmotionRes() {
        return emotionRes;
    }

    public String getEmotionContent() {
        return emotionContent;
    }


    public CacheMsgEmotion setEmotion(String emotionContent, int emotionRes) {
        this.emotionContent = emotionContent;
        this.emotionRes = emotionRes;
        return this;
    }

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgEmotion fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            emotionRes = jsonObject.optInt("emotionRes");
            emotionContent = jsonObject.optString("emotionContent");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.emotionRes);
        dest.writeString(this.emotionContent);
    }

    protected CacheMsgEmotion(Parcel in) {
        this.emotionRes = in.readInt();
        this.emotionContent = in.readString();
    }

    public static final Parcelable.Creator<CacheMsgEmotion> CREATOR = new Parcelable.Creator<CacheMsgEmotion>() {
        @Override
        public CacheMsgEmotion createFromParcel(Parcel source) {
            return new CacheMsgEmotion(source);
        }

        @Override
        public CacheMsgEmotion[] newArray(int size) {
            return new CacheMsgEmotion[size];
        }
    };

}
