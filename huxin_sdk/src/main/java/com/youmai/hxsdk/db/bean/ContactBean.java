package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.youmai.hxsdk.entity.cn.CN;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by YW on 2018/4/11.
 */
@Entity
public class ContactBean implements CN, Parcelable {

    @Id
    private Long id; //主键id

    private String uuid; //用户uuid
    private String userId; //用户id
    private String mobile;    //联系人号码
    private String nickName;    //昵称
    private String realName;    //昵称
    private String userName; //姓名 - 联系人名
    private String avatar; //头像url
    private String sex;
    private String email;
    private String jobName;
    private String orgId;  //彩之云用于小区UUID
    private String orgName;    //彩之云用于小区名称
    private String sign;     //个性签名
    private int status;  //状态（删除：0；好友：1；拉黑：2）

    @Transient
    private String pinyin = "#"; //姓名拼音

    @Transient
    private String simplePinyin;//简拼

    @Transient
    private int memberRole; //群成员角色

    @Transient
    private String orgType;

    @Transient
    private int uiType;

    public ContactBean(String name) {
        this.nickName = name;
    }


    @Override
    public String chinese() {
        //return nickName;
        return getDisplayName();
    }

    @Override
    public boolean equals(Object obj) {
        boolean res = false;

        if (obj instanceof ContactBean) {
            ContactBean bean = (ContactBean) obj;
            if (uuid != null && bean.getUuid() != null) {
                res = uuid.equals(bean.getUuid());
            } else if (uuid == null && bean.getUuid() == null) {
                res = uiType == bean.getUiType();
            }
        }
        return res;

    }


    public String getDisplayName() {
        String res = null;
        if (!TextUtils.isEmpty(realName)) {
            res = realName;
        } else if (!TextUtils.isEmpty(userName)) {
            res = userName;
        } else if (!TextUtils.isEmpty(nickName)) {
            res = nickName;
        }
        return res;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getUuid() {
        return this.uuid;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getUserId() {
        return this.userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getMobile() {
        return this.mobile;
    }


    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getNickName() {
        return this.nickName;
    }


    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public String getRealName() {
        return this.realName;
    }


    public void setRealName(String realName) {
        this.realName = realName;
    }


    public String getUserName() {
        return this.userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getAvatar() {
        return this.avatar;
    }


    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getSex() {
        return this.sex;
    }


    public void setSex(String sex) {
        this.sex = sex;
    }


    public String getEmail() {
        return this.email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getJobName() {
        return this.jobName;
    }


    public void setJobName(String jobName) {
        this.jobName = jobName;
    }


    public String getOrgId() {
        return this.orgId;
    }


    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }


    public String getOrgName() {
        return this.orgName;
    }


    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }


    public String getSign() {
        return this.sign;
    }


    public void setSign(String sign) {
        this.sign = sign;
    }


    public String getPinyin() {
        return this.pinyin;
    }


    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }


    public String getSimplePinyin() {
        return this.simplePinyin;
    }


    public void setSimplePinyin(String simplePinyin) {
        this.simplePinyin = simplePinyin;
    }


    public int getMemberRole() {
        return this.memberRole;
    }


    public void setMemberRole(int memberRole) {
        this.memberRole = memberRole;
    }


    public String getOrgType() {
        return this.orgType;
    }


    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }


    public int getUiType() {
        return this.uiType;
    }


    public void setUiType(int uiType) {
        this.uiType = uiType;
    }


    public int getStatus() {
        return this.status;
    }


    public void setStatus(int status) {
        this.status = status;
    }


    @Generated(hash = 1261050529)
    public ContactBean(Long id, String uuid, String userId, String mobile,
                       String nickName, String realName, String userName, String avatar,
                       String sex, String email, String jobName, String orgId, String orgName,
                       String sign, int status) {
        this.id = id;
        this.uuid = uuid;
        this.userId = userId;
        this.mobile = mobile;
        this.nickName = nickName;
        this.realName = realName;
        this.userName = userName;
        this.avatar = avatar;
        this.sex = sex;
        this.email = email;
        this.jobName = jobName;
        this.orgId = orgId;
        this.orgName = orgName;
        this.sign = sign;
        this.status = status;
    }


    @Generated(hash = 1283900925)
    public ContactBean() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.uuid);
        dest.writeString(this.userId);
        dest.writeString(this.mobile);
        dest.writeString(this.nickName);
        dest.writeString(this.realName);
        dest.writeString(this.userName);
        dest.writeString(this.avatar);
        dest.writeString(this.sex);
        dest.writeString(this.email);
        dest.writeString(this.jobName);
        dest.writeString(this.orgId);
        dest.writeString(this.orgName);
        dest.writeString(this.sign);
        dest.writeInt(this.status);
        dest.writeString(this.pinyin);
        dest.writeString(this.simplePinyin);
        dest.writeInt(this.memberRole);
        dest.writeString(this.orgType);
        dest.writeInt(this.uiType);
    }

    protected ContactBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.uuid = in.readString();
        this.userId = in.readString();
        this.mobile = in.readString();
        this.nickName = in.readString();
        this.realName = in.readString();
        this.userName = in.readString();
        this.avatar = in.readString();
        this.sex = in.readString();
        this.email = in.readString();
        this.jobName = in.readString();
        this.orgId = in.readString();
        this.orgName = in.readString();
        this.sign = in.readString();
        this.status = in.readInt();
        this.pinyin = in.readString();
        this.simplePinyin = in.readString();
        this.memberRole = in.readInt();
        this.orgType = in.readString();
        this.uiType = in.readInt();
    }

    public static final Creator<ContactBean> CREATOR = new Creator<ContactBean>() {
        @Override
        public ContactBean createFromParcel(Parcel source) {
            return new ContactBean(source);
        }

        @Override
        public ContactBean[] newArray(int size) {
            return new ContactBean[size];
        }
    };
}
