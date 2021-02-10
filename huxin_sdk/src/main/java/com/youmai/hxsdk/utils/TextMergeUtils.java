package com.youmai.hxsdk.utils;

import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.R;

/**
 * Created by fylder on 2017/4/18.
 */

public class TextMergeUtils {

    private static String themeSeparator = " - ";//主题分隔符

    //合并主题
    public static String mergeTheme(String beforeTheme, String afterTheme) {
        String str;
        if (TextUtils.isEmpty(beforeTheme)) {
            str = afterTheme;
        } else {
            str = beforeTheme;
            str = str + themeSeparator;
            str += afterTheme;
        }
        return str;
    }

    //合并备注
    public static String mergeRemark(Context context, String beforeRemark, String afterRemark, String name) {
        String suffixStr = "【" + name + "】\n\n";
        String str;
        str = TextUtils.isEmpty(beforeRemark) ? context.getString(R.string.im_card_no_remark) : beforeRemark;
        str = str + suffixStr;
        str += afterRemark;
        return str;
    }

    //替换备注
    public static String replaceRemark(Context context, String remark, String name) {
        String suffixStr = "【" + name + "】";
        remark = remark.replace(suffixStr, "");
        remark = TextUtils.isEmpty(remark) ? context.getString(R.string.im_card_no_remark) : remark;
        return remark + suffixStr;
    }

    public static String getBeforeTheme(String mergeTheme) {
        int index = mergeTheme.indexOf(themeSeparator);
        if (index > 0) {
            return mergeTheme.substring(0, index);
        } else {
            return mergeTheme;
        }
    }

    public static String getAfterTheme(String mergeTheme) {
        int index = mergeTheme.indexOf(themeSeparator);
        if (index > 0 && mergeTheme.length() > index) {
            return mergeTheme.substring(index, mergeTheme.length());
        } else {
            return mergeTheme;
        }
    }

    public static String getBeforeRemark(String mergeRemark) {
        int index = mergeRemark.indexOf("】\n\n");
        if (index > 0) {
            return mergeRemark.substring(0, index);
        } else {
            return mergeRemark;
        }
    }

    public static String getAfterRemark(String mergeRemark) {
        int index = mergeRemark.indexOf("】\n\n");
        if (index > 0 && mergeRemark.length() > index) {
            return mergeRemark.substring(index, mergeRemark.length());
        } else {
            return mergeRemark;
        }
    }

    public static String getDefaultTheme(String theme, String remark) {
        String themeDefaultStr;
        if (!TextUtils.isEmpty(remark) && TextUtils.isEmpty(theme)) {
            //只保存了备注内容的情况下，主题自动填充备注内容前5个字，超出部分用省略号表示
            themeDefaultStr = remark.length() > 5 ? remark.substring(0, 5) + "..." : remark;
        } else {
            themeDefaultStr = theme;
        }
        return themeDefaultStr;
    }
}
