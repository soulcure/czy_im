package com.youmai.hxsdk.module.movierecord;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.DisplayUtil;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-07-25 9:34
 * Description: 自定义toast
 */
public class MovieRecodeToast extends Toast {

    public static final int MODE_TIP_GREEN = 1;
    public static final int MODE_TIP_RED = 2;

    public MovieRecodeToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, int mode) {
        Toast result = new Toast(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.hxs_mr_tip, null);

        TextView textView = (TextView) layout.findViewById(R.id.tip_tv);

        if (MODE_TIP_GREEN == mode) {
            textView.setText(R.string.hxs_mr_tip_1);
            textView.setTextColor(context.getResources().getColor(R.color.hxs_mr_color_green));
        } else {
            textView.setText(R.string.hxs_mr_tip_2);
            textView.setTextColor(context.getResources().getColor(R.color.hxs_mr_color_red));
        }


        result.setView(layout);
        int m120 = DisplayUtil.dip2px(context, 120);
        result.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM, 0, m120);
        result.setDuration(Toast.LENGTH_LONG);

        return result;
    }

    private void setToast(int mode) {

    }
}
