package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgRedPackage;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.cache.JsonFormat;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 11:31
 * Description:  消息实体类
 */
@Entity
public class CacheMsgBean implements Parcelable {


    public static final int SEND_TEXT = 1;
    public static final int SEND_IMAGE = 2;
    public static final int SEND_VOICE = 3;
    public static final int SEND_VIDEO = 4;
    public static final int SEND_LOCATION = 5;
    public static final int SEND_FILE = 6;
    public static final int SEND_EMOTION = 7;
    public static final int SEND_REDPACKAGE = 8;
    public static final int OPEN_REDPACKET = 9;

    public static final int RECEIVE_TEXT = 101;
    public static final int RECEIVE_IMAGE = 102;
    public static final int RECEIVE_VOICE = 103;
    public static final int RECEIVE_VIDEO = 104;
    public static final int RECEIVE_LOCATION = 105;
    public static final int RECEIVE_FILE = 106;
    public static final int RECEIVE_EMOTION = 107;
    public static final int RECEIVE_REDPACKAGE = 108;
    public static final int RECEIVE_PACKET_OPENED = 109;

    public static final int GROUP_MEMBER_CHANGED = 1001;
    public static final int GROUP_NAME_CHANGED = 1002;
    public static final int GROUP_TRANSFER_OWNER = 1003;
    public static final int PACKET_OPENED_SUCCESS = 1004;

    public static final int BUDDY_AGREE = 2001;
    public static final int BUDDY_BLACK = 2002;
    public static final int BUDDY_DEL = 2003;


    public static final int SEND_DRAFT = 0;      //草稿
    public static final int SEND_GOING = 1;//正在发送
    public static final int SEND_SUCCEED = 2;    //发送成功
    public static final int SEND_FAILED = 3;  //发送失败
    public static final int RECEIVE_UNREAD = 4;   //接收到消息，未读
    public static final int RECEIVE_READ = 5;  //接收到消息，已读

    @Id
    private Long id;  //消息ID

    private Long msgId; //发送消息成功后IM后台回给的消息Id
    private int msgType; //发送和接受消息类型
    private long msgTime; //消息时间

    private String senderUserId; //发送者的uuid
    private String senderMobile; //发送者的手机号
    private String senderSex; //发送者的性别
    private String senderRealName; //发送者的姓名
    private String senderAvatar; //发送者的头像
    private String senderUserName; //发送者账号名

    private String receiverUserId; //接收者的uuid

    private String contentJsonBody;  //消息内容json body

    private int groupId;  //标识群组id
    private int groupType;  //标识群组类型

    private String targetName; //沟通对方的姓名
    private String targetUuid; //沟通列表查询的关键字段：eg:去重, 筛选时间最近的一条
    private String targetAvatar; //沟通对方的头像
    private String targetUserName; //用于拉取用户详情与拼接头像
    private int msgStatus;  //消息发送状态
    private int progress;//保存下载进度

    private String memberChanged; //群组成员变化提示

    @Transient
    private boolean isTop;

    public CacheMsgBean(CacheMsgBean bean) {
        this.id = bean.getId();
        this.msgId = bean.getMsgId();
        this.msgType = bean.getMsgType();
        this.msgTime = bean.getMsgTime();

        this.senderUserId = bean.getSenderUserId();
        this.senderMobile = bean.getSenderMobile(); //发送者的手机号
        this.senderSex = bean.getSenderSex(); //发送者的性别
        this.senderRealName = bean.getSenderRealName(); //发送者的姓名
        this.senderAvatar = bean.getSenderAvatar(); //发送者的头像
        this.senderUserName = bean.getSenderUserName(); //发送者的头像

        this.receiverUserId = bean.getReceiverUserId();

        this.contentJsonBody = bean.getContentJsonBody();

        this.groupId = bean.getGroupId();  //标识群组id
        this.groupType = bean.getGroupType();  //标识群组id
        this.targetName = bean.getTargetName(); //沟通对方的姓名
        this.targetUserName = bean.getTargetUserName();
        this.targetAvatar = bean.getTargetAvatar();//沟通对方的头像
        this.targetUuid = bean.getTargetUuid();
        this.msgStatus = bean.getMsgStatus();
        this.progress = bean.getProgress();
        this.memberChanged = bean.getMemberChanged();
    }


    /**
     * 设置json body obj
     *
     * @param jsonBodyObj
     * @return
     */
    public CacheMsgBean setJsonBodyObj(JsonFormat jsonBodyObj) {
        this.contentJsonBody = jsonBodyObj.toJson();
        return this;
    }

    public JsonFormat getJsonBodyObj() {
        JsonFormat jsonBodyObj = null;
        switch (msgType) {
            case SEND_TEXT:
            case RECEIVE_TEXT:
                jsonBodyObj = new CacheMsgTxt().fromJson(contentJsonBody);
                break;
            case SEND_IMAGE:
            case RECEIVE_IMAGE:
                jsonBodyObj = new CacheMsgImage().fromJson(contentJsonBody);
                break;
            case SEND_LOCATION:
            case RECEIVE_LOCATION:
                jsonBodyObj = new CacheMsgMap().fromJson(contentJsonBody);
                break;
            case SEND_VOICE:
            case RECEIVE_VOICE:
                jsonBodyObj = new CacheMsgVoice().fromJson(contentJsonBody);
                break;
            case SEND_EMOTION:
            case RECEIVE_EMOTION:
                jsonBodyObj = new CacheMsgEmotion().fromJson(contentJsonBody);
                break;
            case SEND_FILE:
            case RECEIVE_FILE:
                jsonBodyObj = new CacheMsgFile().fromJson(contentJsonBody);
                break;
            case SEND_VIDEO:
            case RECEIVE_VIDEO:
                jsonBodyObj = new CacheMsgVideo().fromJson(contentJsonBody);
                break;
            case SEND_REDPACKAGE:
            case RECEIVE_REDPACKAGE:
            case OPEN_REDPACKET:
            case RECEIVE_PACKET_OPENED:
            case PACKET_OPENED_SUCCESS:
                jsonBodyObj = new CacheMsgRedPackage().fromJson(contentJsonBody);
                break;
        }

        return jsonBodyObj;
    }

    public boolean isRightUI() {
        boolean res = false;
        switch (msgType) {
            case SEND_TEXT:
                res = true;
                break;
            case SEND_IMAGE:
                res = true;
                break;
            case SEND_LOCATION:
                res = true;
                break;
            case SEND_VOICE:
                res = true;
                break;
            case SEND_EMOTION:
                res = true;
                break;
            case SEND_FILE:
                res = true;
                break;
            case SEND_VIDEO:
                res = true;
                break;
            case SEND_REDPACKAGE:
                res = true;
                break;
        }
        return res;
    }


    public Long getMsgId() {
        return msgId;
    }

    public CacheMsgBean setMsgId(Long msgId) {
        this.msgId = msgId;
        return this;
    }

    public int getMsgType() {
        return msgType;
    }

    public CacheMsgBean setMsgType(int msgType) {
        this.msgType = msgType;
        return this;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public CacheMsgBean setMsgTime(long msgTime) {
        this.msgTime = msgTime;
        return this;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public CacheMsgBean setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
        return this;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public CacheMsgBean setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
        return this;
    }

    public String getSenderSex() {
        return senderSex;
    }

    public CacheMsgBean setSenderSex(String senderSex) {
        this.senderSex = senderSex;
        return this;
    }

    public String getSenderRealName() {
        return senderRealName;
    }

    public CacheMsgBean setSenderRealName(String senderRealName) {
        this.senderRealName = senderRealName;
        return this;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public CacheMsgBean setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
        return this;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public CacheMsgBean setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
        return this;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public CacheMsgBean setReceiverUserId(String receiverUserId) {
        this.receiverUserId = receiverUserId;
        return this;
    }


    public String getContentJsonBody() {
        return contentJsonBody;
    }

    public CacheMsgBean setContentJsonBody(String contentJsonBody) {
        this.contentJsonBody = contentJsonBody;
        return this;
    }

    public int getGroupId() {
        return groupId;
    }

    public CacheMsgBean setGroupId(int groupId) {
        this.groupId = groupId;
        return this;
    }


    public int getGroupType() {
        return groupType;
    }

    public CacheMsgBean setGroupType(int groupType) {
        this.groupType = groupType;
        return this;
    }

    public String getTargetUuid() {
        return targetUuid;
    }

    public CacheMsgBean setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
        return this;
    }


    public String getTargetAvatar() {
        return targetAvatar;
    }

    public CacheMsgBean setTargetAvatar(String targetAvatar) {
        this.targetAvatar = targetAvatar;
        return this;
    }

    public int getMsgStatus() {
        return msgStatus;
    }

    public CacheMsgBean setMsgStatus(int msgStatus) {
        this.msgStatus = msgStatus;
        return this;
    }

    public String getTargetName() {
        return targetName;
    }

    public CacheMsgBean setTargetName(String targetName) {
        this.targetName = targetName;
        return this;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public CacheMsgBean setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public CacheMsgBean setProgress(int progress) {
        this.progress = progress;
        return this;
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getMemberChanged() {
        return this.memberChanged;
    }


    public CacheMsgBean setMemberChanged(String memberChanged) {
        this.memberChanged = memberChanged;
        return this;
    }

    public boolean isTop() {
        return isTop;
    }

    public CacheMsgBean setTop(boolean top) {
        isTop = top;
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.msgId);
        dest.writeInt(this.msgType);
        dest.writeLong(this.msgTime);
        dest.writeString(this.senderUserId);
        dest.writeString(this.senderMobile);
        dest.writeString(this.senderSex);
        dest.writeString(this.senderRealName);
        dest.writeString(this.senderAvatar);
        dest.writeString(this.senderUserName);
        dest.writeString(this.receiverUserId);
        dest.writeString(this.contentJsonBody);
        dest.writeInt(this.groupId);
        dest.writeInt(this.groupType);
        dest.writeString(this.targetName);
        dest.writeString(this.targetUuid);
        dest.writeString(this.targetAvatar);
        dest.writeString(this.targetUserName);
        dest.writeInt(this.msgStatus);
        dest.writeInt(this.progress);
        dest.writeString(this.memberChanged);
        dest.writeByte(this.isTop ? (byte) 1 : (byte) 0);
    }

    protected CacheMsgBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.msgId = (Long) in.readValue(Long.class.getClassLoader());
        this.msgType = in.readInt();
        this.msgTime = in.readLong();
        this.senderUserId = in.readString();
        this.senderMobile = in.readString();
        this.senderSex = in.readString();
        this.senderRealName = in.readString();
        this.senderAvatar = in.readString();
        this.senderUserName = in.readString();
        this.receiverUserId = in.readString();
        this.contentJsonBody = in.readString();
        this.groupId = in.readInt();
        this.groupType = in.readInt();
        this.targetName = in.readString();
        this.targetUuid = in.readString();
        this.targetAvatar = in.readString();
        this.targetUserName = in.readString();
        this.msgStatus = in.readInt();
        this.progress = in.readInt();
        this.memberChanged = in.readString();
        this.isTop = in.readByte() != 0;
    }


    @Generated(hash = 2056432359)
    public CacheMsgBean(Long id, Long msgId, int msgType, long msgTime, String senderUserId,
            String senderMobile, String senderSex, String senderRealName, String senderAvatar,
            String senderUserName, String receiverUserId, String contentJsonBody, int groupId,
            int groupType, String targetName, String targetUuid, String targetAvatar,
            String targetUserName, int msgStatus, int progress, String memberChanged) {
        this.id = id;
        this.msgId = msgId;
        this.msgType = msgType;
        this.msgTime = msgTime;
        this.senderUserId = senderUserId;
        this.senderMobile = senderMobile;
        this.senderSex = senderSex;
        this.senderRealName = senderRealName;
        this.senderAvatar = senderAvatar;
        this.senderUserName = senderUserName;
        this.receiverUserId = receiverUserId;
        this.contentJsonBody = contentJsonBody;
        this.groupId = groupId;
        this.groupType = groupType;
        this.targetName = targetName;
        this.targetUuid = targetUuid;
        this.targetAvatar = targetAvatar;
        this.targetUserName = targetUserName;
        this.msgStatus = msgStatus;
        this.progress = progress;
        this.memberChanged = memberChanged;
    }


    @Generated(hash = 107805209)
    public CacheMsgBean() {
    }

    public static final Creator<CacheMsgBean> CREATOR = new Creator<CacheMsgBean>() {
        @Override
        public CacheMsgBean createFromParcel(Parcel source) {
            return new CacheMsgBean(source);
        }

        @Override
        public CacheMsgBean[] newArray(int size) {
            return new CacheMsgBean[size];
        }
    };
}
