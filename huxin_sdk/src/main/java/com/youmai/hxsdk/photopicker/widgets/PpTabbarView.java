package com.youmai.hxsdk.photopicker.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.DisplayUtil;

public class PpTabbarView extends RelativeLayout {

    private Context context;
    public static final String btn_back_tag = "btn_back_tag";
    public static final String commit_tag = "commit_tag";
    public static final String bottom_tv_ph_tag = "bottom_tv_ph_tag";
    private int actionBarHeight;

    public int getActionBarHeight() {
        return actionBarHeight;
    }

    public PpTabbarView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView() {
        setBackgroundColor(Color.parseColor("#1d1d1d"));

        TypedValue tvv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tvv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tvv.data, getResources().getDisplayMetrics());
        }

        int padding = DisplayUtil.dip2px(context, 20);
        int padding2 = DisplayUtil.dip2px(context, 10);
        TextView tv = new TextView(context);
        tv.setTag(btn_back_tag);
        tv.setText(R.string.hx_cancel);
        tv.setPadding(padding, padding2, padding, padding2);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(16);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_VERTICAL);
        addView(tv, params);

        TextView top_tv_ph = new TextView(context);
        top_tv_ph.setTag(bottom_tv_ph_tag);
        top_tv_ph.setText(R.string.hx_send);
        top_tv_ph.setTextSize(16);
        top_tv_ph.setTextColor(Color.parseColor("#ffffff"));
        top_tv_ph.setPadding(padding, padding2, padding, padding2);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_RIGHT);
        params.addRule(CENTER_VERTICAL);
        addView(top_tv_ph, params);

    }
}
