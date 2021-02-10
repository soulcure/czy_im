package com.youmai.hxsdk.entity;

/**
 * 作者：create by YW
 * 日期：2018.04.27 15:05
 * 描述：
 */
public class GroupAndMember {

    String member_id;
    String member_name;
    String user_name;
    int member_role;  //群成员角色(0-群主，1-管理员，2-普通成员)

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getMember_role() {
        return member_role;
    }

    public void setMember_role(int member_role) {
        this.member_role = member_role;
    }

    @Override
    public String toString() {
        return "GroupAndMember{" +
                "member_id='" + member_id + '\'' +
                ", member_name='" + member_name + '\'' +
                ", user_name='" + user_name + '\'' +
                ", member_role=" + member_role +
                '}';
    }
}
