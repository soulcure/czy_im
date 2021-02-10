package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.youmai.hxsdk.R;

/*
 * 自定义圆角的dialog
 */
public class HxAddFriendDialog extends Dialog implements View.OnClickListener {


    private EditText ed_remark;
    private CallBack callBack;

    public interface CallBack {
        void onConfirm(String remark);
    }


    public static class Builder {
        private Context context;
        private CallBack callBack;

        public HxAddFriendDialog builder() {
            return new HxAddFriendDialog(this);
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCallBack(CallBack callBack) {
            this.callBack = callBack;
            return this;
        }

    }


    public HxAddFriendDialog(Builder builder) {
        super(builder.context, R.style.red_packet_dialog);
        callBack = builder.callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_friend);
        setDialogFeature();
        initView();
    }

    /**
     * 设置对话框特征
     */
    private void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);
    }


    private void initView() {
        ed_remark = findViewById(R.id.ed_remark);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.tv_confirm) {
            if (callBack != null) {
                callBack.onConfirm(ed_remark.getText().toString());
            }
            dismiss();
        }
    }


}