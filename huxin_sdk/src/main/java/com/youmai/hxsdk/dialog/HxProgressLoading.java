package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youmai.hxsdk.R;

/**
 * 加载提示窗
 * Created by fylder on 2017/5/12.
 */

public class HxProgressLoading extends Dialog {


    public HxProgressLoading(Context context) {
        this(context, 0);

    }

    public HxProgressLoading(Context context, int theme) {
        super(context, theme);

    }


    public static class Builder {

        HxProgressLoading dialog;
        private Context context;

        private boolean canceledOutside = true;

        private String message = null;

        TextView showText;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOutSideCancel(boolean canceledOutside) {
            this.canceledOutside = canceledOutside;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public void dismiss() {
            this.dialog.dismiss();
        }

        public HxProgressLoading create() {
            if (dialog == null) {
                dialog = new HxProgressLoading(context, R.style.Theme_dialog);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hx_loading_lay, null);// 得到加载view
            showText = (TextView) view.findViewById(R.id.loading_text);
            dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if (!TextUtils.isEmpty(message)) {
                showText.setText(message);
            }

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(canceledOutside);
            return dialog;
        }
    }
}
