package com.youmai.hxsdk.module.filemanager.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.filemanager.utils.DPUtils;

/**
 * 作者：create by YW
 * 日期：2017.08.30 14:05
 * 描述：
 */
public class FileDownloadItemView extends LinearLayout {

    private Drawable drawable;
    private String nString;
    private String cString;

    private Context mContext;
    private TextView count;

    public FileDownloadItemView(Context context) {
        this(context, null);
    }

    public FileDownloadItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FileDownloadItemView);
        drawable = a.getDrawable(R.styleable.FileDownloadItemView_logo);
        nString = a.getString(R.styleable.FileDownloadItemView_name);
        cString = a.getString(R.styleable.FileDownloadItemView_count);
        a.recycle();

        initLayout(context);
    }

    private void initLayout(Context context) {
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(LinearLayout.HORIZONTAL);

        ImageView logo = new ImageView(context);
        logo.setImageDrawable(drawable);
        LayoutParams params = new LayoutParams(DPUtils.dip2px(context, 40), DPUtils.dip2px(context, 40));
        this.addView(logo, params);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        params = new LayoutParams(0, -2);
        params.setMargins(8, 0, 0, 0);
        params.weight = 1;
        this.addView(layout, params);

        TextView name = new TextView(context);
        name.setText(nString);
        name.setTextSize(15);
        name.setSingleLine();
        name.setTextColor(context.getResources().getColor(R.color.hxs_color_black4));
        layout.addView(name);

        count = new TextView(context);
        count.setText(cString);
        count.setTextColor(context.getResources().getColor(R.color.hxs_color_gray15));
        count.setTextSize(12);
        layout.addView(count);

        ImageView back = new ImageView(context);
        back.setImageResource(R.drawable.hx_app_next);
        params = new LayoutParams(-2, -2);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        this.addView(back, params);

    }

    public void setCountText(int num) {
        count.setText(num + mContext.getString(R.string.hx_file_manager_count));
    }
}
