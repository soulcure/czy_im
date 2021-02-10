package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;


public class HxPayPasswordDialog extends Dialog implements View.OnClickListener {
    private TextView[] tvList;
    private String strPassword;     //输入的密码
    private int currentIndex = -1;    //用于记录当前输入密码格位置

    private TextView tv_money;
    private TextView tv_error;
    private OnPasswordInputFinish mOnPasswordInputFinish;


    public interface OnPasswordInputFinish {
        void inputFinish();

        void forgetPassWord();
    }


    public HxPayPasswordDialog(Context context) {
        super(context, R.style.wheel_dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay_password);
        setDialogFeature();
        initView();
    }

    /**
     * 设置对话框特征
     */
    private void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialog_anim_style); // 添加动画
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
    }


    public void initView() {
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_error = (TextView) findViewById(R.id.tv_error);
        findViewById(R.id.img_close).setOnClickListener(this);
        findViewById(R.id.tv_forget).setOnClickListener(this);

        tvList = new TextView[6];
        tvList[0] = (TextView) findViewById(R.id.pay_box1);
        tvList[1] = (TextView) findViewById(R.id.pay_box2);
        tvList[2] = (TextView) findViewById(R.id.pay_box3);
        tvList[3] = (TextView) findViewById(R.id.pay_box4);
        tvList[4] = (TextView) findViewById(R.id.pay_box5);
        tvList[5] = (TextView) findViewById(R.id.pay_box6);
        TextView[] tv = new TextView[10];
        tv[0] = (TextView) findViewById(R.id.pay_keyboard_zero);
        tv[1] = (TextView) findViewById(R.id.pay_keyboard_one);
        tv[2] = (TextView) findViewById(R.id.pay_keyboard_two);
        tv[3] = (TextView) findViewById(R.id.pay_keyboard_three);
        tv[4] = (TextView) findViewById(R.id.pay_keyboard_four);
        tv[5] = (TextView) findViewById(R.id.pay_keyboard_five);
        tv[6] = (TextView) findViewById(R.id.pay_keyboard_sex);
        tv[7] = (TextView) findViewById(R.id.pay_keyboard_seven);
        tv[8] = (TextView) findViewById(R.id.pay_keyboard_eight);
        tv[9] = (TextView) findViewById(R.id.pay_keyboard_nine);
        ImageView iv_del = (ImageView) findViewById(R.id.pay_keyboard_del);
        for (int i = 0; i < 10; i++) {
            tv[i].setOnClickListener(this);
        }
        iv_del.setOnClickListener(this);

        tvList[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    strPassword = "";     //每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱
                    for (int i = 0; i < 6; i++) {
                        strPassword += tvList[i].getText().toString().trim();
                    }

                    if (mOnPasswordInputFinish != null) {
                        mOnPasswordInputFinish.inputFinish();    //接口中要实现的方法，完成密码输入完成后的响应逻辑
                    }
                }
            }
        });
    }


    public void setMoney(String money) {
        if (tv_money != null)
            tv_money.setText(money);
    }

    public void pwError() {
        if (tv_error != null) {
            tv_error.setVisibility(View.VISIBLE);
        }

        for (TextView item : tvList) {
            item.setText("");
        }
        currentIndex = -1;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.pay_keyboard_one) {
            getPass("1");
        } else if (id == R.id.pay_keyboard_two) {
            getPass("2");
        } else if (id == R.id.pay_keyboard_three) {
            getPass("3");
        } else if (id == R.id.pay_keyboard_two) {
            getPass("2");
        } else if (id == R.id.pay_keyboard_three) {
            getPass("3");
        } else if (id == R.id.pay_keyboard_four) {
            getPass("4");
        } else if (id == R.id.pay_keyboard_five) {
            getPass("5");
        } else if (id == R.id.pay_keyboard_sex) {
            getPass("6");
        } else if (id == R.id.pay_keyboard_seven) {
            getPass("7");
        } else if (id == R.id.pay_keyboard_eight) {
            getPass("8");
        } else if (id == R.id.pay_keyboard_nine) {
            getPass("9");
        } else if (id == R.id.pay_keyboard_zero) {
            getPass("0");
        } else if (id == R.id.pay_keyboard_del) {
            if (currentIndex - 1 >= -1) {      //判断是否删除完毕————要小心数组越界
                tvList[currentIndex--].setText("");
            }
        } else if (id == R.id.tv_forget) {
            if (mOnPasswordInputFinish != null) {
                mOnPasswordInputFinish.forgetPassWord();
            }
            dismiss();
        } else if (id == R.id.img_close) {
            dismiss();
        }

    }

    public void getPass(String str) {
        if (currentIndex >= -1 && currentIndex < 5) {
            tvList[++currentIndex].setText(str);
        }

        if (tv_error.getVisibility() == View.VISIBLE) {
            tv_error.setVisibility(View.GONE);
        }

    }

    public String getStrPassword() {
        return strPassword;
    }

    //设置监听方法，在第6位输入完成后触发
    public void setOnFinishInput(OnPasswordInputFinish pass) {
        mOnPasswordInputFinish = pass;
    }
}
