package com.youmai.hxsdk.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.utils.FileUtils;


/**
 * Created by colin on 2016/9/12.
 * 用户秀选择素材
 */
public class FileLogFragment extends BaseFragment {

    public static final String TAG = FileLogFragment.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_log, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view) {
        TextView textView = (TextView) view.findViewById(R.id.tv_log);
        String path = FileConfig.getLogPaths();
        String filePath = path + "/Log.txt";

        String content = FileUtils.readFile(filePath);
        textView.setText(content);
    }


}
