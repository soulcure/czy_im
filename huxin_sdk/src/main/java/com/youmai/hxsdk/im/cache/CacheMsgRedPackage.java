package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;


public class CacheMsgRedPackage implements Parcelable, JsonFormat<CacheMsgRedPackage> {


    public static final String RED_PACKET_REVIEW = "查看利是";
    public static final String RED_PACKET_IS_OPEN_GROUP = "利是已领完";
    public static final String RED_PACKET_IS_OPEN_SINGLE = "利是已领取";
    public static final String RED_PACKET_OVERDUE = "利是已过期";
    public static final String RED_PACKET_RECEIVE = "领取利是";
    public static final String RED_PACKET_OPENED = "利是被领取";


    private long msgId;
    private String value;
    private String redTitle;
    private String redStatus;
    private String redUuid;
    private String receiveName;
    private String receiveDone;


    private int status;  //利是状态：-1已过期 ,0未拆开 ,1未领完 ,2已撤回 ,3已退款 ,4已领完
    private int canOpen; //用户是否已抢到了该利是：0否1是
    private int isGrabbed; //是否可以开这个利是：0否1是


    public CacheMsgRedPackage() {
    }

    public String getValue() {
        return value;
    }

    public CacheMsgRedPackage setValue(String value) {
        this.value = value;
        return this;
    }

    public String getRedTitle() {
        return redTitle;
    }

    public CacheMsgRedPackage setRedTitle(String redTitle) {
        this.redTitle = redTitle;
        return this;
    }

    public long getMsgId() {
        return msgId;
    }

    public CacheMsgRedPackage setMsgId(long msgId) {
        this.msgId = msgId;
        return this;
    }

    public String getRedStatus() {
        return redStatus;
    }

    public CacheMsgRedPackage setRedStatus(String redStatus) {
        this.redStatus = redStatus;
        return this;
    }

    public String getRedUuid() {
        return redUuid;
    }

    public CacheMsgRedPackage setRedUuid(String redUuid) {
        this.redUuid = redUuid;
        return this;
    }


    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getReceiveDone() {
        return receiveDone;
    }

    public void setReceiveDone(String receiveDone) {
        this.receiveDone = receiveDone;
    }


    public int getStatus() {
        return status;
    }

    public CacheMsgRedPackage setStatus(int status) {
        this.status = status;
        return this;
    }

    public int getCanOpen() {
        return canOpen;
    }

    public CacheMsgRedPackage setCanOpen(int canOpen) {
        this.canOpen = canOpen;
        return this;
    }

    public int getIsGrabbed() {
        return isGrabbed;
    }

    public CacheMsgRedPackage setIsGrabbed(int isGrabbed) {
        this.isGrabbed = isGrabbed;
        return this;
    }

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgRedPackage fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            value = jsonObject.optString("value");
            redTitle = jsonObject.optString("redTitle");
            msgId = jsonObject.optLong("msgId");
            redStatus = jsonObject.optString("redStatus");
            redUuid = jsonObject.optString("redUuid");
            receiveName = jsonObject.optString("receiveName");
            receiveDone = jsonObject.optString("receiveDone");

            status = jsonObject.optInt("status");
            canOpen = jsonObject.optInt("canOpen");
            isGrabbed = jsonObject.optInt("isGrabbed");

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
        dest.writeLong(this.msgId);
        dest.writeString(this.value);
        dest.writeString(this.redTitle);
        dest.writeString(this.redStatus);
        dest.writeString(this.redUuid);
        dest.writeString(this.receiveName);
        dest.writeString(this.receiveDone);
        dest.writeInt(this.status);
        dest.writeInt(this.canOpen);
        dest.writeInt(this.isGrabbed);
    }

    protected CacheMsgRedPackage(Parcel in) {
        this.msgId = in.readLong();
        this.value = in.readString();
        this.redTitle = in.readString();
        this.redStatus = in.readString();
        this.redUuid = in.readString();
        this.receiveName = in.readString();
        this.receiveDone = in.readString();
        this.status = in.readInt();
        this.canOpen = in.readInt();
        this.isGrabbed = in.readInt();
    }

    public static final Creator<CacheMsgRedPackage> CREATOR = new Creator<CacheMsgRedPackage>() {
        @Override
        public CacheMsgRedPackage createFromParcel(Parcel source) {
            return new CacheMsgRedPackage(source);
        }

        @Override
        public CacheMsgRedPackage[] newArray(int size) {
            return new CacheMsgRedPackage[size];
        }
    };
}
