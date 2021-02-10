package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.dialog.listener.SendDialogListener;
import com.youmai.hxsdk.utils.TimeUtils;

import java.text.NumberFormat;


/**
 * Created by fylder on 2017/11/17.
 */

public class HxSendVoiceDialog extends Dialog {

    public SeekBar seekBar;
    public ImageView playImg;
    public TextView timeText;

    private boolean isPlay = false;
    private boolean isPaurse = false;
    private int voiceTime = 0;

    public HxSendVoiceDialog(Context context) {
        super(context);
    }

    public HxSendVoiceDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    public static class Builder {

        HxSendVoiceDialog dialog;
        private Context context;

        private String title;
        private String submit;
        private String cancel;
        private int submitColor;

        private String time;

        private boolean canceledOutside = true;

        private SendDialogListener positiveClickListener;
        private PlayListener playListener;

        private TextView titleText;
        private Button positiveBtn;
        private Button cancelBtn;


        public Builder(Context context) {
            this.context = context;
        }

        public HxSendVoiceDialog.Builder setOutSide(boolean canceledOutside) {
            this.canceledOutside = canceledOutside;
            return this;
        }


        public HxSendVoiceDialog.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 设置dialog信息
         *
         * @param title 资源内容
         */
        public HxSendVoiceDialog.Builder setTitle(@StringRes int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public HxSendVoiceDialog.Builder setTime(String time) {
            this.time = time;
            return this;
        }

        public HxSendVoiceDialog.Builder setSubmit(String submit) {
            this.submit = submit;
            return this;
        }

        public HxSendVoiceDialog.Builder setSubmit(String submit, @StringRes int color) {
            this.submit = submit;
            this.submitColor = color;
            return this;
        }

        public HxSendVoiceDialog.Builder setSubmit(@StringRes int submit) {
            this.submit = (String) context.getText(submit);
            return this;
        }

        public HxSendVoiceDialog.Builder setCancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public HxSendVoiceDialog.Builder setCancel(@StringRes int cancel) {
            this.cancel = (String) context.getText(cancel);
            return this;
        }

        public HxSendVoiceDialog.Builder setOnListener(SendDialogListener listener) {
            this.positiveClickListener = listener;
            return this;
        }

        public HxSendVoiceDialog.Builder setPlayListener(PlayListener playListener) {
            this.playListener = playListener;
            return this;
        }

        public void dismiss() {
            this.dialog.dismiss();
        }


        public HxSendVoiceDialog create() {
            if (dialog == null) {
                dialog = new HxSendVoiceDialog(context, R.style.PhoneDialog);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hx_dialog_send_voice_lay, null);
            titleText = (TextView) view.findViewById(R.id.dialog_send_title);
            positiveBtn = (Button) view.findViewById(R.id.dialog_send_submit_btn);
            cancelBtn = (Button) view.findViewById(R.id.dialog_send_cancel_btn);

            dialog.playImg = (ImageView) view.findViewById(R.id.dialog_send_voice_play_img);
            dialog.seekBar = (SeekBar) view.findViewById(R.id.dialog_send_voice_seek);
            dialog.timeText = (TextView) view.findViewById(R.id.dialog_send_voice_time_text);

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
            dialog.playImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playListener != null) {
                        playListener.play();
                    }
                }
            });

            dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            try {
                float voiceF = Float.valueOf(time);
                dialog.voiceTime = Math.round(voiceF);//四舍五入为int
            } catch (Exception e) {
                dialog.voiceTime = -1;
            }

            if (title != null) {
                titleText.setText(title);
            }
            dialog.timeText.setText(TimeUtils.getTimeFromMillisecond(dialog.voiceTime * 1000));
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


    public void setPlay(boolean play) {
        isPlay = play;
    }

    private int t = 0;

    public void play() {
        if (!isPlay) {
            //开始
            isPlay = true;
            t = 0;
            timeText.setText(TimeUtils.getTimeFromMillisecond(0));
            seekBar.setProgress(0);
            playImg.setImageResource(R.drawable.hx_send_voice_stop_icon);
            runTime();
        } else {
            //暂停
            purse();
        }
    }

    private void runTime() {
        handler.postDelayed(timeRunnable, 1000);
    }

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlay) {
                t++;
                timeText.setText(TimeUtils.getTimeFromMillisecond(t * 1000));
                int p = percent(t, voiceTime);
                p = p > 100 ? 100 : p;
                seekBar.setProgress(p);
                runTime();
            }
        }
    };

    public void stop() {
        handler.removeCallbacks(timeRunnable);
        isPlay = false;
        timeText.setText(TimeUtils.getTimeFromMillisecond(voiceTime * 1000));
        seekBar.setProgress(100);
        playImg.setImageResource(R.drawable.hx_send_voice_play_icon);
    }

    public void restStart() {
        handler.removeCallbacks(timeRunnable);
        isPlay = false;
        isPaurse = false;
        t = 0;
        timeText.setText(TimeUtils.getTimeFromMillisecond(0));
        seekBar.setProgress(0);
        playImg.setImageResource(R.drawable.hx_send_voice_play_icon);
    }

    public void purse() {
        handler.removeCallbacks(timeRunnable);
        isPlay = false;
        isPaurse = true;
        playImg.setImageResource(R.drawable.hx_send_voice_play_icon);
    }

    public void resume() {
        handler.postDelayed(timeRunnable, 1000);
        isPlay = true;
        isPaurse = false;
        playImg.setImageResource(R.drawable.hx_send_voice_stop_icon);
    }

    /**
     * 获取百分比
     */
    public int percent(double p1, double p2) {
        try {
            if (p2 == 0) {
                return 0;
            }
            String str;
            double p3 = p1 / p2;
            NumberFormat nf = NumberFormat.getPercentInstance();
            nf.setMinimumFractionDigits(2);
            str = nf.format(p3);
            str = str.substring(0, str.length() - 1);
            return Math.round(Float.valueOf(str));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    Handler handler = new Handler(Looper.getMainLooper());

    public boolean isPaurse() {
        return isPaurse;
    }

    public interface PlayListener {
        void play();
    }

}
