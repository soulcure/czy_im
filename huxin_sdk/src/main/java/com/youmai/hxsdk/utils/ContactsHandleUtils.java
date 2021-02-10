package com.youmai.hxsdk.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by Administrator on 2017/7/28.
 */

public class ContactsHandleUtils {
    public static String queryContactsName(Context context, String phone) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        Cursor cursor = null;
        String name = "";
        try {
            cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    "replace(replace(replace(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",' ', '') ,'-',''),'+86','')" + "='" + phone + "'",
                    null,
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (cursor.getString(0) != null) {
                        name = cursor.getString(0).replaceAll(" ", "");
                    }
                    break;
                }
                cursor.close();
            }
        } catch (SecurityException se) {
            se.printStackTrace();
        }
        if (name.equals("")) {
            return phone;
        }
        return name;
    }

    public static boolean isInContacts(Context context, String phone) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        Cursor cursor = null;
        String name = "";
        try {
            cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    "replace(replace(replace(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",' ', '') ,'-',''),'+86','')" + "='" + phone + "'",
                    null,
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (cursor.getString(0) != null) {
                        name = cursor.getString(0).replaceAll(" ", "");
                    }
                    break;
                }
                cursor.close();
            }
        } catch (SecurityException se) {
            se.printStackTrace();
        }
        return !name.equals("");
    }

    public static boolean queryContacts(Context context, String name) {
        boolean findFlag = false;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "= '" + name + "'";

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                findFlag = true;
            }

            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            return findFlag;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return findFlag;
    }
}
