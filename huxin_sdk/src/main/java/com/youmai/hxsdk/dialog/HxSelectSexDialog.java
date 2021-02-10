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
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-08-15 11:20
 * Description:
 */
public class HxSelectSexDialog extends Dialog implements View.OnClickListener {


    public interface HxCallback {
        void onSaveMale();
        void onSaveFemale();
    }

    private View btn_save_male, btn_save_female;
    private View btnCancel;
    private Context mContext;

    private HxCallback callback;

    public HxSelectSexDialog(Context context) {
        super(context, R.style.hx_app_dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_dialog_select_gender);
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

        btn_save_male = findViewById(R.id.btn_save_male);
        btn_save_female = findViewById(R.id.btn_save_female);
        btnCancel = findViewById(R.id.btn_cancel);
        btn_save_male.setOnClickListener(this);
        btn_save_female.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_save_male) {//选择性别男
            if (callback != null) {
                callback.onSaveMale();
            }
            dismiss();
        } else if (i == R.id.btn_save_female) { //选择性别女
            if (callback != null) {
                callback.onSaveFemale();
            }
            dismiss();
        } else if (i == R.id.btn_cancel) {
            dismiss();
        }
    }

    public void setHxSelectSexDialog(HxCallback callback) {
        this.callback = callback;
    }
}
