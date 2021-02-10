package com.youmai.hxsdk.group;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.ContactHelper;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.group.adapter.GroupDetailAdapter;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.router.APath;

import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2018.04.23 17:32
 * 描述：群聊详情
 */
public class ChatGroupAllMembersActivity extends SdkBaseActivity implements GroupDetailAdapter.ItemEventListener {

    public static final String GROUP_LIST = "GROUP_LIST";

    private int mGroupId;

    private TextView mTvBack, mTvTitle;
    private RecyclerView recycler_view;

    private GroupDetailAdapter mAdapter;

    private ArrayList<ContactBean> groupList;

    private GroupInfoBean mGroupInfo;
    private String groupName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_im_chat_group_all_members);

        mGroupInfo = getIntent().getParcelableExtra(IMGroupActivity.GROUP_INFO);
        mGroupId = getIntent().getIntExtra(IMGroupActivity.GROUP_ID, -1);
        groupName = getIntent().getStringExtra(IMGroupActivity.GROUP_NAME);

        groupList = GroupMembers.instance().getGroupList();

        if (null == mGroupInfo) {
            mGroupInfo = GroupInfoHelper.instance().toQueryByGroupId(this, mGroupId);
        }

        initView();

        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HuxinSdkManager.instance().getStackAct().removeActivity(this);
    }

    private void initView() {
        mTvBack = findViewById(R.id.tv_back);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTvTitle = findViewById(R.id.tv_title);
        recycler_view = findViewById(R.id.recycler_view);


        mAdapter = new GroupDetailAdapter(mContext, groupList, this);
        GridLayoutManager manager = new GridLayoutManager(mContext, 5);
        recycler_view.addItemDecoration(new PaddingItemDecoration(5));
        recycler_view.setLayoutManager(manager);
        recycler_view.setAdapter(mAdapter);

        setGroupInfo(mGroupInfo);
    }

    private void setGroupInfo(GroupInfoBean info) {
        if (info != null) {
            if (TextUtils.isEmpty(groupName)
                    || groupName.contains(ColorsConfig.GROUP_DEFAULT_NAME)) {
                String title = String.format(getString(R.string.group_default_title),
                        "聊天详情", info.getGroup_member_count());
                mTvTitle.setText(title);
            } else {
                if (groupName.length() > 6) {
                    mTvTitle.setText(groupName.substring(0, 4) + "...(" + info.getGroup_member_count() + ")");
                } else {
                    mTvTitle.setText(groupName + "(" + info.getGroup_member_count() + ")");
                }
            }

        } else {
            mTvTitle.setText("聊天详情");
        }
    }

    @Override
    public void onItemClick(int pos, ContactBean contact) {
        String uuid = contact.getUuid();
        if (uuid.equals(HuxinSdkManager.instance().getUuid())) {  //自己的头像
            ARouter.getInstance().build(APath.USER_INFO_ACT)
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
}
