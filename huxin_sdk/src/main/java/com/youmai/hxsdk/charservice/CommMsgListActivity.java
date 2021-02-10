package com.youmai.hxsdk.charservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;


/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-11-07 10:31
 * Description:  im 界面
 */
public class CommMsgListActivity extends SdkBaseActivity {
    /*
     * Const.
     */
    public static final String TAG = CommMsgListActivity.class.getSimpleName();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_comm_msg_list);
        initView();
    }

    private void initView() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("社区消息");

        TextView tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView imgRight = (ImageView) findViewById(R.id.img_right);
        imgRight.setVisibility(View.GONE);


    }

}
