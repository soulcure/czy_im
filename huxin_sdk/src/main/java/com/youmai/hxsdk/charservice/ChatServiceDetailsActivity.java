package com.youmai.hxsdk.charservice;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.im.IMMsgManager;

import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2018.04.18 16:36
 * 描述：单聊详情
 */
public class ChatServiceDetailsActivity extends SdkBaseActivity {

    public static final String GROUP_LIST = "GROUP_LIST";
    public static final String DS_NAME = "DS_NAME";
    public static final String DS_USER_AVATAR = "DS_USER_AVATAR";
    public static final String DS_UUID = "DS_UUID";
    public static final String DS_USERNAME = "DS_USERNAME";

    private TextView mTvBack, mTvTitle;
    private RelativeLayout mClearRecords;

    private String uuid;
    private String avatar;
    private String realname;
    private String username;
    private boolean isClearUp;

    private ArrayList<ContactBean> groupList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_im_chat_service_details);

        initView();
        setOnClickListener();

        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != groupList) {
            groupList.clear();
        }

        GroupMembers.instance().destory();
        HuxinSdkManager.instance().getStackAct().removeActivity(this);
    }

    private void initView() {

        realname = getIntent().getStringExtra(DS_NAME);
        avatar = getIntent().getStringExtra(DS_USER_AVATAR);
        uuid = getIntent().getStringExtra(DS_UUID);
        username = getIntent().getStringExtra(DS_USERNAME);

        mTvBack = findViewById(R.id.tv_back);
        mTvTitle = findViewById(R.id.tv_title);

        ImageView img_right = findViewById(R.id.img_right);
        img_right.setVisibility(View.GONE);

        mClearRecords = findViewById(R.id.rl_clear_chat_records);

        mTvTitle.setText("聊天详情");


    }


    private void setOnClickListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mClearRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setMessage("确定要删除当前所有的聊天记录吗")
                        .setPositiveButton("清除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CacheMsgHelper.instance().deleteAllMsgAndSaveEntry(mContext, uuid);
                                        IMMsgManager.instance().removeBadge(uuid);
                                        isClearUp = true;
                                        Toast.makeText(mContext, "当前聊天记录删除成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (isClearUp) {
            setResult(IMConnectionActivity.RESULT_CODE_CLEAN);
        }
        finish();
    }
}
