package com.youmai.hxsdk.view.chat.emoticon.bean;


import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonBase;

/**
 * EmoticonEntity
 * Created by 90Chris on 2015/11/25.
 */
public class EmoticonEntity {
    private String path;
    private EmoticonBase.Scheme scheme;

    public EmoticonEntity(String path, EmoticonBase.Scheme scheme) {
        this.path = path;
        this.scheme = scheme;
    }

    public String getPath() {
        return path;
    }

    public EmoticonBase.Scheme getScheme() {
        return scheme;
    }
}
