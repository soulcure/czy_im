package com.youmai.hxsdk.data;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

import java.util.Comparator;

public class SortComparator implements Comparator<CacheMsgBean> {
    @Override
    public int compare(CacheMsgBean lhs, CacheMsgBean rhs) {
        if (rhs.isTop()) {
            return 1;
        }

        if (lhs.isTop()) {
            return -1;
        }

        return String.valueOf(rhs.getMsgTime()).compareTo(String.valueOf(lhs.getMsgTime()));
    }
}


