package com.youmai.hxsdk.view.text;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;


/**
 * Created by fylder on 2017/3/7.
 */

public class CopyTextView extends AppCompatEditText {


    public CopyTextView(Context context) {
        super(context);
    }

    public CopyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CopyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected boolean getDefaultEditable() {//等同于在布局文件中设置 android:editable="false"
        return false;
    }

}