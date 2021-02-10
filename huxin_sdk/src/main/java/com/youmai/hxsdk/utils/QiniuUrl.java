package com.youmai.hxsdk.utils;

import android.content.Context;

import com.youmai.hxsdk.config.AppConfig;

import java.util.Locale;

/**
 * 七牛图片链接的
 * Created by fylder on 2017/11/2.
 */

public class QiniuUrl {
    public static final int SCALE = 30; // 30%

    /**
     * 获取图片缩略图
     * 按最大尺寸缩放
     * ?imageMogr2/thumbnail/128
     *
     * @param imageUrl 七牛图片地址
     * @param size     最大尺寸：px
     */
    public static String getMaxSizeUrl(String imageUrl, int size) {
        return String.format(Locale.CHINA, imageUrl + "?imageMogr2/thumbnail/%d", size);
    }


    /**
     * 获取缩略图的下载地址
     * 按百分比缩放
     * &imageMogr2/thumbnail/!50p
     *
     * @param fid
     * @param scale
     */
    public static String getThumbImageUrl(String fid, int scale) {
        return AppConfig.getImageUrl(fid) + String.format(Locale.CHINA, "&imageMogr2/thumbnail/!%dp", scale);
    }
}
