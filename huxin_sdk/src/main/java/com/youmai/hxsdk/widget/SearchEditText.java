package com.youmai.hxsdk.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.youmai.hxsdk.R;

import java.util.Locale;

public class SearchEditText extends AppCompatEditText {

    private static boolean DEBUG = false;

    private Drawable mSearchDrawable;
    private Drawable mDeleteDrawable;

    public SearchEditText(Context context) {
        this(context, null);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSearchDrawable = getCompoundDrawables()[0];    // left top right bottom
        if (mSearchDrawable == null) {
            mSearchDrawable = ContextCompat.getDrawable(getContext(), R.drawable.hx_global_seach_n);
        }
        int mIntrinsicWidth = mSearchDrawable.getIntrinsicWidth();
        int mIntrinsicHeight = mSearchDrawable.getIntrinsicHeight();
        int width = (int) (mIntrinsicWidth * 0.8f);     // scale
        int height = (int) (mIntrinsicHeight * 0.8f);   // scale
        mSearchDrawable.setBounds(0, 0, dip2px(getContext(), 22), dip2px(getContext(), 22));

        if (DEBUG) {
            Locale locale = Locale.getDefault();
            String info = String.format(locale, "[(%d, %d), (%d, %d)]", mIntrinsicWidth, mIntrinsicHeight, width, height);
            Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();  // (96, 96), (76, 76)
        }

        mDeleteDrawable = getCompoundDrawables()[2];    // left top right bottom
        if (mDeleteDrawable == null) {
            mDeleteDrawable = ContextCompat.getDrawable(getContext(), R.drawable.hx_global_delete_n);
        }
        mIntrinsicWidth = mDeleteDrawable.getIntrinsicWidth();
        mIntrinsicHeight = mDeleteDrawable.getIntrinsicHeight();
        width = (int) (mIntrinsicWidth * 0.8f);
        height = (int) (mIntrinsicHeight * 0.8f);
        mDeleteDrawable.setBounds(0, 0, dip2px(getContext(), 22), dip2px(getContext(), 22));

        setDeleteDrawable(false);
        addTextChangedListener(new MiddleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setDeleteDrawable(s.length() > 0);
            }
        });
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                boolean visible = hasFocus && (getText().length() > 0);
                setDeleteDrawable(visible);
            }
        });
    }

    public void setDeleteDrawable(boolean visible) {
        Drawable right = visible ? mDeleteDrawable : null;
        setCompoundDrawables(mSearchDrawable, null, right, null);   // firstly, setBounds()
//        setCompoundDrawablesWithIntrinsicBounds(mSearchDrawable, null, right, null); // IntrinsicBounds
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDeleteDrawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int left = getWidth() - getPaddingRight() - mDeleteDrawable.getIntrinsicWidth();
            int right = getWidth() - getPaddingRight();
            if (DEBUG) {
                Toast.makeText(getContext(), "left, right = " + left + ", " + right, Toast.LENGTH_SHORT).show();
            }
            if (event.getX() >= left && event.getX() <= right) {
                this.setText("");
            }
            // getRawX/getRawY - no right
//            int eventX = (int) event.getRawX();
//            int eventY = (int) event.getRawY();
//            Toast.makeText(getContext(), "eventX, eventY = " + eventX + ", " + eventY, Toast.LENGTH_SHORT).show();
//            Rect rect = new Rect();
//            getGlobalVisibleRect(rect);         //
//            rect.left = rect.right - 50;
//            if(rect.contains(eventX, eventY)){
//                setText("");
//            }
        }

        return super.onTouchEvent(event);
    }

    public static class MiddleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
