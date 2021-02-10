package com.youmai.hxsdk.view.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.youmai.hxsdk.utils.DisplayUtil;
import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonBean;
import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonSetBean;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonBase;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonLoader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Utils
 *
 * @author zhongdaxia 2014-9-2 12:05:55
 */

public class Utils {
    private static final String EXTRA_ISINITDB = "ISINITDB";
    private static final String EXTRA_DEF_KEYBOARDHEIGHT = "DEF_KEYBOARDHEIGHT";
    private static int sDefKeyboardHeight = 300; //设置默认初始化的键盘高度

    public static boolean isInitDb(Context context) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(EXTRA_ISINITDB, false);
    }

    public static void setIsInitDb(Context context, boolean b) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putBoolean(EXTRA_ISINITDB, b).apply();
    }

    public static int getDefKeyboardHeight(Context context) {
        if (sDefKeyboardHeight == 0) {   //evaluate keyboard height
            sDefKeyboardHeight = getDisplayHeightPixels(context) * 3 / 7;
        }
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        int height = settings.getInt(EXTRA_DEF_KEYBOARDHEIGHT, 0);
        if (height > 0 && sDefKeyboardHeight != height) {
            Utils.setDefKeyboardHeight(context, height);
        }
        return sDefKeyboardHeight;
    }

    public static void setDefKeyboardHeight(Context context, int height) {
        if (sDefKeyboardHeight != height) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            settings.edit().putInt(EXTRA_DEF_KEYBOARDHEIGHT, height).apply();
        }
        Utils.sDefKeyboardHeight = height;
    }

    private static int DisplayWidthPixels = 0;
    private static int DisplayHeightPixels = 0;

    private static void getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        DisplayWidthPixels = dm.widthPixels;
        DisplayHeightPixels = dm.heightPixels;
    }

    public static int getDisplayHeightPixels(Context context) {
        if (context == null) {
            return -1;
        }
        if (DisplayHeightPixels == 0) {
            getDisplayMetrics(context);
        }
        return DisplayHeightPixels;
    }

    public static int getDisplayWidthPixels(Context context) {
        if (context == null) {
            return -1;
        }
        if (DisplayWidthPixels == 0) {
            getDisplayMetrics(context);
        }
        return DisplayWidthPixels;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    public static ArrayList<EmoticonBean> ParseData(String[] arry, long eventType, EmoticonBase.Scheme scheme) {
        try {
            ArrayList<EmoticonBean> emojis = new ArrayList<>();
            for (int i = 0; i < arry.length; i++) {
                if (!TextUtils.isEmpty(arry[i])) {
                    String temp = arry[i].trim();
                    String[] text = temp.split(",");
                    if (text.length == 2) {
                        String fileName;
                        if (scheme == EmoticonBase.Scheme.DRAWABLE) {
                            if (text[0].contains(".")) {
                                fileName = scheme.toUri(text[0].substring(0, text[0].lastIndexOf(".")));
                            } else {
                                fileName = scheme.toUri(text[0]);
                            }
                        } else {
                            fileName = scheme.toUri(text[0]);
                        }
                        String content = text[1];
                        EmoticonBean bean = new EmoticonBean(eventType, fileName, content, content);
                        emojis.add(bean);
                    }
                }
            }
            return emojis;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static EmoticonSetBean ParseEmoticons(Context context, String path, EmoticonBase.Scheme scheme) throws IOException, XmlPullParserException {
        String arrayParentKey = "EmoticonBean";
        EmoticonSetBean emoticonSetBean = new EmoticonSetBean();
        ArrayList<EmoticonBean> emoticonList = new ArrayList<>();
        emoticonSetBean.setEmoticonList(emoticonList);
        EmoticonBean emoticonBeanTemp = null;

        EmoticonLoader emoticonLoader = EmoticonLoader.getInstance(context);
        InputStream inStream = emoticonLoader.getConfigStream(path, scheme);
        if (inStream == null) {
            throw new IOException("Read config.xml in emoticon directory failed");
        }

        boolean isChildCheck = false;
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(inStream, "UTF-8");
        int event = pullParser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                String sKeyName = pullParser.getName();
                if (isChildCheck) {
                    switch (sKeyName) {
                        case "eventType": {
                            String value = pullParser.nextText();
                            emoticonBeanTemp.setEventType(Integer.parseInt(value));
                            break;
                        }
                        case "iconUri": {
                            String value = pullParser.nextText();
                            emoticonBeanTemp.setIconUri(scheme.toUri(path + "/" + value));
                            break;
                        }
                        case "msgUri": {
                            String value = pullParser.nextText();
                            emoticonBeanTemp.setMsgUri(scheme.toUri(path + "/" + value));
                            break;
                        }
                        case "tag": {
                            String value = pullParser.nextText();
                            emoticonBeanTemp.setTag(value);
                            break;
                        }
                        case "name": {
                            String value = pullParser.nextText();
                            emoticonBeanTemp.setName(value);
                            break;
                        }
                    }
                } else {
                    switch (sKeyName) {
                        case "name": {
                            String value = pullParser.nextText();
                            emoticonSetBean.setName(value);
                            break;
                        }
                        case "line": {
                            String value = pullParser.nextText();
                            emoticonSetBean.setLine(Integer.parseInt(value));
                            break;
                        }
                        case "row": {
                            String value = pullParser.nextText();
                            emoticonSetBean.setRow(Integer.parseInt(value));
                            break;
                        }
                        case "iconUri": {
                            String value = pullParser.nextText();
                            emoticonSetBean.setIconUri(scheme.toUri(path + "/" + value));
                            break;
                        }
                        case "isShowDelBtn": {
                            String value = pullParser.nextText();
                            emoticonSetBean.setShowDelBtn(Integer.parseInt(value) == 1);
                            break;
                        }
                        case "itemPadding": {
                            String value = pullParser.nextText();
                            emoticonSetBean.setItemPadding(Integer.parseInt(value));
                            break;
                        }
                        case "horizontalSpacing": {
                            String value = pullParser.nextText();
                            emoticonSetBean.setHorizontalSpacing(Integer.parseInt(value));
                            break;
                        }
                        case "verticalSpacing": {
                            String value = pullParser.nextText();
                            emoticonSetBean.setVerticalSpacing(Integer.parseInt(value));
                            break;
                        }
                        case "isShowName": {
                            String value = pullParser.nextText();
                            emoticonSetBean.setIsShownName(Integer.parseInt(value) == 1);
                            break;
                        }
                    }
                }

                if (sKeyName.equals(arrayParentKey)) {
                    isChildCheck = true;
                    emoticonBeanTemp = new EmoticonBean();
                }
            } else if (event == XmlPullParser.END_TAG) {
                String ekeyName = pullParser.getName();
                if (isChildCheck && ekeyName.equals(arrayParentKey)) {
                    isChildCheck = false;
                    emoticonList.add(emoticonBeanTemp);
                }
            }
            event = pullParser.next();
        }
        return emoticonSetBean;
    }

    public static int getFontSize(float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) (Math.ceil(fm.bottom - fm.top) + 0.5);
    }

    /**
     * 开启软键盘
     */
    public static void openSoftKeyboard(EditText et) {
        InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et, 0);
    }

    /**
     * 关闭软键盘
     */
    public static void closeSoftKeyboard(Context context) {
        DisplayUtil.showInput(false,context);
//        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
//            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }
}
