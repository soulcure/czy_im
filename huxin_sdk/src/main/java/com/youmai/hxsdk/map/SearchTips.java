package com.youmai.hxsdk.map;


import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;

/**
 * Created by colin on 2017/11/9.
 */

public class SearchTips {
    private String name;
    private String address;
    private LatLonPoint point;


    public SearchTips(Tip tip) {
        name = tip.getName();
        address = tip.getAddress();
        point = tip.getPoint();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public LatLonPoint getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return name;
    }
}
