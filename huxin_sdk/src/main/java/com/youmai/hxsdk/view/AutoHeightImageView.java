package com.youmai.hxsdk.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by fylder on 2017/11/20.
 */

public class AutoHeightImageView extends AppCompatImageView {

    public AutoHeightImageView(Context context) {
        super(context);
    }

    public AutoHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            int width = drawable.getMinimumWidth();
            int height = drawable.getMinimumHeight();
            float scale = (float) width / height;

            //强制根据图片原有比例，重新计算ImageView显示区域宽度
            int heightMeasure = MeasureSpec.getSize(heightMeasureSpec);
            int widthMeasure = (int) (heightMeasure * scale);

            //并设置为MeasureSpec.EXACTLY精确模式保证之后的super.onMeasure()不再调整
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthMeasure, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}