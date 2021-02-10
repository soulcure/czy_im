package com.youmai.hxsdk.module.picker.model;


/**
 * Created by colin on 2017/10/17.
 */

public class LocalImageAlbum {

    /**
     * 文件夹名称
     */
    private String albumName;

    /**
     * 文件个数
     */
    private int count;


    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
