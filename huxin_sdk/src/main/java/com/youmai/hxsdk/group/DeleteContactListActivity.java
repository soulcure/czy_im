package com.youmai.hxsdk.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.fragment.GroupDelMemberFragment;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：create by YW
 * 日期：2018.04.12 13:54
 * 描述：删除群聊列表
 */
public class DeleteContactListActivity extends SdkBaseActivity {

    public static final String DELETE_GROUP_ID = "DELETE_GROUP_ID";

    private TextView tv_title;
    private TextView tv_Cancel;
    private TextView tv_Sure;

    private ArrayList<ContactBean> groupList; //群组成员列表
    private Map<String, ContactBean> mTotalMap = new HashMap<>();
    private int mGroupId = -1;
    private int groupType = YouMaiBasic.GroupType.GROUP_TYPE_MULTICHAT_VALUE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_contact_list);

        groupList = GroupMembers.instance().getDelGroupList();
        mGroupId = getIntent().getIntExtra(DELETE_GROUP_ID, -1);
        groupType = getIntent().getIntExtra(IMGroupActivity.GROUP_TYPE, YouMaiBasic.GroupType.GROUP_TYPE_MULTICHAT_VALUE);

        initView();
        setListener();
    }

    private void initView() {
        //标题
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("删除成员");

        tv_Cancel = findViewById(R.id.tv_back);
        tv_Sure = findViewById(R.id.tv_right);
        tv_Sure.setText("完成(" + 0 + ")");
        tv_Sure.setEnabled(false);

        GroupDelMemberFragment fragment = GroupDelMemberFragment.newInstance(true, groupList);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, GroupDelMemberFragment.TAG);
        ft.commitAllowingStateLoss();

    }


    private void setListener() {
        tv_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_Sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });


    }

    public void updateCacheMap(ContactBean contact) {
        if (mTotalMap.containsKey(contact.getUuid())) {
            mTotalMap.remove(contact.getUuid());
        } else {
            mTotalMap.put(contact.getUuid(), contact);
        }

        int count = mTotalMap.size();
        Log.d("YW", "map size: " + count);

        if (count > 0) {
            tv_Sure.setEnabled(true);
        } else {
            tv_Sure.setEnabled(false);
        }
        tv_Sure.setText("完成(" + count + ")");
    }


    private void done() {
        showProgressDialog();

        List<YouMaiGroup.GroupMemberItem> list = new ArrayList<>();
        //删除成员
        for (Map.Entry<String, ContactBean> entry : mTotalMap.entrySet()) {
            ContactBean item = entry.getValue();
            YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
            builder.setMemberId(item.getUuid());
            builder.setMemberName(item.getDisplayName());
            builder.setUserName(item.getAvatar());
            builder.setMemberRole(item.getMemberRole());
            list.add(builder.build());
        }

        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberChangeRsp ack = YouMaiGroup.GroupMemberChangeRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        Toast.makeText(mContext, "删除成员成功", Toast.LENGTH_SHORT).show();

                        ArrayList<ContactBean> list = new ArrayList<>();
                        for (Map.Entry<String, ContactBean> entry : mTotalMap.entrySet()) {
                            ContactBean item = entry.getValue();
                            list.add(item);
                        }
                        mTotalMap.clear();
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra(ChatGroupDetailsActivity.UPDATE_GROUP_LIST, list);
                        setResult(ChatGroupDetailsActivity.RESULT_CODE, intent);
                        finish();
                    } else {
                        Toast.makeText(mContext, "删除成员失败", Toast.LENGTH_SHORT).show();
                    }

                    dismissProgressDialog();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        if (mGroupId == -1) {
            Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
            return;
        }

        HuxinSdkManager.instance().changeGroupMember(
                YouMaiGroup.GroupMemberOptType.GROUP_MEMBER_OPT_DEL,
                list, mGroupId, groupType, listener);
    }


}
