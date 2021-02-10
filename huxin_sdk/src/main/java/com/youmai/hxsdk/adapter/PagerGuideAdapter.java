package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class PagerGuideAdapter extends PagerAdapter {

    private Context mContext;
    private List<View> mList;


    public PagerGuideAdapter(Context context) {
        mContext = context;
    }

    public PagerGuideAdapter(Context context, List<View> list) {
        mContext = context;
        mList = list;
    }

    public void setList(List<View> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        arg0.removeView(mList.get(arg1));
    }


    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        arg0.addView(mList.get(arg1), 0);
        return mList.get(arg1);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == (arg1);
    }


}

