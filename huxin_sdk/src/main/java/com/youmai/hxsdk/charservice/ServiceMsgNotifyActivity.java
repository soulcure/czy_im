package com.youmai.hxsdk.charservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.im.IMMsgCallback;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.utils.Utils;

import q.rorbin.badgeview.QBadgeView;


/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-11-07 10:31
 * Description:  im 界面
 */
public class ServiceMsgNotifyActivity extends SdkBaseActivity implements IMMsgCallback {
    /*
     * Const.
     */
    public static final String TAG = ServiceMsgNotifyActivity.class.getSimpleName();

    private QBadgeView badgeOwner;
    //private QBadgeView badgeComm;

    private TextView tvOwner;
    private TextView tvComm;

    private TextView msgOwnerContent;
    private TextView msgOwnerTime;

    private TextView msgCommContent;
    private TextView msgCommTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_service_msg_notify);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        HuxinSdkManager.instance().setImMsgCallback(this);

        int ownerCount = IMMsgManager.instance().getAllBadgeOwnerCount();
        badgeOwner.setBadgeNumber(ownerCount);

        //int commCount = IMMsgManager.instance().getAllBadgeCommCount();
        //badgeComm.setBadgeNumber(commCount);

    }


    @Override
    public void onPause() {
        super.onPause();
        HuxinSdkManager.instance().removeImMsgCallback(this);
    }


    private void initView() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("消息通知");

        ImageView imgRight = (ImageView) findViewById(R.id.img_right);
        imgRight.setVisibility(View.GONE);

        TextView tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        msgOwnerContent = (TextView) findViewById(R.id.msg_owner_content);
        msgOwnerTime = (TextView) findViewById(R.id.msg_owner_time);

        CacheMsgBean lastOwner = CacheMsgHelper.instance().getLastOwnerMsg(mContext);
        if (lastOwner != null) {
            setContent(msgOwnerContent, lastOwner);
            msgOwnerTime.setText(TimeUtils.dateFormat(lastOwner.getMsgTime()));
        }

        RelativeLayout msgOwner = findViewById(R.id.msg_owner);
        msgOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, OwnerMsgListActivity.class));
            }
        });

        msgCommContent = (TextView) findViewById(R.id.msg_comm_content);
        msgCommTime = (TextView) findViewById(R.id.msg_comm_time);

        CacheMsgBean lastComm = CacheMsgHelper.instance().getLastCommMsg(mContext);
        if (lastComm != null) {
            setContent(msgCommContent, lastComm);
            msgCommTime.setText(TimeUtils.dateFormat(lastComm.getMsgTime()));
        }


        RelativeLayout msgComm = findViewById(R.id.msg_comm);
        msgComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, CommMsgListActivity.class));
            }
        });


        tvOwner = (TextView) findViewById(R.id.tv_owner);
        tvComm = (TextView) findViewById(R.id.tv_comm);

        badgeOwner = new QBadgeView(mContext);
        badgeOwner.bindTarget(tvOwner);
        badgeOwner.setBadgeGravity(Gravity.TOP | Gravity.END);
        badgeOwner.setBadgeTextSize(8f, true);
        badgeOwner.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
        badgeOwner.setGravityOffset(0, 5, true);
        badgeOwner.setBadgePadding(1, true);
        badgeOwner.setShowShadow(false);
        badgeOwner.hide(false);


        /*badgeComm = new QBadgeView(mContext);
        badgeComm.bindTarget(tvComm);
        badgeComm.setBadgeGravity(Gravity.TOP | Gravity.END);
        badgeComm.setBadgeTextSize(8f, true);
        badgeComm.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
        badgeComm.setGravityOffset(0, 5, true);
        badgeComm.setBadgePadding(1, true);
        badgeComm.setShowShadow(false);
        badgeComm.hide(false);*/


    }


    private void setContent(TextView itemView, CacheMsgBean model) {
        switch (model.getMsgType()) {
            case CacheMsgBean.SEND_EMOTION:
            case CacheMsgBean.RECEIVE_EMOTION:
                itemView.setText(mContext.getString(R.string.message_type_1));
                break;
            case CacheMsgBean.SEND_TEXT:
            case CacheMsgBean.RECEIVE_TEXT:
                CacheMsgTxt textM = (CacheMsgTxt) model.getJsonBodyObj();
                SpannableString msgSpan = new SpannableString(textM.getMsgTxt());
                msgSpan = EmoticonHandler.getInstance(mContext.getApplicationContext()).getTextFace(
                        textM.getMsgTxt(), msgSpan, 0, Utils.getFontSize(itemView.getTextSize()));
                itemView.setText(msgSpan);
                break;
            case CacheMsgBean.SEND_IMAGE:
            case CacheMsgBean.RECEIVE_IMAGE:
                itemView.setText(mContext.getString(R.string.message_type_3));
                break;
            case CacheMsgBean.SEND_LOCATION:
            case CacheMsgBean.RECEIVE_LOCATION:
                itemView.setText(mContext.getString(R.string.message_type_4));
                break;
            case CacheMsgBean.SEND_VIDEO:
            case CacheMsgBean.RECEIVE_VIDEO:
                itemView.setText(mContext.getString(R.string.message_type_5));
                break;
            case CacheMsgBean.SEND_VOICE:
            case CacheMsgBean.RECEIVE_VOICE:
                itemView.setText(mContext.getString(R.string.message_type_sounds));
                break;
            case CacheMsgBean.SEND_FILE:
            case CacheMsgBean.RECEIVE_FILE:
                itemView.setText(mContext.getString(R.string.message_type_file));
                break;
            case CacheMsgBean.SEND_REDPACKAGE:
            case CacheMsgBean.RECEIVE_REDPACKAGE:
            case CacheMsgBean.OPEN_REDPACKET:
                itemView.setText(mContext.getString(R.string.message_red_package));
                break;
            case CacheMsgBean.RECEIVE_PACKET_OPENED:
                itemView.setText(mContext.getString(R.string.message_red_package_open));
                break;
            case CacheMsgBean.PACKET_OPENED_SUCCESS:
                itemView.setText(mContext.getString(R.string.message_open_red_packet_success));
                break;
            case CacheMsgBean.BUDDY_AGREE:
                itemView.setText(mContext.getString(R.string.buddy_agree));
                break;
            case CacheMsgBean.BUDDY_BLACK:
                itemView.setText(mContext.getString(R.string.buddy_black));
                break;
            case CacheMsgBean.BUDDY_DEL:
                itemView.setText(mContext.getString(R.string.buddy_del));
                break;
            default:
                itemView.setText("");
        }
    }


    @Override
    public void onBuddyMsgCallback(CacheMsgBean cacheMsgBean) {

    }

    @Override
    public void onOwnerMsgCallback(CacheMsgBean cacheMsgBean) {
        //CacheMsgBean lastOwner = CacheMsgHelper.instance().getLastOwnerMsg(mContext);
        if (cacheMsgBean != null) {
            setContent(msgOwnerContent, cacheMsgBean);
            msgOwnerTime.setText(TimeUtils.dateFormat(cacheMsgBean.getMsgTime()));
        }
        refreshUnReadCount();
    }

    @Override
    public void onCommunityMsgCallback(CacheMsgBean cacheMsgBean) {
        //CacheMsgBean lastComm = CacheMsgHelper.instance().getLastCommMsg(mContext);
        if (cacheMsgBean != null) {
            setContent(msgCommContent, cacheMsgBean);
            msgCommTime.setText(TimeUtils.dateFormat(cacheMsgBean.getMsgTime()));
        }
        refreshUnReadCount();
    }


    private void refreshUnReadCount() {
        int ownerCount = IMMsgManager.instance().getAllBadgeOwnerCount();
        if (ownerCount > 0) {
            badgeOwner.setBadgeNumber(ownerCount);
        } else {
            badgeOwner.hide(true);
        }

        /*int commCount = IMMsgManager.instance().getAllBadgeCommCount();
        if (commCount > 0) {
            badgeComm.setBadgeNumber(commCount);
        } else {
            badgeComm.hide(true);
        }*/
    }


}
