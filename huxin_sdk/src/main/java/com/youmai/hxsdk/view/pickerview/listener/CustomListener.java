package com.youmai.hxsdk.view.pickerview.listener;

import android.view.View;

/**
 * Created by KyuYi on 2017/3/2.
 * E-Mail:kyu_yi@sina.com
 * 功能：
 */

public interface CustomListener {
    void customLayout(View v);
    void customCurrentDate(String date);
    void customOptions(int options1, int options2, int options3);
}
