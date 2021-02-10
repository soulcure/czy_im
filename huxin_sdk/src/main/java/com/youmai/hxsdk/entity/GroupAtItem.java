package com.youmai.hxsdk.entity;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by colin on 2017/12/12.
 */

public class GroupAtItem implements Parcelable {

    private String nickName;
    private String uuid;

    public GroupAtItem(String nickName, String uuid) {
        this.nickName = nickName;
        this.uuid = uuid;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            if (this.getClass() == obj.getClass()) {
                GroupAtItem u = (GroupAtItem) obj;
                return this.getNickName().equals(u.getNickName());
            } else {
                return false;
            }
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickName);
        dest.writeString(this.uuid);
    }

    public GroupAtItem() {
    }

    protected GroupAtItem(Parcel in) {
        this.nickName = in.readString();
        this.uuid = in.readString();
    }

    public static final Parcelable.Creator<GroupAtItem> CREATOR = new Parcelable.Creator<GroupAtItem>() {
        @Override
        public GroupAtItem createFromParcel(Parcel source) {
            return new GroupAtItem(source);
        }

        @Override
        public GroupAtItem[] newArray(int size) {
            return new GroupAtItem[size];
        }
    };
}
