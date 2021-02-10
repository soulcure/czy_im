package com.youmai.hxsdk.module.filemanager.interfaces;

import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2017.08.31 11:56
 * 描述：
 */
public interface PickerRefreshUIListener {
    void onRefresh(ArrayList<String> paths, int resultCode);
}
