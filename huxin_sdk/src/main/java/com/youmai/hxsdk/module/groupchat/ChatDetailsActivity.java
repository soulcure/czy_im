package com.youmai.hxsdk.module.groupchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.db.helper.ContactHelper;
import com.youmai.hxsdk.group.AddContactsCreateGroupActivity;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.proto.YouMaiBuddy;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2018.04.18 16:36
 * 描述：单聊详情
 */
public class ChatDetailsActivity extends SdkBaseActivity {

    public static final String GROUP_LIST = "GROUP_LIST";
    public static final String DS_NAME = "DS_NAME";
    public static final String DS_USER_AVATAR = "DS_USER_AVATAR";
    public static final String DS_UUID = "DS_UUID";
    public static final String DS_USERNAME = "DS_USERNAME";

    private TextView mTvBack, mTvTitle;
    private ImageView mSelfHeader;
    private TextView mSelfName;
    private ImageView mAddMore;
    private RelativeLayout mClearRecords;

    private Switch switch_notify;
    private Switch switch_top;
    private Switch switch_black;

    private String uuid;
    private String avatar;
    private String realname;
    private String username;
    private boolean isClearUp;

    private ArrayList<ContactBean> groupList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_im_chat_details);

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

        mSelfName = findViewById(R.id.tv_self_name);
        mSelfHeader = findViewById(R.id.iv_self_header);
        mAddMore = findViewById(R.id.iv_add_more);
        mClearRecords = findViewById(R.id.rl_clear_chat_records);

        mTvTitle.setText("聊天详情");
        mSelfName.setText(realname);

        switch_notify = findViewById(R.id.switch_notify);
        boolean isClosed = HuxinSdkManager.instance().getNotDisturb(uuid);
        if (isClosed) {
            switch_notify.setChecked(true);
        } else {
            switch_notify.setChecked(false);
        }

        switch_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HuxinSdkManager.instance().setNotDisturb(uuid);
                } else {
                    HuxinSdkManager.instance().removeNotDisturb(uuid);
                }
            }
        });


        switch_top = findViewById(R.id.switch_top);
        boolean isTop = HuxinSdkManager.instance().getMsgTop(uuid);
        if (isTop) {
            switch_top.setChecked(true);
        } else {
            switch_top.setChecked(false);
        }

        switch_top.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HuxinSdkManager.instance().setMsgTop(uuid);
                } else {
                    HuxinSdkManager.instance().removeMsgTop(uuid);
                }
            }
        });


        switch_black = findViewById(R.id.switch_black);
        boolean isBlack = HuxinSdkManager.instance().getBuddyBlack(uuid);
        if (isBlack) {
            switch_black.setChecked(true);
        } else {
            switch_black.setChecked(false);
        }

        switch_black.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HuxinSdkManager.instance().addFriend(uuid,
                            YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_BLACKLIST,
                            "加入黑名单",
                            new ProtoCallback.AddFriendListener() {
                                @Override
                                public void result(YouMaiBuddy.IMOptBuddyRsp ack) {
                                    if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_OK) {
                                        HuxinSdkManager.instance().setBuddyBlack(uuid);
                                    }
                                }
                            });
                } else {
                    HuxinSdkManager.instance().addFriend(uuid,
                            YouMaiBuddy.BuddyOptType.BUDDY_OPT_REMOVE_BLACKLIST,
                            "移除黑名单",
                            new ProtoCallback.AddFriendListener() {
                                @Override
                                public void result(YouMaiBuddy.IMOptBuddyRsp ack) {
                                    if (ack.getResult() == YouMaiBuddy.ResultCode.CODE_OK) {
                                        HuxinSdkManager.instance().removeBuddyBlack(uuid);
                                    }
                                }
                            });
                }
            }
        });


        Glide.with(mContext)
                .load(avatar)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .override(60, 60)
                        .transform(new GlideRoundTransform())
                        .error(R.drawable.color_default_header))
                .into(mSelfHeader);

        createGroupMap();

    }

    private void createGroupMap() {
        ContactBean self = new ContactBean();
        String selfUid = HuxinSdkManager.instance().getUuid();
        self.setUuid(selfUid);
        self.setAvatar(HuxinSdkManager.instance().getHeadUrl());
        self.setNickName(HuxinSdkManager.instance().getNickName());
        self.setUserName(HuxinSdkManager.instance().getUserName());
        groupList.add(self);

        if (!selfUid.equals(uuid)) {
            ContactBean contact = new ContactBean();
            contact.setUuid(this.uuid);
            contact.setNickName(realname);
            contact.setAvatar(avatar);
            contact.setUserName(username);
            groupList.add(contact);
        }
    }

    private void setOnClickListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupMembers.instance().addAll(groupList);
                Intent intent = new Intent(mContext, AddContactsCreateGroupActivity.class);
                intent.putExtra(AddContactsCreateGroupActivity.DETAIL_TYPE, 1);
                //intent.putParcelableArrayListExtra(GROUP_LIST, groupList);
                startActivity(intent);
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

        mSelfHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isBuddy = ContactHelper.instance().queryBuddyById(mContext, uuid);
                if (isBuddy) {
                    ARouter.getInstance().build(APath.BUDDY_FRIEND)
                            .withString("useruuid", uuid)
                            .navigation(mContext);
                } else {
                    ARouter.getInstance().build(APath.BUDDY_NOT_FRIEND)
                            .withString("useruuid", uuid)
                            .navigation(mContext);
                }
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
