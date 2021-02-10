package com.youmai.hxsdk.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;


/**
 * Created by colin on 2016/7/19.
 */
public class BaseFragment extends Fragment {

    protected Activity mAct;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAct = getActivity();
    }



}
