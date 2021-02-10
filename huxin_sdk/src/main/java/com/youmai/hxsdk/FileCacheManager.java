package com.youmai.hxsdk;

import android.content.Context;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.utils.LogUtils;

import java.io.File;
import java.math.BigDecimal;

public class FileCacheManager {

    private static final String TAG = "FileCacheManager";

    /**
     * 一键清除所有缓存
     *
     * @return
     * @author yw
     * @date 2016年3月24日
     */
    public static boolean clearAllCache(Context context) {
        try {
            String path = FileConfig.getHuXinCachePath();
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (deleteFile(file)) {
                    LogUtils.i(TAG, "文件删除成功");
                    return true;
                }
            }
            clearGlideCache(context);// 清理Glide框架的缓存

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 清除图片文件
     *
     * @return
     * @author yw
     * @date 2017年1月17日
     */
    public static boolean clearImageCache() {
        try {
            File file = new File(FileConfig.getPicDownLoadPath());
            if (deleteFile(file)) {
                LogUtils.i(TAG, "文件删除成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 清除下载的安装包
     *
     * @return
     * @author yw
     * @date 2017年1月17日
     */
    public static boolean clearApkCache() {
        try {
            File file = new File(FileConfig.getApkDownLoadPath());
            if (deleteFile(file)) {
                LogUtils.i(TAG, "文件删除成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 清除下载的视频
     *
     * @return
     * @author yw
     * @date 2016年6月20日 下午22:09:41
     */
    public static boolean clearVideoCache() {
        try {
            File file = new File(FileConfig.getVideoDownLoadPath());
            if (deleteFile(file)) {
                LogUtils.i(TAG, "文件删除成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 清除下载的语音
     *
     * @return
     * @author yw
     * @date 2016年6月20日 下午22:09:41
     */
    public static boolean clearAudioCache() {
        try {
            File file = new File(FileConfig.getAudioDownLoadPath());
            if (deleteFile(file)) {
                LogUtils.i(TAG, "文件删除成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 清除下载的文件
     *
     * @return
     * @date 2017年1月17日
     */
    public static boolean clearBigFileCache() {
        try {
            File file = new File(FileConfig.getFileDownLoadPath());
            if (deleteFile(file)) {
                LogUtils.i(TAG, "文件删除成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 清除图片加载框架中的图片缓存
     *
     * @param context
     * @author
     * @date 2017年1月27日
     */
    public static void clearGlideCache(final Context context) {
        File photoCacheDir = Glide.getPhotoCacheDir(context);
        if (null != photoCacheDir && photoCacheDir.exists()) {
            LogUtils.i(TAG, "Glide 缓存删除成功");
            Glide.get(context).clearMemory();
            //Glide.get(context).clearDiskCache();
        }
    }

    /**
     * 文件删除
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        try {
            if (file == null) {
                return true;
            }
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            } else {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 清除IM表缓存信息,比如 图文，文件，语音
     *
     * @param context
     * @author
     * @date 2017年1月17日
     */
    public static void clearIMTableCache(Context context) {
        LogUtils.i(TAG, "清除缓存个人IM信息");
        try {
            CacheMsgHelper.instance().deleteAll(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存大小
     *
     * @return
     */
    public static String getCacheSize() {
        String sizeStr;
        long size = getFolderSize(new File(FileConfig.getFileDownLoadPath()));
        long size2 = getFolderSize(new File(FileConfig.getVideoDownLoadPath()));
        long size3 = getFolderSize(new File(FileConfig.getPicDownLoadPath()));
        long size4 = getFolderSize(new File(FileConfig.getAudioDownLoadPath()));
        long size5 = getFolderSize(new File(FileConfig.getFileDownLoadPath()));

        long total = size + size2 + size3 + size4 + size5;
        sizeStr = getFormatSize(total);
        return sizeStr;
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {

        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte(s)";
            return size + "KB";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
}
