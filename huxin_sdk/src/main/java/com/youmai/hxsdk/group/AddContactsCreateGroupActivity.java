package com.youmai.hxsdk.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.fragment.ContactsFragment;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.module.groupchat.ChatDetailsActivity;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 作者：create by YW
 * 日期：2018.04.19 09:41
 * 描述：创建群
 */
public class AddContactsCreateGroupActivity extends SdkBaseActivity
        implements SearchContactAdapter.ItemEventListener {

    public static final String GROUP_LIST = "GROUP_LIST";
    public static final String DETAIL_TYPE = "DETAIL_TYPE";
    public static final String GROUP_ID = "GROUP_ID";

    //广播
    public static final String BROADCAST_FILTER = "com.tg.coloursteward.searchcontact";
    public static final String ACTION = "contact_action";
    public static final String ADAPTER_CONTACT = "adapter";


    private TextView tv_title;
    private TextView tv_Cancel;
    private TextView tv_Sure;

    private Map<String, ContactBean> mGroupMap = new HashMap<>();
    private ArrayList<ContactBean> mContactList; //群组成员列表
    private int mDetailType; //详情的类型 1：单聊  2：群聊
    private int mGroupId; //群Id
    private int groupType = YouMaiBasic.GroupType.GROUP_TYPE_MULTICHAT_VALUE;

    private Map<String, ContactBean> mTotalMap = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts_layout);

        mDetailType = getIntent().getIntExtra(DETAIL_TYPE, -1);
        mGroupId = getIntent().getIntExtra(GROUP_ID, -1);
        groupType = getIntent().getIntExtra(IMGroupActivity.GROUP_TYPE, YouMaiBasic.GroupType.GROUP_TYPE_MULTICHAT_VALUE);

        mContactList = GroupMembers.instance().getGroupList();
        if (!ListUtils.isEmpty(mContactList)) {
            initGroupMap();
        }

        initView();
        setListener();
    }

    /**
     * 初始化
     */
    private void initView() {
        //标题
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("添加成员");

        tv_Cancel = findViewById(R.id.tv_back);
        tv_Sure = findViewById(R.id.tv_right);
        tv_Sure.setText("完成(" + 0 + ")");
        tv_Sure.setEnabled(false);

        ContactsFragment fragment = ContactsFragment.newInstance(true, mContactList);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, ContactsFragment.TAG);
        ft.commitAllowingStateLoss();
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


    @Override
    public void onDestroy() {
        /*if (!ListUtils.isEmpty(mContactList)) {
            mContactList.clear();
            mContactList = null;
        }*/
        if (null != mTotalMap) {
            mTotalMap.clear();
            mTotalMap = null;
        }
        if (null != mGroupMap) {
            mGroupMap.clear();
            mTotalMap = null;
        }
        super.onDestroy();
    }


    private void initGroupMap() {
        for (ContactBean contact : mContactList) {
            mGroupMap.put(contact.getUuid(), contact);
        }
    }


    public void done() {
        showProgressDialog();

        if (mDetailType == 1) {
            createGroup();
        } else if (mDetailType == 2) {
            updateGroup();
        }
    }

    private void setListener() {
        tv_Sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
        tv_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createGroup() {
        Map<String, ContactBean> data = mTotalMap;
        if (data != null && !data.isEmpty()) {
            List<YouMaiGroup.GroupMemberItem> list = new ArrayList<>();

            StringBuffer sb = new StringBuffer(ColorsConfig.GROUP_DEFAULT_NAME);
            int count = 0;
            if (!ListUtils.isEmpty(mContactList)) {
                for (ContactBean contact : mContactList) {
                    list.add(insertBuilder(contact).build());
                    if (!HuxinSdkManager.instance().getUuid().equals(contact.getUuid())) {
                        count++;
                        sb.append(contact.getDisplayName() + "、");
                    }
                }
            }

            for (Map.Entry<String, ContactBean> entry : data.entrySet()) {
                ContactBean item = entry.getValue();
                list.add(insertBuilder(item).build());
                if (count < 3) {
                    count++;
                    sb.append(item.getDisplayName() + "、");
                }
            }

            final String groupName = sb.deleteCharAt(sb.length() - 1).toString();
            HuxinSdkManager.instance().createGroup(groupName, list, new ReceiveListener() {
                @Override
                public void OnRec(PduBase pduBase) {
                    try {
                        YouMaiGroup.GroupCreateRsp ack = YouMaiGroup.GroupCreateRsp.parseFrom(pduBase.body);
                        if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                            List<YouMaiGroup.GroupMemberItem> list = ack.getMemberListList();
                            int groupId = ack.getGroupId();
                            GroupInfoBean groupInfo = new GroupInfoBean();
                            groupInfo.setGroup_id(groupId);
                            groupInfo.setGroup_name(groupName);
                            groupInfo.setGroup_member_count(list.size());

                            Intent intent = new Intent(mContext, IMGroupActivity.class);
                            intent.putExtra(IMGroupActivity.DST_NAME, groupName);
                            intent.putExtra(IMGroupActivity.DST_UUID, groupId);
                            intent.putExtra(IMGroupActivity.GROUP_INFO, groupInfo);

                            startActivity(intent);

                            Toast.makeText(mContext, "创建群成功", Toast.LENGTH_SHORT).show();


                            finish();
                            HuxinSdkManager.instance().getStackAct().finishActivity(IMConnectionActivity.class);
                            HuxinSdkManager.instance().getStackAct().finishActivity(ChatDetailsActivity.class);
                        } else {
                            Toast.makeText(mContext, "创建群失败", Toast.LENGTH_SHORT).show();
                        }

                        dismissProgressDialog();
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private YouMaiGroup.GroupMemberItem.Builder insertBuilder(ContactBean item) {
        YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
        builder.setMemberId(item.getUuid());
        builder.setMemberName(item.getDisplayName());
        builder.setUserName(item.getAvatar());
        if (HuxinSdkManager.instance().getUuid().equals(item.getUuid())) {
            builder.setMemberRole(0);
        } else {
            builder.setMemberRole(2);
        }
        return builder;
    }

    private void updateGroup() {
        List<YouMaiGroup.GroupMemberItem> list = new ArrayList<>();
        //删除成员
        for (Map.Entry<String, ContactBean> entry : mTotalMap.entrySet()) {
            ContactBean item = entry.getValue();

            if (TextUtils.isEmpty(item.getUuid())) {
                Toast.makeText(this, item.getDisplayName() + "的uuid为空，无法创建群", Toast.LENGTH_SHORT).show();
                return;
            }

            YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
            builder.setMemberId(item.getUuid());
            builder.setMemberName(item.getDisplayName());
            builder.setUserName(item.getAvatar());
            builder.setMemberRole(2);
            list.add(builder.build());
        }

        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberChangeRsp ack = YouMaiGroup.GroupMemberChangeRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        Toast.makeText(AddContactsCreateGroupActivity.this, "添加成员", Toast.LENGTH_SHORT).show();

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
                    }

                    dismissProgressDialog();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        HuxinSdkManager.instance().changeGroupMember(
                YouMaiGroup.GroupMemberOptType.GROUP_MEMBER_OPT_ADD,
                list, mGroupId, groupType, listener);
    }


    /**
     * item点击
     *
     * @param pos
     * @param contact
     */
    @Override
    public void onItemClick(int pos, ContactBean contact) {
        //Toast.makeText(this, "点击position：" + pos, Toast.LENGTH_SHORT).show();
        itemFunction(pos, contact);
    }

    /**
     * item 长按
     *
     * @param pos
     */
    @Override
    public void onLongClick(int pos) {

    }

    @Override
    public void collectCount(int count) {
        //tv_Sure.setText("完成(" + count + ")");
    }


    public Map<String, ContactBean> getTotalMap() {
        return mTotalMap;
    }

    public Map<String, ContactBean> getGroupMap() {
        return mGroupMap;
    }

    /**
     * 固定头item的跳转
     *
     * @param pos
     * @param contact
     */
    private void itemFunction(int pos, ContactBean contact) {

        int type = contact.getUiType();

        if (type == SearchContactAdapter.TYPE.ORGANIZATION_TYPE.ordinal()) {
        } else if (type == SearchContactAdapter.TYPE.DEPARTMENT_TYPE.ordinal()) {
        }
    }


}
