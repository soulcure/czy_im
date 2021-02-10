package com.youmai.hxsdk.utils;

import java.io.IOException;

/**
 * Created by Administrator on 2017/1/10.
 */

public class EMUIUtils {

    private static final String KEY_EMUI_VERSION_CODE = "ro.build.hw_emui_api_level";


    public static boolean isEMUI() {
        try {
            //BuildProperties 是一个工具类，下面会给出代码
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_EMUI_VERSION_CODE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    public static int getEmuiCode() {
        int level = 0;
        try {
            //BuildProperties 是一个工具类，下面会给出代码
            final BuildProperties prop = BuildProperties.newInstance();
            String valueString = prop.getProperty(KEY_EMUI_VERSION_CODE, "0");
            try {
                level = Integer.parseInt(valueString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return level;
    }


    public static boolean isHigherV9() {
        return getEmuiCode() >= 9;
    }

}
