package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;


/**
 * Created by colin on 2016/7/22.
 */
public class ContentAudio implements Parcelable {

    private String audioId;
    private String barTime;
    private String sourcePhone;//发送源号码
    private String forwardCount;
    private boolean isPlay;//是否已经播放过

    public ContentAudio(IMContentUtil parser) {
        IMContentType type;

        while ((type = parser.hasNext()) != null) {
            switch (type) {
                case CONTEXT_AUDIO_ID:
                    audioId = parser.readNext();
                    break;
                case CONTEXT_BAR_TIME:
                    barTime = parser.readNext();
                    break;
                case CONTEXT_SOURCE_PHONE:
                    sourcePhone = parser.readNext();
                    break;
                case CONTEXT_FORWARD_COUNT:
                    forwardCount = parser.readNext();
                    break;
                default:
                    parser.readNext();
                    break;
            }
        }
    }


    protected ContentAudio(Parcel in) {
        audioId = in.readString();
        barTime = in.readString();
        sourcePhone = in.readString();
        forwardCount = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(audioId);
        dest.writeString(barTime);
        dest.writeString(sourcePhone);
        dest.writeString(forwardCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContentAudio> CREATOR = new Creator<ContentAudio>() {
        @Override
        public ContentAudio createFromParcel(Parcel in) {
            return new ContentAudio(in);
        }

        @Override
        public ContentAudio[] newArray(int size) {
            return new ContentAudio[size];
        }
    };

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getBarTime() {
        return barTime;
    }

    public void setBarTime(String barTime) {
        this.barTime = barTime;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public String getSourcePhone() {
        return sourcePhone;
    }

    public void setSourcePhone(String sourcePhone) {
        this.sourcePhone = sourcePhone;
    }

    public String getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(String forwardCount) {
        this.forwardCount = forwardCount;
    }
}
