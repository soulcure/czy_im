package com.youmai.hxsdk.utils;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;

/**
 * 作者：create by YW
 * 日期：2016.08.17 09:27
 * 描述：
 */
public class ToastUtil {

    private static Toast mToast;
    private static Toast mBottomToast;
    private static Toast mSaveToast;
    private static Toast mRepToast;
    private static Toast mPublicToast;

    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            if (mToast != null) {
                mHandler.removeCallbacks(r);
                mToast.cancel();
                mToast = null;
            }
        }
    };
    private static Runnable r2 = new Runnable() {
        public void run() {
            if (mSaveToast != null) {
                mSaveToast.cancel();
                mSaveToast = null;
            }
        }
    };

    private static Runnable r3 = new Runnable() {
        public void run() {
            if (mRepToast != null) {
                mHandler.removeCallbacks(r3);
                mRepToast.cancel();
                mRepToast = null;
            }
        }
    };

    private static Runnable r4 = new Runnable() {
        public void run() {
            if (mBottomToast != null) {
                mHandler.removeCallbacks(r4);
                mBottomToast.cancel();
                mBottomToast = null;
            }
        }
    };

    private static Runnable r5 = new Runnable() {
        public void run() {
            if (mPublicToast != null) {
                mHandler.removeCallbacks(r5);
                mPublicToast.cancel();
                mPublicToast = null;
            }
        }
    };

    public static void showToast(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hx_custom_toast_view, null);
        TextView text = (TextView) view.findViewById(R.id.toast_message);
        text.setText(message);
        if (mToast == null) {
            mToast = new Toast(context);
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.CENTER, 0, 0);//180
            mToast.setView(view);
        } else {
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setView(view);
        }
        mHandler.postDelayed(r, 1800);// 延迟1秒隐藏toast
        mToast.show();
    }

    public static void showSaveToast(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hx_custom_save_toast_lay, null);
        TextView text = (TextView) view.findViewById(R.id.save_toast_message);
        text.setText(message);
        mHandler.removeCallbacks(r2);
        if (mSaveToast == null) {
            mSaveToast = new Toast(context);
            mSaveToast.setDuration(Toast.LENGTH_LONG);
            mSaveToast.setGravity(Gravity.CENTER, 0, 0);//180
            mSaveToast.setView(view);
        }
        mHandler.postDelayed(r2, 1800);// 延迟1秒隐藏toast
        mSaveToast.show();
    }

    public static void showBottomToast(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hx_custom_bottom_toast_view, null);
        TextView text = (TextView) view.findViewById(R.id.toast_message);
        text.setText(message);
        if (mBottomToast == null) {
            mBottomToast = new Toast(context);
            mBottomToast.setDuration(Toast.LENGTH_LONG);
            mBottomToast.setGravity(Gravity.BOTTOM, 0, DisplayUtil.dip2px(context, 75));//180
            mBottomToast.setView(view);
        } else {
            mBottomToast.setDuration(Toast.LENGTH_LONG);
            mBottomToast.setGravity(Gravity.BOTTOM, 0, DisplayUtil.dip2px(context, 75));
            mBottomToast.setView(view);
        }
        mHandler.postDelayed(r4, 1800);// 延迟1秒隐藏toast
        mBottomToast.show();
    }

    public static void showPublicToast(Context context, String message, int Res, int msgId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(Res, null);
        TextView text = (TextView) view.findViewById(msgId);
        text.setText(message);
        if (mPublicToast == null) {
            mPublicToast = new Toast(context);
            mPublicToast.setDuration(Toast.LENGTH_LONG);
            mPublicToast.setGravity(Gravity.CENTER, 0, 0);//180
            mPublicToast.setView(view);
        } else {
            mPublicToast.setDuration(Toast.LENGTH_LONG);
            mPublicToast.setGravity(Gravity.CENTER, 0, 0);
            mPublicToast.setView(view);
        }
        mHandler.postDelayed(r5, 2800);// 延迟1秒隐藏toast
        mPublicToast.show();
    }

    public static void showPublicToast2(Context context, @StringRes int textStr, @DrawableRes int imgRes) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hx_public_toast_tip, null);
        ImageView img = (ImageView) view.findViewById(R.id.public_toast_img);
        TextView text = (TextView) view.findViewById(R.id.public_toast_message);
        img.setImageResource(imgRes);
        text.setText(textStr);
        if (mPublicToast == null) {
            mPublicToast = new Toast(context);
            mPublicToast.setDuration(Toast.LENGTH_LONG);
            mPublicToast.setGravity(Gravity.CENTER, 0, 0);//180
            mPublicToast.setView(view);
        } else {
            mPublicToast.setDuration(Toast.LENGTH_LONG);
            mPublicToast.setGravity(Gravity.CENTER, 0, 0);
            mPublicToast.setView(view);
        }
        mHandler.postDelayed(r5, 1500);// 延迟1.5秒隐藏toast(最长3s)
        mPublicToast.show();
    }

}
