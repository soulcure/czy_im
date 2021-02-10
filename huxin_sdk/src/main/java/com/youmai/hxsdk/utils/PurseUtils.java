package com.youmai.hxsdk.utils;

import android.text.TextUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by fylder on 2017/7/18.
 */

public class PurseUtils {

    /**
     * 标题
     *
     * @param type
     * @param titleString
     * @return
     */
    public static String getTitle(String type, String[] titleString) {

        String title = "";
        if (TextUtils.equals(type, "1")) {
            title = titleString[0];
        } else if (TextUtils.equals(type, "2")) {
            title = titleString[1];
        } else if (TextUtils.equals(type, "3")) {
            title = titleString[2];
        } else if (TextUtils.equals(type, "4")) {
            title = titleString[3];
        } else if (TextUtils.equals(type, "0")) {
            title = titleString[4];
        }

        return title;
    }

    /**
     * 获取请求的数据类型
     * 业务类型.0:所有;1:挂机短信;2:充值提现转账;3:积分墙;4:代言;5:app下载;6:邀请;7:被邀请;8:一级返利;9:二级返利;10:voip电话  默认为0
     * <p>
     * <p>
     * 收支细明：0
     * 代言累计：4
     * 任务累计：5
     * 邀请累计：6
     * 返利累计：9（忽略）
     * voip电话：10
     *
     * @return
     */
    public static int getBizType(String type) {
        int bizType = 0;
        if (TextUtils.equals(type, "0")) {
            bizType = 0;
        } else if (TextUtils.equals(type, "1")) {
            bizType = 4;
        } else if (TextUtils.equals(type, "2")) {
            bizType = 5;
        } else if (TextUtils.equals(type, "3")) {
            bizType = 6;
        } else if (TextUtils.equals(type, "4")) {
            bizType = 9;
        } else if (TextUtils.equals(type, "5")) {
            bizType = 10;
        }
        return bizType;
    }

    /**
     * 呼币转金钱
     *
     * @param number
     * @param rate   比例
     * @return
     */
    public static float getNumber(String number, int rate) {
        float money = 0;
        try {
            float numberF = Float.valueOf(number);
            if (rate == 0) {
                money = 0;
            } else {
                money = numberF / rate;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return money;
    }

    /**
     * 兑换金额
     */
    public static String getMoney(String number, int rate) {
        float money = getNumber(number, rate);
        String s = String.format("%.3f", money);
        return s.substring(0, s.length() - 1);
    }

    /**
     * 兑换通话时长
     */
    public static String getCallTime(String number, int rate) {
        float money = getNumber(number, rate);
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(0);//精确到个位
        formater.setGroupingSize(0);
        formater.setRoundingMode(RoundingMode.DOWN);//不做四舍五入
        return formater.format(money);
    }
}
