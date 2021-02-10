package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GroupInfoBean implements Parcelable {

    @Id
    private Long id; //主键id

    private int group_id;
    private long info_update_time; //群资料最新的更新时间戳
    private long member_update_time; //群成员最新的更新时间戳
    private String group_name;
    private String owner_id;
    private String group_avatar; //头像
    private String topic;
    private int group_member_count;//群成员数
    private String groupMemberJson; //群成员列表

    private int groupType;  //群类型


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getGroup_id() {
        return this.group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public long getInfo_update_time() {
        return this.info_update_time;
    }

    public void setInfo_update_time(long info_update_time) {
        this.info_update_time = info_update_time;
    }

    public long getMember_update_time() {
        return this.member_update_time;
    }

    public void setMember_update_time(long member_update_time) {
        this.member_update_time = member_update_time;
    }

    public String getGroup_name() {
        return this.group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getOwner_id() {
        return this.owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getGroup_avatar() {
        return this.group_avatar;
    }

    public void setGroup_avatar(String group_avatar) {
        this.group_avatar = group_avatar;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getGroup_member_count() {
        return this.group_member_count;
    }

    public void setGroup_member_count(int group_member_count) {
        this.group_member_count = group_member_count;
    }

    public String getGroupMemberJson() {
        return groupMemberJson;
    }

    public void setGroupMemberJson(String groupMemberJson) {
        this.groupMemberJson = groupMemberJson;
    }

    public int getGroupType() {
        if (groupType == 0) {
            groupType = 1;
        }
        return this.groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    @Generated(hash = 1729154387)
    public GroupInfoBean(Long id, int group_id, long info_update_time,
                         long member_update_time, String group_name, String owner_id, String group_avatar,
                         String topic, int group_member_count, String groupMemberJson, int groupType) {
        this.id = id;
        this.group_id = group_id;
        this.info_update_time = info_update_time;
        this.member_update_time = member_update_time;
        this.group_name = group_name;
        this.owner_id = owner_id;
        this.group_avatar = group_avatar;
        this.topic = topic;
        this.group_member_count = group_member_count;
        this.groupMemberJson = groupMemberJson;
        this.groupType = groupType;
    }

    @Generated(hash = 1490267550)
    public GroupInfoBean() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.group_id);
        dest.writeLong(this.info_update_time);
        dest.writeLong(this.member_update_time);
        dest.writeString(this.group_name);
        dest.writeString(this.owner_id);
        dest.writeString(this.group_avatar);
        dest.writeString(this.topic);
        dest.writeInt(this.group_member_count);
        dest.writeString(this.groupMemberJson);
        dest.writeInt(this.groupType);
    }

    protected GroupInfoBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.group_id = in.readInt();
        this.info_update_time = in.readLong();
        this.member_update_time = in.readLong();
        this.group_name = in.readString();
        this.owner_id = in.readString();
        this.group_avatar = in.readString();
        this.topic = in.readString();
        this.group_member_count = in.readInt();
        this.groupMemberJson = in.readString();
        this.groupType = in.readInt();
    }

    public static final Creator<GroupInfoBean> CREATOR = new Creator<GroupInfoBean>() {
        @Override
        public GroupInfoBean createFromParcel(Parcel source) {
            return new GroupInfoBean(source);
        }

        @Override
        public GroupInfoBean[] newArray(int size) {
            return new GroupInfoBean[size];
        }
    };
}
