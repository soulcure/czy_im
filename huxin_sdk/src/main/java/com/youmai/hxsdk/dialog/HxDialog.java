package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.youmai.hxsdk.R;

public class HxDialog extends Dialog implements View.OnClickListener {


    public interface HxCallback {
        void onItemOne();
        void onItemTwo();
    }

    private Button btn_item_one, btn_item_two;
    private View btnCancel;
    private Context mContext;

    private HxCallback callback;

    public HxDialog(Context context) {
        super(context, R.style.hx_app_dialog);
        setContentView(R.layout.hx_dialog_select);
        initView();
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

    public void initView() {
        btn_item_one = (Button) findViewById(R.id.btn_item_one);
        btn_item_two = (Button) findViewById(R.id.btn_item_two);
        btnCancel = findViewById(R.id.btn_cancel);
        btn_item_one.setOnClickListener(this);
        btn_item_two.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_item_one) {
            if (callback != null) {
                callback.onItemOne();
            }
            dismiss();
        } else if (i == R.id.btn_item_two) {
            if (callback != null) {
                callback.onItemTwo();
            }
            dismiss();
        } else if (i == R.id.btn_cancel) {
            dismiss();
        }
    }

    public HxDialog setItemOneString(String name) {
        btn_item_one.setText(name);
        return this;
    }

    public HxDialog setItemTwoString(String name, boolean isVisible) {
        if (isVisible) {
            btn_item_two.setVisibility(View.GONE);
        } else {
            btn_item_two.setText(name);
        }
        return this;
    }

    public void setHxDialog(HxCallback callback) {
        this.callback = callback;
    }
}
