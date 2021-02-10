package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;

/**
 * 录音提示窗
 * Created by fylder on 2017/7/19.
 */

public class HxRecordDialog extends Dialog {

    private ImageView tipImg;
    private TextView tipTimeText;
    private TextView tipText;
    Context mContext;

    private boolean isLess = false;

    public HxRecordDialog(Context context) {
        this(context, R.style.RecordDialogTheme);
    }

    public HxRecordDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hx_dialog_record_lay, null);
        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(view);
        setCanceledOnTouchOutside(false);
        tipImg = (ImageView) view.findViewById(R.id.hx_dialog_record_tip_img);
        tipTimeText = (TextView) view.findViewById(R.id.hx_dialog_record_tip_remaining_time_text);
        tipText = (TextView) view.findViewById(R.id.hx_dialog_record_tip_text);
    }

    /**
     * 是否少于1秒
     */
    public boolean isLess() {
        return isLess;
    }

    /**
     * 录音情况
     */
    public void setRecording(float radius) {
        int level = 1;
        int imgLevelRes = R.drawable.hx_im_voice_recording_icon;
        //声音分贝等级划分
        if (radius <= level) {
            imgLevelRes = R.drawable.hx_im_voice_recording_icon;
        } else if (radius > level && radius <= level * 2) {
            imgLevelRes = R.drawable.hx_im_voice_recording2_icon;
        } else if (radius > level * 2 && radius <= level * 3) {
            imgLevelRes = R.drawable.hx_im_voice_recording3_icon;
        } else if (radius > level * 3 && radius <= level * 4) {
            imgLevelRes = R.drawable.hx_im_voice_recording4_icon;
        } else if (radius > level * 4) {
            imgLevelRes = R.drawable.hx_im_voice_recording5_icon;
        }

        tipImg.setVisibility(View.VISIBLE);
        tipTimeText.setVisibility(View.GONE);
        tipImg.setImageResource(imgLevelRes);
        tipText.setText(R.string.hx_recording_tip);
        tipText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_transparent));
        isLess = false;
    }

    /**
     * 取消情况
     */
    public void setCancel() {
        tipImg.setVisibility(View.VISIBLE);
        tipTimeText.setVisibility(View.GONE);
        tipImg.setImageResource(R.drawable.hx_im_voice_cancel_icon);
        tipText.setText(R.string.hx_recording_tip2);
        tipText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.hx_record_tip_bg));
        isLess = false;
    }

    public void setRemainingTime(int time) {
        tipImg.setVisibility(View.GONE);
        tipTimeText.setVisibility(View.VISIBLE);
        String t = time + "";
        tipTimeText.setText(t);
        tipText.setText(R.string.hx_recording_tip3);
        tipText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_transparent));
        isLess = false;
    }

    /**
     * 超时情况
     */
    public void setLongTime() {
        tipImg.setVisibility(View.GONE);
        tipTimeText.setVisibility(View.VISIBLE);
//        tipImg.setImageResource(R.drawable.hx_full_success);
        tipTimeText.setText("!");
        tipText.setText(R.string.hx_recording_tip4);
        tipText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_transparent));
        isLess = false;
    }

    /**
     * 超时情况
     */
    public void setLessTime() {
        tipImg.setVisibility(View.GONE);
        tipTimeText.setVisibility(View.VISIBLE);
        tipTimeText.setText("!");
        tipText.setText(R.string.hx_recording_tip5);
        tipText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_transparent));
        isLess = true;
    }

}
