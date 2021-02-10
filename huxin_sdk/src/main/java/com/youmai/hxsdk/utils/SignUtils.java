package com.youmai.hxsdk.utils;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class SignUtils {

    private SignUtils() {
        throw new AssertionError();
    }


    /**
     * 动态生成应用 appkey
     *
     * @param context
     * @return
     */
    public static String genSignature(Context context) {

        String packageName = context.getPackageName();
        packageName += sHA1(context);
        packageName += context.getPackageName();

        String appkey = AppUtils.md5(packageName);
        if (appkey.length() > 20) {
            appkey = appkey.substring(8, 24);
        }
        return appkey;

    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);

            byte[] cert = info.signatures[0].toByteArray();

            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i]).toLowerCase(Locale.US);
                if (appendString.length() == 1)
                    sb.append("0");
                sb.append(appendString);
                if (i != publicKey.length - 1) {
                    sb.append(":");
                }
            }
            return sb.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String genSignature(String packageName, String channel) {
        String appKey = AppUtils.md5(packageName + channel);
        appKey = appKey.substring(8, 24);
        return appKey;
    }
}
