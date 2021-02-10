package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.youmai.hxsdk.R;

public class HxCallDialog extends Dialog implements View.OnClickListener {

    private String message;
    private View.OnClickListener onClickListener;

    private HxCallDialog(Builder builder) {
        super(builder.mContext, R.style.PhoneDialog);
        setCanceledOnTouchOutside(false);
        message = builder.message;
        onClickListener = builder.onClickListener;
    }


    public static class Builder {
        private Context mContext;
        private String message;
        private View.OnClickListener onClickListener;

        public HxCallDialog builder() {
            return new HxCallDialog(this);
        }

        public void setContext(Context context) {
            this.mContext = context;
        }

        public HxCallDialog.Builder setMessage(String msg) {
            this.message = msg;
            return this;
        }

        public HxCallDialog.Builder setOnClickListener(View.OnClickListener listener) {
            this.onClickListener = listener;
            return this;
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_dialog_call);
        //setDialogFeature();
        initView();
    }


    /**
     * 设置对话框特征
     */
    private void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
        }
    }

    private void initView() {
        TextView tv_msg = findViewById(R.id.tv_msg);

        if (!TextUtils.isEmpty(message)) {
            tv_msg.setText(message);
        }

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_call).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_call) {//调取摄像头的拍照功能
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
            dismiss();
        } else if (i == R.id.btn_cancel) {
            dismiss();
        }
    }

}
