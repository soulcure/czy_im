package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;

import pl.droidsonroids.gif.GifImageView;

/**
 * 转发dialog
 * 文字、表情、图片
 * Created by fylder on 2017/10/12.
 */
public class HxScoreDialog extends Dialog {

    private HxScoreDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        private HxScoreDialog dialog;

        private Context context;
        private String title;
        private String content;

        private boolean canceledOutside = true;

        private HxScoreDialog.OnClickListener positiveClickListener;

        private TextView titleText;
        private TextView contentText;


        public Builder(Context context) {
            this.context = context;
        }

        public HxScoreDialog.Builder setOutSide(boolean canceledOutside) {
            this.canceledOutside = canceledOutside;
            return this;
        }


        public HxScoreDialog.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置dialog信息
         *
         * @param title 资源内容
         */
        public HxScoreDialog.Builder setTitle(@StringRes int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public HxScoreDialog.Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public HxScoreDialog.Builder setContent(@StringRes int content) {
            this.content = (String) context.getText(content);
            return this;
        }

        public HxScoreDialog.Builder setOnListener(HxScoreDialog.OnClickListener listener) {
            this.positiveClickListener = listener;
            return this;
        }


        public void dismiss() {
            this.dialog.dismiss();
        }


        public HxScoreDialog create() {
            if (dialog == null) {
                dialog = new HxScoreDialog(context, R.style.PhoneDialog);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hx_dialog_score, null);
            titleText = (TextView) view.findViewById(R.id.dialog_send_title);
            contentText = (TextView) view.findViewById(R.id.dialog_send_message);

            dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (title != null) {
                titleText.setText(title);
            }

            if (content != null) {
                contentText.setText(content);
            }

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(canceledOutside);
            return dialog;
        }

    }


    /**
     * 提交回调
     */
    public interface OnClickListener {

        void onSubmitClick(DialogInterface dialog);

        void onCancelClick(DialogInterface dialog);
    }

}
