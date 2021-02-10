package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.youmai.hxsdk.R;

/**
 * Author:  yw
 * Date:    2017-11-16 17:55
 * Description:
 */
public class HxPayErrorDialog extends Dialog implements View.OnClickListener {

    public interface PayErrorCallback {
        void onForget();

        void onTryAgain();
    }

    private PayErrorCallback callback;

    private TextView tv_msg;
    private Button btn_forget;
    private Button btn_again;


    public HxPayErrorDialog(Context context) {
        super(context, R.style.hx_app_dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_pay_error_dialog);
        initView();
    }


    private void initView() {
        tv_msg = findViewById(R.id.tv_msg);
        btn_forget = findViewById(R.id.btn_forget);
        btn_again = findViewById(R.id.btn_again);

        btn_forget.setOnClickListener(this);
        btn_again.setOnClickListener(this);

    }

    public void setMsg(String msg) {
        if (tv_msg != null) {
            tv_msg.setText(msg);
        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_forget) {
            if (callback != null) {
                callback.onForget();
            }
            dismiss();
        } else if (i == R.id.btn_again) {
            if (callback != null) {
                callback.onTryAgain();
            }
            dismiss();
        }
    }

    public void setPayErrorCallback(PayErrorCallback callback) {
        this.callback = callback;
    }
}
