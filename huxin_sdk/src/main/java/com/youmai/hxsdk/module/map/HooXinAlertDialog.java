package com.youmai.hxsdk.module.map;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.StringUtils;

public class HooXinAlertDialog extends Dialog {

    //UI
    private String title;
    private String message;
    private String LeftButtonText;
    private String RightButtonText;

    //Logic
    private OnClickListener LeftButtonClickListener;
    private OnClickListener RightButtonClickListener;

    public HooXinAlertDialog(Context context) {
        super(context, R.style.hx_sdk_dialog);
        setCancelable(false);
    }


    public HooXinAlertDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public HooXinAlertDialog setMessage(String message) {
        this.message = message;
        if (tv_message != null) {
            tv_message.setText(message);
        }
        return this;
    }

    public HooXinAlertDialog setLeftButtonText(String leftButtonText) {
        LeftButtonText = leftButtonText;
        return this;
    }

    public HooXinAlertDialog setRightButtonText(String rightButtonText) {
        RightButtonText = rightButtonText;
        return this;
    }

    public HooXinAlertDialog setLeftButtonClickListener(OnClickListener listener) {
        LeftButtonClickListener = listener;
        return this;
    }

    public HooXinAlertDialog setRightButtonClickListener(
            OnClickListener listener) {
        RightButtonClickListener = listener;
        return this;
    }

    private TextView tv_title, tv_message;
    private Button btn_left, btn_right;


    @Override
    public void show() {
        init();
        super.show();
    }


    public void init() {

        setContentView(R.layout.hx_message_dialog);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_message = (TextView) findViewById(R.id.tv_message);
        btn_left = (Button) findViewById(R.id.btn_msg_negative);
        btn_right = (Button) findViewById(R.id.btn_msg_positive);


        if (!StringUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(message)) {
            tv_message.setText(message);
        }

        if (!StringUtils.isEmpty(LeftButtonText)) {
            btn_left.setVisibility(View.VISIBLE);
            btn_left.setText(LeftButtonText);
            btn_left.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                    if (LeftButtonClickListener != null) {
                        LeftButtonClickListener.onClick(HooXinAlertDialog.this, -1);
                    }
                }
            });
        } else {
            btn_left.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(RightButtonText)) {
            btn_right.setVisibility(View.VISIBLE);
            btn_right.setText(RightButtonText);
            btn_right.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                    if (RightButtonClickListener != null)
                        RightButtonClickListener.onClick(HooXinAlertDialog.this, 1);

                }
            });

        } else {
            btn_right.setVisibility(View.INVISIBLE);
        }
    }


    public HooXinAlertDialog setCancelableDialog(boolean flag) {
        setCancelable(flag);
        return this;
    }

    public TextView getTv_message() {
        return tv_message;
    }


    public Button getBtn_left() {
        return btn_left;
    }

    public void setTextGravity(int gravity) {
        tv_message.setGravity(gravity);
    }

}
