package com.youmai.hxsdk.picker;

import java.util.ArrayList;

public interface PickerManagerListener {
    void onItemSelected(int currentCount);
    void onSingleItemSelected(ArrayList<String> paths);
}