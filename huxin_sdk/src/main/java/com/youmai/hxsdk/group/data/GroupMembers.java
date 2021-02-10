package com.youmai.hxsdk.group.data;

import com.youmai.hxsdk.db.bean.ContactBean;

import java.util.ArrayList;

public class GroupMembers {

    private static GroupMembers instance;

    private ArrayList<ContactBean> groupList;
    private ContactBean groupOwner;

    private GroupMembers() {
        groupList = new ArrayList<>();
    }

    public static GroupMembers instance() {
        if (instance == null) {
            instance = new GroupMembers();
        }
        return instance;
    }


    public void setGroupOwner(ContactBean groupOwner) {
        this.groupOwner = groupOwner;
    }

    public ArrayList<ContactBean> getGroupList() {
        if (groupOwner != null
                && !groupList.contains(groupOwner)) {
            groupList.add(groupOwner);
        }
        return groupList;
    }

    public ArrayList<ContactBean> getDelGroupList() {
        if (groupList.contains(groupOwner)) {
            groupList.remove(groupOwner);
        }
        return groupList;
    }


    public void addGroupListItem(ContactBean item) {
        groupList.add(item);
    }

    public void addGroupListItem(int index, ContactBean item) {
        groupList.add(index, item);
    }

    public void addAll(ArrayList<ContactBean> list) {
        groupList.addAll(list);
    }

    public void removeAll(ArrayList<ContactBean> list) {
        groupList.removeAll(list);
    }


    public void clear() {
        groupList.clear();
        groupOwner = null;
    }

    public void destory() {
        groupList.clear();
        groupList = null;
        groupOwner = null;
        instance = null;
    }
}
