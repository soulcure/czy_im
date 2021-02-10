package com.youmai.hxsdk.im.voice.manager;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.DisplayUtil;

/**
 * Created by admin on 10/12/16.
 */
public class DialogManager {

    private Context mContext;

    private Dialog mDialog;


    private ImageView voiceIV;
    private View voiceLy;
    private View cancelLy;


    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        setDialogFeature();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.hx_dialog_im_voice, null);
        mDialog.setContentView(view);

        voiceLy = mDialog.findViewById(R.id.voice_ly);
        voiceIV = (ImageView) mDialog.findViewById(R.id.voice_iv);
        cancelLy = mDialog.findViewById(R.id.cancel_ly);

        mDialog.show();
    }

    protected void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.y -= DisplayUtil.dip2px(mContext, 70); // 新位置Y坐标
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    public void dismissDialog() {
        if (mDialog == null || !mDialog.isShowing()) return;
        mDialog.dismiss();
        mDialog = null;
    }

    public void cancel() {
        if (mDialog == null || !mDialog.isShowing()) return;

        cancelLy.setVisibility(View.VISIBLE);
        voiceLy.setVisibility(View.GONE);
    }

    public void recording() {
        if (mDialog == null || !mDialog.isShowing()) return;

        cancelLy.setVisibility(View.GONE);
        voiceLy.setVisibility(View.VISIBLE);

    }

    public void updateVolume(int level) {
        if (mDialog == null || !mDialog.isShowing()) return;

        int resId = mContext.getResources().getIdentifier("hx_im_v"+level, "drawable", mContext.getPackageName());
        voiceIV.setImageResource(resId);
    }
}
