package com.youmai.hxsdk.utils;

import java.io.IOException;

/**
 * Created by Administrator on 2017/1/10.
 */

public class MiUIUtils {

    // 检测MIUI
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    private static final String KEY_MIUI_VERSION_INCRE = "ro.build.version.incremental";
    
    private static final String COMPARE_VERSION = "V8";
    private static final String COMPARE_VERSION_INCRE = "V8.1";

    public static boolean isMiUI() {
        try {
            //BuildProperties 是一个工具类，下面会给出代码
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    public static String getEmuiVerName() {
        String valueString = "";
        try {
            //BuildProperties 是一个工具类，下面会给出代码
            final BuildProperties prop = BuildProperties.newInstance();
            valueString = prop.getProperty(KEY_MIUI_VERSION_NAME, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return valueString;
    }


    public static String getEmuiVerIncre() {
        String valueString = "";
        try {
            //BuildProperties 是一个工具类，下面会给出代码
            final BuildProperties prop = BuildProperties.newInstance();
            valueString = prop.getProperty(KEY_MIUI_VERSION_INCRE, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return valueString;
    }


    public static boolean isHigherV8() {
        String property = getEmuiVerName();
        if (property != null) {
            return property.compareTo(COMPARE_VERSION) >= 0;
        }
        return false;
    }


    public static boolean isHigherVerIncre8() {
        String property = getEmuiVerIncre();
        if (property != null) {
            return property.compareTo(COMPARE_VERSION_INCRE) >= 0;
        }
        return false;
    }
}
