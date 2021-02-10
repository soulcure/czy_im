package com.youmai.hxsdk.view.tip.bean;

/**
 * Created by fylder on 2017/11/21.
 */

public class TipBean {

    private String name;
    private String type;

    public TipBean(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
