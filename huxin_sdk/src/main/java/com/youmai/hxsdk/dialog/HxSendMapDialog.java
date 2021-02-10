package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.dialog.listener.SendDialogListener;

/**
 * Created by fylder on 2017/11/15.
 */

public class HxSendMapDialog extends Dialog {

    public HxSendMapDialog(Context context) {
        super(context);
    }

    public HxSendMapDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        HxSendMapDialog dialog;
        private Context context;

        private String title;
        private String submit;
        private String cancel;
        private int submitColor;

        private String address;
        private String location;
        private String picUrl;

        private boolean canceledOutside = true;

        private SendDialogListener positiveClickListener;

        private TextView titleText;
        private Button positiveBtn;
        private Button cancelBtn;

        private TextView addressText;
        private TextView locationText;
        private ImageView mapImg;


        public Builder(Context context) {
            this.context = context;
        }

        public HxSendMapDialog.Builder setOutSide(boolean canceledOutside) {
            this.canceledOutside = canceledOutside;
            return this;
        }


        public HxSendMapDialog.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置dialog信息
         *
         * @param title 资源内容
         */
        public HxSendMapDialog.Builder setTitle(@StringRes int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public HxSendMapDialog.Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public HxSendMapDialog.Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public HxSendMapDialog.Builder setPicUrl(String picUrl) {
            this.picUrl = picUrl;
            return this;
        }

        public HxSendMapDialog.Builder setSubmit(String submit) {
            this.submit = submit;
            return this;
        }

        public HxSendMapDialog.Builder setSubmit(String submit, @ColorRes int color) {
            this.submit = submit;
            this.submitColor = color;
            return this;
        }

        public HxSendMapDialog.Builder setSubmit(@StringRes int submit) {
            this.submit = (String) context.getText(submit);
            return this;
        }

        public HxSendMapDialog.Builder setCancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public HxSendMapDialog.Builder setCancel(@StringRes int cancel) {
            this.cancel = (String) context.getText(cancel);
            return this;
        }

        public HxSendMapDialog.Builder setOnListener(SendDialogListener listener) {
            this.positiveClickListener = listener;
            return this;
        }


        public void dismiss() {
            this.dialog.dismiss();
        }


        public HxSendMapDialog create() {
            if (dialog == null) {
                dialog = new HxSendMapDialog(context, R.style.PhoneDialog);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hx_dialog_send_map_lay, null);
            titleText = (TextView) view.findViewById(R.id.dialog_send_title);
            positiveBtn = (Button) view.findViewById(R.id.dialog_send_submit_btn);
            cancelBtn = (Button) view.findViewById(R.id.dialog_send_cancel_btn);

            addressText = (TextView) view.findViewById(R.id.dialog_send_map_address_text);
            locationText = (TextView) view.findViewById(R.id.dialog_send_map_location_text);
            mapImg = (ImageView) view.findViewById(R.id.dialog_send_map_img);

            positiveBtn.setOnClickListener(new View.OnClickListener() {
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

            if (address != null) {
                addressText.setText(address);
            }
            if (location != null) {
                locationText.setText(location);
            }
            if (picUrl != null) {
                Glide.with(context)
                        .load(picUrl)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .placeholder(R.drawable.image_placeholder))
                        .into(mapImg);
            }
            if (submit != null) {
                positiveBtn.setText(submit);
            }
            if (submitColor != 0) {
                positiveBtn.setTextColor(ContextCompat.getColor(context, submitColor));
            }
            if (cancel != null) {
                cancelBtn.setText(cancel);
            }
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(canceledOutside);
            return dialog;
        }

    }

}
