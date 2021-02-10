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
public class HxMediaStoreDialog extends Dialog implements View.OnClickListener {


    public interface HxCallback {
        void onCallSavePhoto();

        void onSendtoFriend();
    }

    private View btn_sendto_friend;
    private View btn_save_photo;
    private View btnCancel;
    private Context mContext;

    private HxCallback callback;

    public HxMediaStoreDialog(Context context) {
        super(context, R.style.hx_app_dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_dialog_mediastore);
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
        btn_sendto_friend = findViewById(R.id.btn_send_to_friend);
        btn_save_photo = findViewById(R.id.btn_save_photo);
        btnCancel = findViewById(R.id.btn_cancel);
        btn_sendto_friend.setOnClickListener(this);
        btn_save_photo.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_save_photo) {//调取摄像头的拍照功能
            if (callback != null) {
                callback.onCallSavePhoto();
            }
            dismiss();
        } else if (i == R.id.btn_cancel) {
            dismiss();
        } else if (i == R.id.btn_send_to_friend) {
            if (callback != null) {
                callback.onSendtoFriend();
            }
            dismiss();
        }
    }

    public void setHxMediaStoreDialog(HxCallback callback) {
        this.callback = callback;
    }
}
