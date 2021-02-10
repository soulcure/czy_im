package com.youmai.hxsdk.module.filemanager.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * 作者：create by YW
 * 日期：2017.06.23 09:49
 * 描述：Base基类 -> 区别弹屏的SdkBaseActivity
 */
public abstract class SdkHomeActivity extends AppCompatActivity {

    protected Context mContext;
    protected boolean isPreview = true; //一级一级返回

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();

        initData();
        initView();
        bindData();
        loadData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        bindClick();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isPreview = false;
    }

    /**
     * 获取传递bundle数据
     */
    public void initData() {

    }

    /**
     * 初始化布局view
     */
    public abstract void initView();

    /**
     * 布局绑定初始化的数据：eg: title.setTextView("Hello world!");
     */
    public void bindData() {

    }

    /**
     * 网络请求
     */
    public void loadData() {

    }

    /**
     * 设置view事件点击
     */
    public void bindClick() {

    }



}
