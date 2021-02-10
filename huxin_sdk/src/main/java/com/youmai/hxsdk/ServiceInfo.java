package com.youmai.hxsdk;


import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Created by colin on 2017/2/12.
 */

public class ServiceInfo {
    private String uuid;   //用户UUID
    private String phoneNum;   //用户电话号码
    private String realName;   //用户名称，对应OA真实姓名
    private String nickName;   //用户名称，对应OA真实姓名
    private String sex;   //用户性别
    private String avatar;   //用户头像
    private String userName;   //用户名，OA账号
    private String orgName;   //组织架构名称


    public ServiceInfo() {
        uuid = "";   //用户UUID
        phoneNum = "";   //用户电话号码
        realName = "";   //用户名称，对应OA真实姓名
        nickName = "";   //用户昵称
        sex = "";   //用户性别
        avatar = "";   //用户头像
        userName = "";   //用户名，OA账号
        orgName = "";
    }

    public String getDisplayName() {
        String res = "";
        if (!TextUtils.isEmpty(realName)) {
            res = realName;
        } else if (TextUtils.isEmpty(userName)) {
            res = userName;
        } else if (TextUtils.isEmpty(nickName)) {
            res = nickName;
        }
        return res;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void saveJson(Context context) {
        if (context != null) {
            String json = GsonUtil.format(this);
            AppUtils.setStringSharedPreferences(context, "serviceInfo", json);
        }
    }


    public void fromJson(Context context) {
        String json = AppUtils.getStringSharedPreferences(context, "serviceInfo", "");
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);

                uuid = jsonObject.optString("uuid");
                phoneNum = jsonObject.optString("phoneNum");
                realName = jsonObject.optString("realName");
                nickName = jsonObject.optString("nickName");
                sex = jsonObject.optString("sex");
                avatar = jsonObject.optString("avatar");
                userName = jsonObject.optString("userName");
                orgName = jsonObject.optString("orgName");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void clear(Context context) {
        uuid = "";
        phoneNum = "";
        realName = "";
        nickName = "";
        sex = "";
        avatar = "";
        userName = "";
        orgName = "";

        if (context != null) {
            AppUtils.setStringSharedPreferences(context, "serviceInfo", null);
        }
    }
}
