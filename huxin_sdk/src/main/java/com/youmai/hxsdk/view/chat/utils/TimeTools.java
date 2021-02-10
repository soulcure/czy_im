package com.youmai.hxsdk.view.chat.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fylder on 2015/9/10.
 */
public class TimeTools {

    /**
     * 计算时间
     *
     * @param t 开始时间
     * @return 00:00
     */
    public static String getRecordTime(long t) {

        SimpleDateFormat format = new SimpleDateFormat("ss");
        return format.format(new Date(t));
    }
}
