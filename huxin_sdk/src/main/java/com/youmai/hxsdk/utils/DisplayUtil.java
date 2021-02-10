package com.youmai.hxsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 屏幕显示工具类
 *
 * @author 罗文忠
 * @version 1.0
 * @date 2013-04-02
 */
public class DisplayUtil {

    /**
     * 获取通知栏高度.
     *
     * @param context 上下文环境.
     * @return 通知栏高度.
     * @version 1.0
     * @createTime 2013-9-15,下午2:29:29
     * @updateTime 2013-9-15,下午2:29:29
     * @createAuthor paladin
     * @updateAuthor paladin
     * @updateInfo
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @author 罗文忠
     * @date 2013-04-02
     * @version 1.0
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @author 罗文忠
     * @date 2013-04-02
     * @version 1.0
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, int spVal) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spVal * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     * @version 1.0
     * @createTime 2014年3月18日 下午12:54:02
     * @updateTime 2014年3月18日 下午12:54:02
     * @createAuthor liuyue
     * @updateAuthor liuyue
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static int getScreenWidth(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        return screenWidth;
    }

    /**
     * 得到屏幕除去静态状态栏的显示高度(px)
     *
     * @param context
     * @return
     * @version 1.0
     * @createTime 2015年5月21日, 上午9:46:54
     * @updateTime 2015年5月21日, 上午9:46:54
     * @createAuthor yanzhong
     * @updateAuthor
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static int getShowHeight(Context context) {
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        int showHeight = screenHeight - getStatusBarHeight(context);
        return showHeight;
    }

    /**
     * 根据比例设置控件的宽高
     *
     * @param context 上下文
     * @param view    需要设置宽高的控件
     * @param l       给定的比例
     * @version 1.0
     * @createTime 2015年3月6日, 下午8:00:24
     * @updateTime 2015年3月6日, 下午8:00:24
     * @createAuthor chencong
     * @updateAuthor chencong
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static void setViewWH(Context context, View view, float l) {
        int screenWidth = DisplayUtil.getScreenWidth(context);
        int height = (int) (screenWidth / l);
        view.setLayoutParams(new LayoutParams(screenWidth, height));
    }

    /**
     * 根据比例设置控件的宽高(迪讯好友资料页面专用)
     *
     * @param context 上下文
     * @param view    需要设置宽高的控件
     * @param l       给定的比例
     * @version 1.0
     * @createTime 2015年3月6日, 下午8:00:24
     * @updateTime 2015年3月6日, 下午8:00:24
     * @createAuthor chencong
     * @updateAuthor chencong
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static void setViewWH_Dixun(Context context, View view, int v, float l) {
        int screenWidth = DisplayUtil.getScreenWidth(context);
        int height = (int) (screenWidth / l);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth, height);
        layoutParams.addRule(RelativeLayout.ABOVE, v);
        view.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, height));

    }

    /**
     * 根据比例设置控件的宽高(引导页)
     *
     * @param context 上下文
     * @param view    需要设置宽高的控件
     * @param l       宽高比
     * @param scale   整体相对于屏幕宽比例
     * @version 1.0
     * @createTime 2015年3月6日, 下午8:00:24
     * @updateTime 2015年3月6日, 下午8:00:24
     * @createAuthor chencong
     * @updateAuthor chencong
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static void setViewWH_TopL(Context context, View view, float l, float scale) {
        int screenWidth = (int) (DisplayUtil.getScreenWidth(context) * scale);
        int height = (int) (screenWidth / l);
        LayoutParams layoutParams = new LayoutParams(screenWidth, height);
        layoutParams.topMargin = dip2px(context, 75 * scale);
        view.setLayoutParams(layoutParams);
    }

    /**
     * 根据比例设置控件的宽高(引导页)
     *
     * @param context 上下文
     * @param view    需要设置宽高的控件
     * @param l       给定的比例
     * @param scale   整体相对于屏幕宽比例
     * @version 1.0
     * @createTime 2015年3月6日, 下午8:00:24
     * @updateTime 2015年3月6日, 下午8:00:24
     * @createAuthor chencong
     * @updateAuthor chencong
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static void setViewWH_TxtL(Context context, View view, float l, float scale) {
        int screenWidth = (int) (DisplayUtil.getScreenWidth(context) * scale);
        int height = (int) (screenWidth / l);
        LayoutParams layoutParams = new LayoutParams(screenWidth, height);
        layoutParams.topMargin = dip2px(context, 20 * scale);
        view.setLayoutParams(layoutParams);
    }

    /**
     * 根据比例设置控件的宽高（使用在加载也的广告图上面）
     *
     * @param context 上下文
     * @param view    需要设置宽高的控件
     * @param l       给定的比例
     * @version 1.0
     * @createTime 2015年3月6日, 下午8:00:24
     * @updateTime 2015年3月6日, 下午8:00:24
     * @createAuthor chencong
     * @updateAuthor chencong
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static void setViewWH_R(Context context, View view, float l) {
        int screenWidth = DisplayUtil.getScreenWidth(context);
        int height = (int) (screenWidth / l);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth, height);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);// 设置控件在地部
        view.setLayoutParams(layoutParams);
    }

    public static void setLinLayoutW_H(Context context, View view, float l) {
        int screenWidth = DisplayUtil.getScreenWidth(context);
        int height = (int) (screenWidth / l);
        LayoutParams layoutParams = new LayoutParams(screenWidth, height);
        view.setLayoutParams(layoutParams);
    }

    /**
     * 显示/隐藏 软键盘
     *
     * @param show    true显示软键盘，false关闭软键盘
     * @param context
     * @version 1.0
     * @createTime 2015年3月8日, 上午9:36:37
     * @updateTime 2015年3月8日, 上午9:36:37
     * @createAuthor chencong
     * @updateAuthor chencong
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static void showInput(boolean show, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = ((Activity) context).getCurrentFocus();
        if (currentFocus != null) {
            IBinder applicationWindowToken = currentFocus.getApplicationWindowToken();
            if (show) {
                if (applicationWindowToken != null) {
                    imm.showSoftInputFromInputMethod(applicationWindowToken, 0);
                    imm.showSoftInput(currentFocus, InputMethodManager.SHOW_FORCED);
                }
            } else {
                if (applicationWindowToken != null) {
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(applicationWindowToken, 0);
                    }
                }
            }
        }
    }

    /**
     * 设置文本[两段颜色不一样时使用][A+B A或B是其他颜色]
     *
     * @version 1.0
     * @createTime 2015年8月17日, 下午4:37:28
     * @updateTime 2015年8月17日, 下午4:37:28
     * @createAuthor chencong
     * @updateAuthor
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static void setColorString(TextView tv, String text, String line, int color) {
        SpannableString s = new SpannableString(text + line);

        // 将名字转换为蓝色
        Pattern p = Pattern.compile(text);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (!TextUtils.isEmpty(line)) {
            tv.setText(s);
        } else {
            tv.setText("");
        }
    }

    /**
     * 设置文本[两段颜色不一样时使用][文本中间为其他颜色]
     *
     * @version 1.0
     * @createTime 2015年8月17日, 下午4:37:28
     * @updateTime 2015年8月17日, 下午4:37:28
     * @createAuthor chencong
     * @updateAuthor
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    public static void setColorString_Mid(TextView tv, String text, String line, int color) {
        SpannableString s = new SpannableString(line);

        // 将名字转换为蓝色
        Pattern p = Pattern.compile(text);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (!TextUtils.isEmpty(line)) {
            tv.setText(s);
        } else {
            tv.setText("");
        }
    }
}
