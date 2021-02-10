package com.youmai.hxsdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HuxinReceiver extends BroadcastReceiver {
    private static final String TAG = HuxinReceiver.class.getSimpleName();

    public static final String ACTION_START_SERVICE = "huxin.intent.action.START_SERVER";
    public static final String SHOW_FLOAT_VIEW = "huxin.intent.action.SHOW_FLOAT_VIEW";
    public static final String HIDE_FLOAT_VIEW = "huxin.intent.action.HIDE_FLOAT_VIEW";

    public static final String ACTION_PUSH_MSG = "huxin.intent.action.PUSH_MSG";
    public static final String ACTION_REMIND_MSG = "huxin.intent.action.REMIND_MSG";

    private static final String SEND_SMS_NUMBER1 = "10690895037128969322";  //发送短信验证码号码
    private static final String SEND_SMS_NUMBER2 = "10655025196509693226";  //发送短信验证码号码

    private static final int CODE_LEN = 4;   //短信验证码长度


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            try {
                Intent in = new Intent(context, HuxinService.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(in);//启动服务
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (action.equals(ACTION_START_SERVICE)
                || action.equals(Intent.ACTION_BOOT_COMPLETED)
                || action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            HuxinSdkManager.instance().init(context);

            Intent in = new Intent(context, HuxinService.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            in.setAction(HuxinService.BOOT_SERVICE);
            context.startService(in);//启动服务
        }
    }


    /**
     * 匹配短信中间的验证码
     *
     * @param message
     * @return
     */
    private String patternCode(String message) {
        String res = "";
        /* 正则匹配验证码 */
        String patternCoder = "(?<!\\d)\\d{" + CODE_LEN + "}(?!\\d)";
        if (StringUtils.isEmpty(message)) {
            return res;
        }

        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(message);
        if (matcher.find()) {
            res = matcher.group();
        }
        return res;
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        //for Build Tool 22,  Build.VERSION_CODES.M build error
        if (Build.VERSION.SDK_INT >= 23/*Build.VERSION_CODES.M*/) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

}