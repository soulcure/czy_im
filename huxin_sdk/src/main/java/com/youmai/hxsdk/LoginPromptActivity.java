package com.youmai.hxsdk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.utils.AppUtils;

import java.net.InetSocketAddress;


/**
 * Created by Administrator on 2016/7/19.
 * SDK 登录页面
 */
public class LoginPromptActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_prompt_login);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);

        TextView tv_content = (TextView) findViewById(R.id.tv_content);

        String msg_start = getString(R.string.relogin_msg_start);
        String userName = HuxinSdkManager.instance().getUserName();
        String msg_content = getString(R.string.relogin_msg);

        String content = msg_start + userName + msg_content;

        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.hxs_color_blue1)),
                msg_start.length(), msg_start.length() + userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_content.setText(style);

        Intent in = new Intent(this, HuxinService.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.setAction(HuxinService.IM_LOGIN_OUT);
        startService(in);//启动服务
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            HuxinSdkManager.instance().getStackAct().finishAllActivity();

            HuxinSdkManager.instance().loginOut();//清楚用户信息

            ARouter.getInstance().build(APath.RE_LOGIN)
                    .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP)  //添加Flag
                    .withBoolean("login_out", true)
                    .navigation(this);

            finish();

        } else if (id == R.id.tv_confirm) {
            String uuid = HuxinSdkManager.instance().getUuid();

            if (!TextUtils.isEmpty(uuid)) {
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("正在重新登录，请稍后...");
                dialog.show();

                String ip = AppUtils.getStringSharedPreferences(this, "IP", AppConfig.getSocketHost());
                int port = AppUtils.getIntSharedPreferences(this, "PORT", AppConfig.getSocketPort());

                InetSocketAddress isa = new InetSocketAddress(ip, port);
                HuxinSdkManager.instance().connectTcp(uuid, isa);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        finish();
                    }
                }, 1000);

            }


        }
    }


    /*@Override
    public void onBackPressed() {

    }*/


}
