package com.youmai.hxsdk.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.youmai.hxsdk.config.Constant;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-09-13 18:40
 * Description:
 */
public class PhoneUtil {
    /**
     * 获取联系人
     *
     * @param phone
     * @return
     */
    public static String getNickName(Context context, String phone) {
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phone);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        String showNickName = "";
        try {
            cursor = resolver.query(uri,
                    new String[]{"display_name"}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    showNickName = cursor.getString(0);
                } else {
                    showNickName = phone;
                }
                LogUtils.e(Constant.SDK_UI_TAG, "phone = " + phone);
                LogUtils.e(Constant.SDK_UI_TAG, "showNickName = " + showNickName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return showNickName;
    }

    /**
     * 仅查询联系人
     *
     * @return 若查不到，返回""
     */
    public static String getNickName2(Context context, String phone) {
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phone);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        String showNickName = "";
        try {
            cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    showNickName = cursor.getString(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return showNickName;
    }
}
