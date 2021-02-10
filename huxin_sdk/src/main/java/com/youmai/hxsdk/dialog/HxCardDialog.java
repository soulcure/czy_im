package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;

/**
 * Created by fylder on 2017/3/15.
 */

public class HxCardDialog extends Dialog {

    public HxCardDialog(Context context) {
        super(context);
    }

    public HxCardDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        HxCardDialog dialog;

        private Context context;
        private String title;
        private String content;
        private String submit;
        private String cancel;
        private int submitColor;
        private boolean isShowClose = false;
        private boolean isShowCancelButton = true;
        private boolean canceledOutside = true;

        private HxCardDialog.OnClickListener positiveClickListener;

        private ImageView closeImg;
        private TextView titleText;
        private TextView contentText;

        private Button positivebtn;
        private Button cancelBtn;
        private View clickBtnLine;


        public Builder(Context context) {
            this.context = context;
        }

        public HxCardDialog.Builder setOutSide(boolean canceledOutside) {
            this.canceledOutside = canceledOutside;
            return this;
        }


        public HxCardDialog.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置dialog信息
         *
         * @param title 资源内容
         */
        public HxCardDialog.Builder setTitle(@StringRes int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public HxCardDialog.Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public HxCardDialog.Builder setContent(@StringRes int content) {
            this.content = (String) context.getText(content);
            return this;
        }

        public HxCardDialog.Builder setSubmit(String submit) {
            this.submit = submit;
            return this;
        }

        public HxCardDialog.Builder setSubmit(String submit, @ColorRes int color) {
            this.submit = submit;
            this.submitColor = color;
            return this;
        }

        public HxCardDialog.Builder setSubmit(@StringRes int submit) {
            this.submit = (String) context.getText(submit);
            return this;
        }

        public HxCardDialog.Builder setCancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public HxCardDialog.Builder setCancel(@StringRes int cancel) {
            this.cancel = (String) context.getText(cancel);
            return this;
        }

        public HxCardDialog.Builder setOnListener(HxCardDialog.OnClickListener listener) {
            this.positiveClickListener = listener;
            return this;
        }

        public HxCardDialog.Builder setShowClose(boolean showClose) {
            isShowClose = showClose;
            return this;
        }

        public HxCardDialog.Builder setShowCancelButton(boolean showCancel) {
            isShowCancelButton = showCancel;
            return this;
        }

        public void dismiss() {
            this.dialog.dismiss();
        }


        public HxCardDialog create() {
            if (dialog == null) {
                dialog = new HxCardDialog(context, R.style.PhoneDialog);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hx_dialog_card_lay, null);
            closeImg = (ImageView) view.findViewById(R.id.dialog_card_close_img);
            titleText = (TextView) view.findViewById(R.id.dialog_card_title);
            contentText = (TextView) view.findViewById(R.id.dialog_card_message);
            positivebtn = (Button) view.findViewById(R.id.dialog_card_submit_btn);
            cancelBtn = (Button) view.findViewById(R.id.dialog_card_cancel_btn);
            clickBtnLine = view.findViewById(R.id.dialog_card_btn_line);

            positivebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (positiveClickListener != null) {
                        positiveClickListener.onSubmitClick(dialog);
                    }
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (positiveClickListener != null) {
                        positiveClickListener.onCancelClick(dialog);
                    }
                }
            });
            closeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            if (isShowClose) {
                closeImg.setVisibility(View.VISIBLE);
            } else {
                closeImg.setVisibility(View.GONE);
            }

            if (isShowCancelButton) {
                cancelBtn.setVisibility(View.VISIBLE);
                clickBtnLine.setVisibility(View.VISIBLE);
            } else {
                cancelBtn.setVisibility(View.GONE);
                clickBtnLine.setVisibility(View.GONE);
            }

            dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (title != null) {
                titleText.setText(title);
            }

            if (content != null) {
                contentText.setText(content);
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
