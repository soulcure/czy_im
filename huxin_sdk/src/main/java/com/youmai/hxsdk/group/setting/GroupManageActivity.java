package com.youmai.hxsdk.group.setting;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.group.ChatGroupDetailsActivity;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;

import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2018.04.26 17:05
 * 描述: 群名设置
 */
public class GroupManageActivity extends SdkBaseActivity {

    public static final String GROUP_ID = "groupId";
    public static final String GROUP_NAME = "groupName";
    public static final String GROUP_LIST = "GROUP_LIST";
    public static final String IS_DIRECT = "is_direct";
    private static final String TAG_SEARCH_CONTACT_FRAGMENT = "groupManageFragment";

    private TextView tv_back, tv_title, tv_title_right;
    private RelativeLayout rl_trans_group;

    private String name;
    private int groupId;

    private GroupManageFragment groupManageFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_manage_setting);

        initView();
        initData();
        setClickListener();
    }

    private void initView() {
        ArrayList<ContactBean> list = GroupMembers.instance().getDelGroupList();

        tv_back = findViewById(R.id.tv_left_cancel);
        tv_back.setText(R.string.hx_back);

        tv_title = findViewById(R.id.tv_title);
        tv_title_right = findViewById(R.id.tv_right_sure);

        rl_trans_group = findViewById(R.id.rl_trans_group);
        groupManageFragment = new GroupManageFragment();
        groupManageFragment.setList(list);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_select_container, groupManageFragment, TAG_SEARCH_CONTACT_FRAGMENT);

        boolean isDirect = getIntent().getBooleanExtra(IS_DIRECT, false);
        if (isDirect) {
            tv_title.setText("转让群主");
            rl_trans_group.setVisibility(View.GONE);
            tv_title_right.setVisibility(View.VISIBLE);
        } else {
            tv_title.setText("群管理");
            rl_trans_group.setVisibility(View.VISIBLE);
            tv_title_right.setVisibility(View.GONE);
            transaction.hide(groupManageFragment);
        }

        transaction.commitAllowingStateLoss();
    }

    private void initData() {
        groupId = getIntent().getIntExtra(GROUP_ID, -1);
        name = getIntent().getStringExtra(GROUP_NAME);

    }

    private void setClickListener() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupManageFragment.isHidden()) {
                    finish();
                } else {
                    tv_title_right.setVisibility(View.GONE);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.hide(groupManageFragment);
                    transaction.commit();
                }
            }
        });

        rl_trans_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_title_right.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.show(groupManageFragment);
                transaction.commit();
            }
        });

        tv_title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(groupManageFragment.getOwnerId())) {
                    return;
                }
                ReceiveListener receiveListener = new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiGroup.GroupInfoModifyRsp ack = YouMaiGroup.GroupInfoModifyRsp.parseFrom(pduBase.body);
                            if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                setResult(ChatGroupDetailsActivity.RESULT_CODE);
                                finish();
                            } else {
                                Toast.makeText(mContext, "群主管理权转让失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                };

                HuxinSdkManager.instance().reqModifyGroupInfo(
                        groupId, groupManageFragment.getOwnerId(),
                        groupManageFragment.getOwnerName(),
                        "", "", "",
                        YouMaiGroup.GroupInfoModifyType.MODIFY_OWNER, receiveListener);
            }
        });


    }

}
