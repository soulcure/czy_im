package com.youmai.hxsdk.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * ScreenUtils
 * dp(dip): device independent pixels(设备独立像素). 不同设备有不同的显示效果,这个和设备硬件有关
 * px: pixels(像素). 不同设备显示效果相同，一般我们HVGA代表320x480像素，这个用的比较多。
 * sp: scaled pixels(放大像素). 主要用于字体显示best for textsize。
 */
public class ScreenUtils {

    private ScreenUtils() {
        throw new AssertionError();
    }


    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }

        return dp * getDensity(context);
    }

    public static float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / getDensity(context);
    }


    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int pxTosp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int spTopx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将dip值转换为px值
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getHeightPixels(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕分辨率
     */
    public static String getScreenPixels(Context context) {
        int w = getWidthPixels(context);
        int h = getHeightPixels(context);
        return w + "x" + h;

    }

    /**
     * 每英寸像素数
     *
     * @return
     */
    public static int getDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }


    /**
     * 获取设备短边的DPI
     *
     * @param context
     * @return
     */
    public static float getSmallWidthDPI(Context context) {
        int width = getWidthPixels(context);
        int height = getHeightPixels(context);
        int small = width > height ? height : width;
        return pxToDp(context, small);
    }

    /**
     * 获取设备动态横边的DPI
     *
     * @param context
     * @return
     */
    public static float getWidthDPI(Context context) {
        int width = getWidthPixels(context);
        return pxToDp(context, width);
    }


    /**
     * 判断当前设备是否是手机
     *
     * @param context
     * @return
     */
    public static boolean isPhone(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) < Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * 一般是19寸以上是电视
     *
     * @param context
     * @return
     */
    public static boolean isTv(Context context) {
        boolean res = false;
        View view = new View(context);
        if (!view.isInTouchMode()) {
            res = true;
        } else if (getScreenPhysicalSize(context) > 19) {  //物理尺寸19寸以上（在电视盒子上判断失效）
            res = true;
        }

        return res;

    }


    /**
     * 一般是7寸以上是平板
     * 一般是19寸以上是电视
     *
     * @param context
     * @return
     */
    public static double getScreenPhysicalSize(Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        double wi = (double) width / (double) dens;
        double hi = (double) height / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        return screenInches;
    }
}
