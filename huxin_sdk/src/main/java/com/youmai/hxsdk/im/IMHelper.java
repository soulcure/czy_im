package com.youmai.hxsdk.im;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;


import com.youmai.hxsdk.R;

import java.io.File;
import java.util.Locale;

/**
 * Created by Kevin on 2016/11/24.
 */

public class IMHelper {

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format(Locale.CHINA, "%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(Locale.CHINA, f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(Locale.CHINA, f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format(Locale.CHINA, "%d B", size);
    }

    /**
     * make true current connect service is wifi
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 文件资源
     *
     * @param fileName
     * @return
     */
    public static int getFileImgRes(String fileName, boolean isFullView) {
        final String preName = IMHelper.getPreFixName(fileName);

        int res;

        if (isFullView) {
            res = R.drawable.hx_full_file_others;
            //后缀名
            if ("doc".equalsIgnoreCase(preName) || "docx".equalsIgnoreCase(preName)) {
                res = R.drawable.hx_full_file_doc;
            } else if ("xls".equalsIgnoreCase(preName) || "xlsx".equalsIgnoreCase(preName)) {
                res = R.drawable.hx_full_file_xls;
            } else if ("ppt".equalsIgnoreCase(preName) || "pptx".equalsIgnoreCase((preName))) {
                res = R.drawable.hx_full_file_ppt;
            } else if ("pdf".equalsIgnoreCase(preName)) {
                res = R.drawable.hx_full_file_pdf;
            } else if ("mp4".equalsIgnoreCase(preName) || "avi".equalsIgnoreCase(preName)) {
                res = R.drawable.hx_full_file_movie;
            } else if ("mp3".equalsIgnoreCase(preName) || "ogg".equalsIgnoreCase(preName) || "rmvb".equalsIgnoreCase(preName)) {
                res = R.drawable.hx_full_file_music;
            } else if ("txt".equalsIgnoreCase(preName)) {
                res = R.drawable.hx_full_file_txt;
            }
        } else {
            res = R.drawable.hx_im_file_iv;
            //后缀名
            if ("doc".equalsIgnoreCase(preName) || "docx".equalsIgnoreCase(preName)) {
                res = R.drawable.hx_im_file_doc;
            } else if ("xls".equalsIgnoreCase(preName) || "xlsx".equalsIgnoreCase(preName)) {
                res = R.drawable.hx_im_file_xls;
            } else if ("ppt".equalsIgnoreCase(preName)) {
                res = R.drawable.hx_im_file_ppt;
            } else if ("pdf".equalsIgnoreCase(preName)) {
                res = R.drawable.hx_im_file_pdf;
            }
        }
        return res;
    }

    /**
     * 获取文件后缀名
     *
     * @param path
     * @return
     */
    public static String getPreFixName(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            String fileName = file.getName();
            if (fileName.lastIndexOf(".") != -1) {
                return path.substring(path.lastIndexOf(".") + 1);
            }
        }
        return "";
    }
}
