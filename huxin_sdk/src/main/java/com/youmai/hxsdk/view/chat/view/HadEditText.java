package com.youmai.hxsdk.view.chat.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.utils.Utils;


public class HadEditText extends AppCompatEditText {
    private Context mContext;

    public HadEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public HadEditText(Context context) {
        super(context);
        mContext = context;
    }

    public HadEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean performClick() {
        // do what you want
        return true;
    }


    @Override
    protected void onTextChanged(CharSequence arg0, int start, int lengthBefore, int after) {
        super.onTextChanged(arg0, start, lengthBefore, after);
        boolean isAdd = false;
        if (lengthBefore == 0 && after > 0) {
            isAdd = true;
        }

        if (onTextChangedInterface != null) {
            onTextChangedInterface.onTextChanged(arg0, isAdd);
        }
        String content = arg0.subSequence(0, start + after).toString();
        EmoticonHandler.getInstance(mContext).setTextFace(content, getText(), start, Utils.getFontSize(getTextSize()));
    }

    public interface OnTextChangedInterface {
        void onTextChanged(CharSequence argo, boolean isAdd);
    }

    OnTextChangedInterface onTextChangedInterface;

    public void setOnTextChangedInterface(OnTextChangedInterface i) {
        onTextChangedInterface = i;
    }
}
