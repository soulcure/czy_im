package com.youmai.hxsdk.group.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.group.ChatGroupDetailsActivity;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.StringUtils;

/**
 * 作者：create by YW
 * 日期：2018.04.26 17:05
 * 描述: 群公告设置
 */
public class GroupNoticeActivity extends SdkBaseActivity {

    public static final String GROUP_ID = "groupId";
    public static final String GROUP_NOTICE = "groupNotice";
    public static final String IS_GROUP_OWNER = "IS_GROUP_OWNER";

    private TextView tv_back, tv_title, tv_title_right;
    private EditText et_owner_notice;
    private TextView tv_not_owner;
    private LinearLayout ll_tip;

    private String notice;
    private int groupId;
    private boolean is_owner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_notice_setting);

        initView();
        initData();
        setClickListener();
    }

    private void initView() {
        tv_back = findViewById(R.id.tv_left_cancel);
        tv_back.setText("返回");
        tv_title = findViewById(R.id.tv_title);
        tv_title_right = findViewById(R.id.tv_right_sure);
        et_owner_notice = findViewById(R.id.et_user_notice);
        tv_not_owner = findViewById(R.id.tv_default);

        ll_tip = findViewById(R.id.ll_not_owner_tip);
    }

    private void initData() {
        groupId = getIntent().getIntExtra(GROUP_ID, -1);
        notice = getIntent().getStringExtra(GROUP_NOTICE);
        is_owner = getIntent().getBooleanExtra(IS_GROUP_OWNER, false);
        tv_title.setText("群公告");
        if (is_owner) {
            ll_tip.setVisibility(View.GONE);
            tv_not_owner.setVisibility(View.GONE);
            et_owner_notice.setVisibility(View.VISIBLE);
            tv_title_right.setVisibility(View.VISIBLE);
        } else {
            et_owner_notice.setVisibility(View.GONE);
            tv_title_right.setVisibility(View.GONE);
            tv_not_owner.setVisibility(View.VISIBLE);
            ll_tip.setVisibility(View.VISIBLE);
        }
        if (!StringUtils.isEmpty(notice)) {
            tv_not_owner.setText(notice);
            et_owner_notice.setText(notice);
        }
        et_owner_notice.setSelection(et_owner_notice.getText().length());

    }

    private void setClickListener() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String groupNotice = et_owner_notice.getText().toString().trim();
                InputMethodManager manager = (InputMethodManager) GroupNoticeActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(et_owner_notice.getWindowToken(), 0);

                if (StringUtils.isEmpty(groupNotice)) {
                    return;
                }

                ReceiveListener receiveListener = new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiGroup.GroupInfoModifyRsp ack = YouMaiGroup.GroupInfoModifyRsp.parseFrom(pduBase.body);
                            if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                Toast.makeText(GroupNoticeActivity.this, "修改群公告成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra(GROUP_NOTICE, groupNotice);
                                setResult(ChatGroupDetailsActivity.RESULT_CODE, intent);
                                finish();
                            } else if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_NOT_FIND) {
                                Toast.makeText(GroupNoticeActivity.this, "你已退出此群", Toast.LENGTH_SHORT).show();
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                };

                HuxinSdkManager.instance().reqModifyGroupInfo(
                        groupId, "", "", "",
                        groupNotice, "",
                        YouMaiGroup.GroupInfoModifyType.MODIFY_TOPIC, receiveListener);
            }
        });
    }

}
