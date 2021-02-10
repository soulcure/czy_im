package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.dialog.listener.SendDialogListener;
import com.youmai.hxsdk.utils.DisplayUtil;

/**
 * Created by fylder on 2017/11/16.
 */

public class HxSendVideoDialog extends Dialog {

    public HxSendVideoDialog(Context context) {
        super(context);
    }

    public HxSendVideoDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        HxSendVideoDialog dialog;
        private Context context;

        private String title;
        private String submit;
        private String cancel;
        private int submitColor;

        private String frameUrl;
        private String time;

        private boolean canceledOutside = true;

        private SendDialogListener positiveClickListener;
        private PlayListener playListener;

        private TextView titleText;
        private Button positiveBtn;
        private Button cancelBtn;

        private TextView timeText;
        private ImageView playImg;
        private ImageView frameImg;


        public Builder(Context context) {
            this.context = context;
        }

        public HxSendVideoDialog.Builder setOutSide(boolean canceledOutside) {
            this.canceledOutside = canceledOutside;
            return this;
        }


        public HxSendVideoDialog.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置dialog信息
         *
         * @param title 资源内容
         */
        public HxSendVideoDialog.Builder setTitle(@StringRes int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public HxSendVideoDialog.Builder setFrameUrl(String frameUrl) {
            this.frameUrl = frameUrl;
            return this;
        }

        public HxSendVideoDialog.Builder setTime(String time) {
            this.time = time;
            return this;
        }

        public HxSendVideoDialog.Builder setSubmit(String submit) {
            this.submit = submit;
            return this;
        }

        public HxSendVideoDialog.Builder setSubmit(String submit, @ColorRes int color) {
            this.submit = submit;
            this.submitColor = color;
            return this;
        }

        public HxSendVideoDialog.Builder setSubmit(@StringRes int submit) {
            this.submit = (String) context.getText(submit);
            return this;
        }

        public HxSendVideoDialog.Builder setCancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public HxSendVideoDialog.Builder setCancel(@StringRes int cancel) {
            this.cancel = (String) context.getText(cancel);
            return this;
        }

        public HxSendVideoDialog.Builder setOnListener(SendDialogListener listener) {
            this.positiveClickListener = listener;
            return this;
        }

        public HxSendVideoDialog.Builder setPlayListener(PlayListener playListener) {
            this.playListener = playListener;
            return this;
        }

        public void dismiss() {
            this.dialog.dismiss();
        }


        public HxSendVideoDialog create() {
            if (dialog == null) {
                dialog = new HxSendVideoDialog(context, R.style.PhoneDialog);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hx_dialog_send_video_lay, null);
            titleText = (TextView) view.findViewById(R.id.dialog_send_title);
            positiveBtn = (Button) view.findViewById(R.id.dialog_send_submit_btn);
            cancelBtn = (Button) view.findViewById(R.id.dialog_send_cancel_btn);

            //content
            timeText = (TextView) view.findViewById(R.id.dialog_send_video_time_text);
            playImg = (ImageView) view.findViewById(R.id.dialog_send_video_play_img);
            frameImg = (ImageView) view.findViewById(R.id.dialog_send_video_img);

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
            playImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playListener != null) {
                        playListener.play();
                    }
                }
            });

            dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (title != null) {
                titleText.setText(title);
            }


            if (frameUrl != null) {

                Glide.with(context)
                        .asBitmap()
                        .load(frameUrl)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .placeholder(R.drawable.image_placeholder))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                int pw = resource.getWidth();
                                int ph = resource.getHeight();
                                int textWidth = DisplayUtil.dip2px(context, 200) * pw / ph;
                                frameImg.setImageBitmap(resource);
                                ViewGroup.LayoutParams layoutParams = timeText.getLayoutParams();
                                layoutParams.width = textWidth;
                                timeText.setLayoutParams(layoutParams);
                            }
                        });
//                Glide.with(context)
//                        .load(frameUrl)
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .crossFade()
//                        .placeholder(R.drawable.image_placeholder)
//                        .into(frameImg);
            }
            if (time != null) {
                timeText.setText(time);
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

    public interface PlayListener {
        void play();
    }

}
