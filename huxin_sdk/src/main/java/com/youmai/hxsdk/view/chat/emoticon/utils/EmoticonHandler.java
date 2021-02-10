package com.youmai.hxsdk.view.chat.emoticon.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonBean;
import com.youmai.hxsdk.view.chat.emoticon.db.EmoticonDBHelper;
import com.youmai.hxsdk.view.chat.emoticon.view.VerticalImageSpan;

import java.util.ArrayList;


/**
 * EmoticonHandler
 * Created by 90Chris on 2015/11/25.
 */
public class EmoticonHandler {
    private static ArrayList<EmoticonBean> mEmoticonBeans = new ArrayList<>();
    private static EmoticonHandler sEmoticonHandler = null;
    private Context mContext;
    EmoticonDBHelper emoticonDbHelper = null;

    public static EmoticonHandler getInstance(@NonNull Context context) {
        if (sEmoticonHandler == null) {
            sEmoticonHandler = new EmoticonHandler(context.getApplicationContext());
        }
        return sEmoticonHandler;
    }

    private EmoticonHandler(Context context) {
        mContext = context;
        emoticonDbHelper = new EmoticonDBHelper(context);
    }

    public EmoticonDBHelper getEmoticonDbHelper() {
        if (emoticonDbHelper == null) {
            emoticonDbHelper = new EmoticonDBHelper(mContext);
        }
        return emoticonDbHelper;
    }

    public ArrayList<EmoticonBean> loadEmoticonsToMemory() {
        if (emoticonDbHelper != null) {
            mEmoticonBeans = emoticonDbHelper.queryAllEmoticonBeans();
            emoticonDbHelper.cleanup();
        }
        return mEmoticonBeans;
    }

    public String getEmoticonUriByTag(String tag) {
        return emoticonDbHelper.getUriByTag(tag);
    }

    public void setTextFace(String content, Editable spannable, int start, int size) {
        if (mEmoticonBeans.size() == 0) {
            mEmoticonBeans = loadEmoticonsToMemory();
        }
        if (content.length() <= 0) {
            return;
        }
        int keyIndex = start;
        String emotionStr = content.substring(start);
        if (emotionStr.contains("[") && emotionStr.contains("]")) {
            if (mEmoticonBeans != null) {
                for (EmoticonBean bean : mEmoticonBeans) {
                    String key = bean.getTag();
                    int keyLength = key.length();
                    while (keyIndex >= 0) {
                        keyIndex = content.indexOf(key, keyIndex);  //when do not find, get -1
                        if (keyIndex < 0) {
                            break;
                        }
                        Drawable drawable = EmoticonLoader.getInstance(mContext).getDrawable(bean.getIconUri());
                        drawable.setBounds(0, 0, size, size);
                        VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
                        ImageSpan imageSpan1 = new ImageSpan(drawable);
                        spannable.setSpan(imageSpan, keyIndex, keyIndex + keyLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        keyIndex += keyLength;
                    }
                    keyIndex = start;
                }
            }
        } else {
            //注定匹配不到表情
            if (start > 190 && emotionStr.contains("[")) {
                spannable.delete(start, content.length());
            }
        }
    }

    /**
     * 过滤String存在的表情
     *
     * @param content
     * @param spannable
     * @param start
     * @param size
     * @return
     */
    public SpannableString getTextFace(String content, SpannableString spannable, int start, int size) {
        if (mEmoticonBeans.size() == 0) {
            mEmoticonBeans = loadEmoticonsToMemory();
        }
        if (content.length() <= 0) {
            return spannable;
        }
        int keyIndex = start;
        if (mEmoticonBeans != null) {
            for (EmoticonBean bean : mEmoticonBeans) {
                String key = bean.getTag();
                int keyLength = key.length();
                while (keyIndex >= 0) {
                    keyIndex = content.indexOf(key, keyIndex);  //when do not find, get -1
                    if (keyIndex < 0) {
                        break;
                    }
                    Drawable drawable = EmoticonLoader.getInstance(mContext).getDrawable(bean.getIconUri());
                    drawable.setBounds(0, 0, size, size);
                    VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
                    spannable.setSpan(imageSpan, keyIndex, keyIndex + keyLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    keyIndex += keyLength;
                }
                keyIndex = start;
            }
        }
        return spannable;
    }
}
