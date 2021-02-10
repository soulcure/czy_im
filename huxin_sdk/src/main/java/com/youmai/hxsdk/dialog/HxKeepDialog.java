package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.youmai.hxsdk.R;

/**
 * Author:  yw
 * Date:    2017-11-16 17:55
 * Description:
 */
public class HxKeepDialog extends Dialog implements View.OnClickListener {

    public interface HxCallback {
        void onForward();

        void onDelete();
    }

    private View btn_collect_forward;
    private View btn_collect_delete;
    private View btnCancel;
    private Context mContext;

    private HxCallback callback;

    public HxKeepDialog(Context context) {
        super(context, R.style.hx_app_dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_keep_dialog);
        initView(savedInstanceState);
        setDialogFeature();
    }

    /**
     * 设置对话框特征
     */
    protected void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }


    public void initView(Bundle savedInstanceState) {
        btn_collect_forward = findViewById(R.id.btn_collect_forward);
        btn_collect_delete = findViewById(R.id.btn_collect_delete);
        btnCancel = findViewById(R.id.btn_collect_cancel);
        btn_collect_forward.setOnClickListener(this);
        btn_collect_delete.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_collect_forward) {//转发
            if (callback != null) {
                callback.onForward();
            }
            dismiss();
        } else if (i == R.id.btn_collect_cancel) {
            dismiss();
        } else if (i == R.id.btn_collect_delete) {
            if (callback != null) {
                callback.onDelete();
            }
            dismiss();
        }
    }

    public void setHxCollectDialog(HxCallback callback) {
        this.callback = callback;
    }
}
