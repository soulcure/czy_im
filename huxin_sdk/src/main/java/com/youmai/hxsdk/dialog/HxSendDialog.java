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
public class HxSendDialog extends Dialog {

    public HxSendDialog(Context context) {
        super(context);
    }

    public HxSendDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        HxSendDialog dialog;

        private Context context;
        private String title;
        private String content;
        private String picUrl;
        private int emotionRes = 0;
        private String emotionUri;
        private String submit;
        private String cancel;
        private int submitColor;

        private boolean canceledOutside = true;

        private HxSendDialog.OnClickListener positiveClickListener;

        private TextView titleText;
        private TextView contentText;
        private GifImageView picImg;

        private Button positivebtn;
        private Button cancelBtn;


        public Builder(Context context) {
            this.context = context;
        }

        public HxSendDialog.Builder setOutSide(boolean canceledOutside) {
            this.canceledOutside = canceledOutside;
            return this;
        }


        public HxSendDialog.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置dialog信息
         *
         * @param title 资源内容
         */
        public HxSendDialog.Builder setTitle(@StringRes int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public HxSendDialog.Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public HxSendDialog.Builder setContent(@StringRes int content) {
            this.content = (String) context.getText(content);
            return this;
        }

        public HxSendDialog.Builder setPicUrl(String picUrl) {
            this.picUrl = picUrl;
            return this;
        }

        public HxSendDialog.Builder setEmotionRes(@DrawableRes int emotionRes) {
            this.emotionRes = emotionRes;
            return this;
        }

        public HxSendDialog.Builder setEmotionUri(String emotionUri) {
            this.emotionUri = emotionUri;
            return this;
        }

        public HxSendDialog.Builder setSubmit(String submit) {
            this.submit = submit;
            return this;
        }

        public HxSendDialog.Builder setSubmit(String submit, @ColorRes int color) {
            this.submit = submit;
            this.submitColor = color;
            return this;
        }

        public HxSendDialog.Builder setSubmit(@StringRes int submit) {
            this.submit = (String) context.getText(submit);
            return this;
        }

        public HxSendDialog.Builder setCancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public HxSendDialog.Builder setCancel(@StringRes int cancel) {
            this.cancel = (String) context.getText(cancel);
            return this;
        }

        public HxSendDialog.Builder setOnListener(HxSendDialog.OnClickListener listener) {
            this.positiveClickListener = listener;
            return this;
        }


        public void dismiss() {
            this.dialog.dismiss();
        }


        public HxSendDialog create() {
            if (dialog == null) {
                dialog = new HxSendDialog(context, R.style.PhoneDialog);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hx_dialog_send_lay, null);
            titleText = (TextView) view.findViewById(R.id.dialog_send_title);
            contentText = (TextView) view.findViewById(R.id.dialog_send_message);
            picImg = (GifImageView) view.findViewById(R.id.dialog_send_pic);
            positivebtn = (Button) view.findViewById(R.id.dialog_send_submit_btn);
            cancelBtn = (Button) view.findViewById(R.id.dialog_send_cancel_btn);

            if (TextUtils.isEmpty(picUrl) && emotionRes == 0) {
                //显示文本
                picImg.setVisibility(View.GONE);
                contentText.setVisibility(View.VISIBLE);
            } else {
                //显示图片
                picImg.setVisibility(View.VISIBLE);
                contentText.setVisibility(View.GONE);
            }

            positivebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onSubmitClick(dialog);
                    }
                    dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onCancelClick(dialog);
                    }
                    dismiss();
                }
            });

            dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (title != null) {
                titleText.setText(title);
            }

            if (content != null) {
                contentText.setText(content);
            }
            if (picUrl != null) {
                Glide.with(context)
                        .load(picUrl)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .placeholder(R.drawable.image_placeholder))
                        .into(picImg);
            }
            if (emotionUri == null) {
                if (emotionRes != 0) {
                    picImg.setImageResource(emotionRes);
                }
            } else {
                Glide.with(context)
                        .load(emotionUri)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop())
                        .into(picImg);
            }
            if (submit != null) {
                positivebtn.setText(submit);
            }
            if (submitColor != 0) {
                positivebtn.setTextColor(ContextCompat.getColor(context, submitColor));
            }
            if (cancel != null) {
                cancelBtn.setText(cancel);
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
