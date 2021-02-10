package com.youmai.hxsdk.group;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.group.adapter.GroupDetailAdapter;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.db.helper.ContactHelper;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.fragment.GroupListFragment;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.group.setting.GroupManageActivity;
import com.youmai.hxsdk.group.setting.GroupNameActivity;
import com.youmai.hxsdk.group.setting.GroupNoticeActivity;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.23 17:32
 * 描述：群聊详情
 */
public class ChatGroupDetailsActivity extends SdkBaseActivity implements GroupDetailAdapter.ItemEventListener {

    public static final String GROUP_LIST = "GROUP_LIST";
    public static final String UPDATE_GROUP_LIST = "UPDATE_GROUP_LIST";

    private static final int REQUEST_CODE_ADD = 101;
    private static final int REQUEST_CODE_DELETE = 102;
    private static final int REQUEST_CODE_MODIFY_NAME = 103;
    private static final int REQUEST_CODE_MODIFY_NOTICE_TOPIC = 104;
    private static final int REQUEST_CODE_TRANS_OWNER = 105;
    private static final int REQUEST_CODE_TRANS_OWNER_EXIT_GROUP = 106;

    public static final int RESULT_CODE = 201;


    private static final int MODIFIY_GROUPNAME = 1;
    private static final int MODIFIY_GROUPTOPIC = 2;
    private static final int MODIFIY_GROUPADD = 3;
    private static final int MODIFIY_GROUPDEL = 4;


    private boolean isGroupOwner = false;  //是否群主

    private TextView mTvBack, mTvTitle;
    private RecyclerView mGridView;
    private RelativeLayout mRlGroupName;
    private RelativeLayout mRlGroupNotice;
    private RelativeLayout mRlGroupManage;
    private RelativeLayout mRlClearChatRecords;

    private Switch switch_notify;
    private Switch switch_top;

    private TextView mTvExitGroup;
    private TextView mTvGroupName;
    private TextView mtvNoticeContent;
    private TextView tv_count;
    private LinearLayout linear_next;

    private GroupDetailAdapter mAdapter;

    //private ArrayList<ContactBean> groupList = new ArrayList<>();
    //private ArrayList<ContactBean> delGroupList = new ArrayList<>();  //删除群成员，不包括群主

    private GroupInfoBean mGroupInfo;
    private int mGroupId;
    private String groupName;
    private int groupType;

    private boolean isClearUp;
    private boolean isMotifyGropInfo;

    private LocalBroadcastManager localBroadcastManager;
    private LocalMsgReceiver mLocalMsgReceiver;

    /**
     * 消息广播
     * 下载视频文件本地广播接收器
     */
    private class LocalMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IMGroupActivity.UPDATE_GROUP_INFO.equals(action)) {
                GroupInfoBean info = intent.getParcelableExtra("GroupInfo");
                int groupId = info.getGroup_id();
                if (groupId != mGroupId) {
                    return;
                }

                String topic = info.getTopic();
                String groupName = info.getGroup_name();

                if (!TextUtils.isEmpty(topic)) {
                    mGroupInfo.setTopic(topic);
                }

                if (!TextUtils.isEmpty(groupName)) {
                    mGroupInfo.setGroup_name(groupName);
                }

                setGroupInfo(mGroupInfo);
            }

        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_im_chat_group_details);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalMsgReceiver = new LocalMsgReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(IMGroupActivity.UPDATE_GROUP_INFO);
        localBroadcastManager.registerReceiver(mLocalMsgReceiver, filter);

        mGroupInfo = getIntent().getParcelableExtra(IMGroupActivity.GROUP_INFO);
        mGroupId = getIntent().getIntExtra(IMGroupActivity.GROUP_ID, -1);
        groupName = getIntent().getStringExtra(IMGroupActivity.GROUP_NAME);
        groupType = getIntent().getIntExtra(IMGroupActivity.GROUP_TYPE, YouMaiBasic.GroupType.GROUP_TYPE_MULTICHAT_VALUE);

        if (null == mGroupInfo) {
            mGroupInfo = GroupInfoHelper.instance().toQueryByGroupId(this, mGroupId);
        }

        initView();
        setOnClickListener();

        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }


    private void initView() {
        linear_next = findViewById(R.id.linear_next);
        tv_count = findViewById(R.id.tv_count);
        tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatGroupAllMembersActivity.class);
                intent.putExtra(IMGroupActivity.GROUP_ID, mGroupId);
                intent.putExtra(IMGroupActivity.GROUP_INFO, mGroupInfo);
                intent.putExtra(IMGroupActivity.GROUP_NAME, groupName);

                startActivity(intent);
            }
        });

        mTvBack = findViewById(R.id.tv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mGridView = findViewById(R.id.rv_grid_list);
        mRlGroupName = findViewById(R.id.rl_group_name);
        mTvGroupName = findViewById(R.id.tv_group_name);
        mRlGroupNotice = findViewById(R.id.rl_group_notice);
        mRlGroupManage = findViewById(R.id.rl_group_manage);
        mRlClearChatRecords = findViewById(R.id.rl_clear_chat_records);
        mTvExitGroup = findViewById(R.id.tv_exit_group);
        mtvNoticeContent = findViewById(R.id.tv_notice_content);
        switch_notify = findViewById(R.id.switch_notify);
        boolean isClosed = HuxinSdkManager.instance().getNotDisturb(mGroupId);
        if (isClosed) {
            switch_notify.setChecked(true);
        } else {
            switch_notify.setChecked(false);
        }

        switch_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HuxinSdkManager.instance().setNotDisturb(mGroupId);
                } else {
                    HuxinSdkManager.instance().removeNotDisturb(mGroupId);
                }
            }
        });


        switch_top = findViewById(R.id.switch_top);
        boolean isTop = HuxinSdkManager.instance().getMsgTop(mGroupId);
        if (isTop) {
            switch_top.setChecked(true);
        } else {
            switch_top.setChecked(false);
        }

        switch_top.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HuxinSdkManager.instance().setMsgTop(mGroupId);
                } else {
                    HuxinSdkManager.instance().removeMsgTop(mGroupId);
                }
            }
        });

        mAdapter = new GroupDetailAdapter(mContext, ChatGroupDetailsActivity.this);
        GridLayoutManager manager = new GridLayoutManager(mContext, 5);
        mGridView.addItemDecoration(new PaddingItemDecoration(5));
        mGridView.setLayoutManager(manager);
        mGridView.setAdapter(mAdapter);


        reqGroupMembers();

        setGroupInfo(mGroupInfo);
    }

    private void setGroupInfo(GroupInfoBean info) {
        if (info != null) {
            int count = info.getGroup_member_count();
            String title = String.format(getString(R.string.group_default_title),
                    "群聊资料", count);
            mTvTitle.setText(title);

            String format = getResources().getString(R.string.group_number);
            tv_count.setText(String.format(format, count));

            String group_name = info.getGroup_name();
            if (TextUtils.isEmpty(group_name)
                    || group_name.contains(ColorsConfig.GROUP_DEFAULT_NAME)) {
                mTvGroupName.setText("未命名");
            } else {
                mTvGroupName.setText(group_name);
            }

            String group_topic = info.getTopic();
            if (TextUtils.isEmpty(group_topic)) {
                mtvNoticeContent.setText("未设置");
            } else {
                mtvNoticeContent.setText(group_topic);
            }
        } else {
            mTvTitle.setText("群聊资料");
        }
    }

    private void reqGroupMembers() {
        HuxinSdkManager.instance().reqGroupMember(mGroupId, new ProtoCallback.ContactListener() {
            @Override
            public void result(List<ContactBean> list) {
                for (ContactBean item : list) {
                    if (item.getUuid().equals(HuxinSdkManager.instance().getUuid())
                            && item.getMemberRole() == 0) {
                        isGroupOwner = true;
                        mRlGroupManage.setVisibility(View.VISIBLE);
                    }

                    if (item.getMemberRole() == 0) {
                        GroupMembers.instance().addGroupListItem(0, item); //群组放第一个
                        GroupMembers.instance().setGroupOwner(item);
                    } else {
                        GroupMembers.instance().addGroupListItem(item);
                    }

                }

                ArrayList<ContactBean> groupList = GroupMembers.instance().getGroupList();

                if (groupList.size() > 8) {
                    mAdapter.addList(groupList.subList(0, 7), isGroupOwner);
                    linear_next.setVisibility(View.VISIBLE);

                } else {
                    mAdapter.addList(groupList, isGroupOwner);
                    //linear_next.setVisibility(View.GONE);
                    linear_next.setVisibility(View.VISIBLE);
                }

                String format = getResources().getString(R.string.group_number);
                tv_count.setText(String.format(format, groupList.size()));

                String title = String.format(getString(R.string.group_default_title),
                        "群聊资料", groupList.size());
                mTvTitle.setText(title);

            }
        });

    }

    private void exitGroupDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("确定退出此群吗?");

        builder.setPositiveButton(context.getString(R.string.hx_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        transAndExitGroup();
                        arg0.dismiss();
                    }
                });

        builder.setNegativeButton(context.getString(R.string.hx_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        builder.show();
    }


    private void setOnClickListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTvExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitGroupDialog(mContext);
            }
        });

        mRlGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(GroupNameActivity.GROUP_NAME, mTvGroupName.getText().equals("未命名") ? "" : mTvGroupName.getText());
                intent.putExtra(GroupNameActivity.GROUP_ID, mGroupId);
                intent.setClass(mContext, GroupNameActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MODIFY_NAME);
            }
        });

        mRlGroupManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(GroupManageActivity.GROUP_ID, mGroupId);
                intent.setClass(mContext, GroupManageActivity.class);
                startActivityForResult(intent, REQUEST_CODE_TRANS_OWNER);
            }
        });

        mRlGroupNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNotNotice = mtvNoticeContent.getText().equals("未设置");
                if (isGroupOwner) {
                    Intent intent = new Intent();
                    intent.putExtra(GroupNoticeActivity.GROUP_NOTICE, isNotNotice ? "" : mtvNoticeContent.getText());
                    intent.putExtra(GroupNoticeActivity.GROUP_ID, mGroupId);
                    intent.putExtra(GroupNoticeActivity.IS_GROUP_OWNER, true);
                    intent.setClass(mContext, GroupNoticeActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MODIFY_NOTICE_TOPIC);
                } else {
                    if (isNotNotice) {
                        Toast.makeText(mContext, "只有群主才能修改群公告", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(GroupNoticeActivity.GROUP_NOTICE, isNotNotice ? "" : mtvNoticeContent.getText());
                        intent.putExtra(GroupNoticeActivity.GROUP_ID, mGroupId);
                        intent.putExtra(GroupNoticeActivity.IS_GROUP_OWNER, false);
                        intent.setClass(mContext, GroupNoticeActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_MODIFY_NOTICE_TOPIC);
                    }
                }
            }
        });

        mRlClearChatRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setMessage("确定要删除当前所有的聊天记录吗")
                        .setPositiveButton("清除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CacheMsgHelper.instance().deleteAllMsgAndSaveEntry(mContext, mGroupId + "");
                                        IMMsgManager.instance().removeBadge(mGroupId + "");
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

    private void transAndExitGroup() {
        ArrayList<ContactBean> groupList = GroupMembers.instance().getGroupList();
        if (isGroupOwner) {
            if (groupList.size() <= 1) {
                delGroup();
            } else {
                transOwner();
            }
        } else {
            exitGroup(2);
        }
    }

    private void transOwner() {
        Intent intent = new Intent();
        intent.putExtra(GroupManageActivity.GROUP_ID, mGroupId);
        intent.putExtra(GroupManageActivity.IS_DIRECT, true);
        intent.setClass(mContext, GroupManageActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TRANS_OWNER_EXIT_GROUP);

    }

    private void exitGroup(int role) {
        List<YouMaiGroup.GroupMemberItem> list = new ArrayList<>();
        //删除成员
        YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
        builder.setMemberId(HuxinSdkManager.instance().getUuid());
        builder.setMemberName(HuxinSdkManager.instance().getRealName());
        builder.setUserName(HuxinSdkManager.instance().getUserName());
        builder.setMemberRole(role);
        list.add(builder.build());

        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberChangeRsp ack = YouMaiGroup.GroupMemberChangeRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        Toast.makeText(mContext, "退出成功", Toast.LENGTH_SHORT).show();
                        GroupInfoHelper.instance().delGroupInfo(mContext, mGroupId);

                        Intent intent = new Intent(GroupListFragment.GROUP_EXIT);
                        intent.putExtra(GroupListFragment.GROUP_ID, mGroupId);
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
                        localBroadcastManager.sendBroadcast(intent);

                        CacheMsgHelper.instance().deleteAllMsg(mContext, mGroupId + ""); //退出群不删除聊天
                        finish();
                        HuxinSdkManager.instance().getStackAct().finishActivity(IMGroupActivity.class);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        HuxinSdkManager.instance().changeGroupMember(
                YouMaiGroup.GroupMemberOptType.GROUP_MEMBER_OPT_DEL,
                list, mGroupId, groupType, listener);
    }


    private void delGroup() {
        HuxinSdkManager.instance().delGroup(mGroupId, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupDissolveRsp ack = YouMaiGroup.GroupDissolveRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        Toast.makeText(mContext, "此群组已经被解散", Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent(GroupListFragment.GROUP_EXIT);
                    intent.putExtra(GroupListFragment.GROUP_ID, mGroupId);
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
                    localBroadcastManager.sendBroadcast(intent);

                    CacheMsgHelper.instance().delCacheMsgGroupId(mContext, mGroupId);
                    GroupInfoHelper.instance().delGroupInfo(mContext, mGroupId);
                    finish();
                    HuxinSdkManager.instance().getStackAct().finishActivity(IMGroupActivity.class);

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onItemClick(int pos, ContactBean contact) {
        int type = contact.getUiType();
        String uuid = contact.getUuid();
        if (type == GroupDetailAdapter.TYPE.ADD_MEMBER.ordinal()) {
            Intent intent = new Intent(this, AddContactsCreateGroupActivity.class);
            intent.putExtra(AddContactsCreateGroupActivity.DETAIL_TYPE, 2);
            intent.putExtra(AddContactsCreateGroupActivity.GROUP_ID, mGroupId);
            intent.putExtra(IMGroupActivity.GROUP_TYPE, groupType);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        } else if (type == GroupDetailAdapter.TYPE.DEL_MEMBER.ordinal()) {
            Intent intent = new Intent(this, DeleteContactListActivity.class);
            intent.putExtra(DeleteContactListActivity.DELETE_GROUP_ID, mGroupId);
            intent.putExtra(IMGroupActivity.GROUP_TYPE, groupType);
            startActivityForResult(intent, REQUEST_CODE_DELETE);
        } else if (uuid.equals(HuxinSdkManager.instance().getUuid())) {  //自己的头像
            ARouter.getInstance().build(APath.USER_INFO_ACT)
                    .withString("useruuid", uuid)
                    .navigation(this);
        } else {
            boolean isBuddy = ContactHelper.instance().queryBuddyById(this, uuid);
            if (isBuddy) {
                ARouter.getInstance().build(APath.BUDDY_FRIEND)
                        .withString("useruuid", uuid)
                        .navigation(this);
            } else {
                ARouter.getInstance().build(APath.BUDDY_NOT_FRIEND)
                        .withString("useruuid", uuid)
                        .navigation(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE) {
            if (requestCode == REQUEST_CODE_ADD) {
                ArrayList<ContactBean> list = data.getParcelableArrayListExtra(UPDATE_GROUP_LIST);
                GroupMembers.instance().addAll(list);
                mAdapter.addList(list);
                //更新人数
                updateGroupMember(MODIFIY_GROUPADD, list.size());
            } else if (requestCode == REQUEST_CODE_DELETE) {
                ArrayList<ContactBean> list = data.getParcelableArrayListExtra(UPDATE_GROUP_LIST);
                GroupMembers.instance().removeAll(list);
                mAdapter.removeList(list);
                //更新人数
                updateGroupMember(MODIFIY_GROUPDEL, list.size());
            } else if (requestCode == REQUEST_CODE_MODIFY_NAME) {
                String groupName = data.getStringExtra(GroupNameActivity.GROUP_NAME);
                mTvGroupName.setText(groupName);
                updateGroupInfo(MODIFIY_GROUPNAME, groupName);
            } else if (requestCode == REQUEST_CODE_MODIFY_NOTICE_TOPIC) {
                String groupNotice = data.getStringExtra(GroupNoticeActivity.GROUP_NOTICE);
                mtvNoticeContent.setText(groupNotice);
                updateGroupInfo(MODIFIY_GROUPTOPIC, groupNotice);
            } else if (requestCode == REQUEST_CODE_TRANS_OWNER) {
                isGroupOwner = false;
                mRlGroupManage.setVisibility(View.GONE);
                mAdapter.setIsGroupOwner(isGroupOwner);
            } else if (requestCode == REQUEST_CODE_TRANS_OWNER_EXIT_GROUP) {
                exitGroup(2);
            }
        }
    }

    private void updateGroupMember(int type, int i) {
        GroupInfoBean info = GroupInfoHelper.instance().toQueryByGroupId(mContext, mGroupId);
        if (info == null) {
            info = new GroupInfoBean();
        }
        if (type == MODIFIY_GROUPADD) {
            info.setGroup_member_count(info.getGroup_member_count() + i);
        } else {
            info.setGroup_member_count(info.getGroup_member_count() - i);
        }
        updateTitle(info);
        GroupInfoHelper.instance().insertOrUpdate(mContext, info);
    }

    private void updateTitle(GroupInfoBean info) {
        if (info != null) {
            String title = String.format(getString(R.string.group_default_title),
                    "群聊资料", info.getGroup_member_count());
            mTvTitle.setText(title);

            int size = GroupMembers.instance().getGroupList().size();
            String format = getResources().getString(R.string.group_number);
            tv_count.setText(String.format(format, size));
        } else {
            mTvTitle.setText("群聊资料");
        }
    }

    private void updateGroupInfo(int type, String content) {
        switch (type) {
            case MODIFIY_GROUPNAME:
                mGroupInfo.setGroup_name(content);
                CacheMsgBean bean = CacheMsgHelper.instance().toQueryCacheMsgGroupId(mContext, mGroupId);
                if (bean != null) {
                    bean.setTargetName(content);
                    CacheMsgHelper.instance().updateList(mContext, bean);
                }

                GroupInfoBean info = GroupInfoHelper.instance().toQueryByGroupId(mContext, mGroupId);
                if (info == null) {
                    info = new GroupInfoBean();
                }
                info.setGroup_name(content);
                updateTitle(info);
                GroupInfoHelper.instance().insertOrUpdate(mContext, info);
                isMotifyGropInfo = true;

                break;
            case MODIFIY_GROUPTOPIC:
                mGroupInfo.setTopic(content);
                break;
        }


    }

    @Override
    public void onBackPressed() {
        if (isClearUp) {
            setResult(IMGroupActivity.RESULT_CODE_CLEAN);
        }

        if (isMotifyGropInfo) {
            Intent intent = new Intent();
            intent.putExtra("GroupInfo", mGroupInfo);
            setResult(IMGroupActivity.MOTIFY_GROUPINFO, intent);
        }
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        localBroadcastManager.unregisterReceiver(mLocalMsgReceiver);
        localBroadcastManager = null;

        isGroupOwner = false;

        GroupMembers.instance().destory();
        HuxinSdkManager.instance().getStackAct().removeActivity(this);
    }

}
