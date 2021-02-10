package com.youmai.hxsdk.view.chat.emoticon.utils;


import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonBean;

public interface IView {

    void onItemClick(EmoticonBean bean);

    void onPageChangeTo(int position);
}
