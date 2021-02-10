package com.youmai.hxsdk.im;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.ProtocolCallBack;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.charservice.IMOwnerActivity;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.chat.ContentRedPackage;
import com.youmai.hxsdk.chat.ContentVideo;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.chat.ContentLocation;
import com.youmai.hxsdk.chat.ContentText;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.ContactHelper;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.DownloadListener;
import com.youmai.hxsdk.http.FileAsyncTaskDownload;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgRedPackage;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBuddy;
import com.youmai.hxsdk.proto.YouMaiBulletin;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.proto.YouMaiUser;
import com.youmai.hxsdk.socket.NotifyListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.BadgeUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-11-09 10:00
 * Description:
 */
public class IMMsgManager {

    private static final String TAG = IMMsgManager.class.getSimpleName();

    private Context mContext;

    private Class<Activity> homeAct;

    private List<IMMsgCallback> imMsgCallbackList;

    private List<Integer> pushMsgNotifyIdList;

    private static IMMsgManager instance = null;

    private String mTargetId;

    private LinkedBlockingQueue<Long> msgList;

    private boolean isNotify;


    private IMMsgManager() {
        imMsgCallbackList = new ArrayList<>();
        pushMsgNotifyIdList = new ArrayList<>();
        msgList = new LinkedBlockingQueue<>();
    }

    public synchronized static IMMsgManager instance() {
        if (instance == null) {
            instance = new IMMsgManager();
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
        getAllBadgeCount();
        isNotify = AppUtils.getBooleanSharedPreferences(context, "im_notify", true);
    }

    public void setContext(Context context) {
        this.mContext = context.getApplicationContext();
    }


    public void setImMsgCallback(IMMsgCallback imMsgCallback) {
        if (!imMsgCallbackList.contains(imMsgCallback)) {
            imMsgCallbackList.add(imMsgCallback);
        }
    }

    public void removeImMsgCallback(IMMsgCallback imMsgCallback) {
        if (imMsgCallbackList.contains(imMsgCallback)) {
            imMsgCallbackList.remove(imMsgCallback);
        }
    }


    public void setHomeAct(Class homeAct) {
        this.homeAct = homeAct;
    }


    public void setNotify(boolean notify) {
        isNotify = notify;
        AppUtils.setBooleanSharedPreferences(mContext, "im_notify", notify);
    }


    public boolean isNotify() {
        return isNotify;
    }

    /*
     *  1：有socket 连接即有消息接收，IM 就必须有mNotifyListener 这个监听器，目前有且只有这个。
     *    【因为如果没有处理，则消息不会再次来到，除非本来没有socket连接，如应用没有启动的情况】
     *  2：mNotifyListener 调用 parseCharMsg 处理消息，回调imMsgCallbackList（目前存在于沟通列表，聊天界面两个回调）
     *  3：mNotifyListener 调用 notifyMsg ，如果有 mOnChatMsgList回调（目前存在于各个通话屏中，与继承SdkBaseActivity 的界面) ，就不通知。
     *  4：回调原则：自己注册，自己负责解注册，监听器处理消息，回调拿去用。
     * */
    private NotifyListener mNotifyListener = new NotifyListener(
            YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiMsg.ChatMsg notify = YouMaiMsg.ChatMsg.parseFrom(data);
                YouMaiMsg.MsgData imChat = notify.getData();
                long msgId = imChat.getMsgId();
                if (msgList.contains(msgId)) {
                    Log.e(TAG, "接收到重复消息");
                    return;
                } else {
                    msgList.offer(msgId);
                    if (msgList.size() > 50) {
                        msgList.poll();
                    }
                }

                HuxinSdkManager.instance().sendMsgReply(imChat.getMsgId());

                IMChat im = new IMChat(imChat);
                int groupType = im.getMsgBean().getGroupType();
                notifyMsg(im, false, false, groupType);
                parseCharMsg(im);

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };

    private NotifyListener mGroupListener = new NotifyListener(
            YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiMsg.ChatMsg notify = YouMaiMsg.ChatMsg.parseFrom(data);
                YouMaiMsg.MsgData imChat = notify.getData();
                long msgId = imChat.getMsgId();
                if (msgList.contains(msgId)) {
                    Log.e(TAG, "接收到重复消息");
                    return;
                } else {
                    msgList.offer(msgId);
                    if (msgList.size() > 50) {
                        msgList.poll();
                    }
                }

                HuxinSdkManager.instance().sendMsgReply(imChat.getMsgId());

                IMChat im = new IMChat(imChat);

                int groupType = im.getMsgBean().getGroupType();
                notifyMsg(im, false, true, groupType);
                parseCharMsg(im);

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };


    /*
     *  1：有socket 连接即有消息接收，IM 就必须有mNotifyListener 这个监听器，目前有且只有这个。
     *    【因为如果没有处理，则消息不会再次来到，除非本来没有socket连接，如应用没有启动的情况】
     *  2：mNotifyListener 调用 parseCharMsg 处理消息，回调imMsgCallbackList（目前存在于沟通列表，聊天界面两个回调）
     *  3：mNotifyListener 调用 notifyMsg ，如果有 mOnChatMsgList回调（目前存在于各个通话屏中，与继承SdkBaseActivity 的界面) ，就不通知。
     *  4：回调原则：自己注册，自己负责解注册，监听器处理消息，回调拿去用。
     * */
    private NotifyListener mServiceListener = new NotifyListener(
            YouMaiBasic.COMMANDID.CID_CHAT_CUSTOMER_SERVICES_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiMsg.ChatMsg notify = YouMaiMsg.ChatMsg.parseFrom(data);
                YouMaiMsg.MsgData imChat = notify.getData();
                long msgId = imChat.getMsgId();
                if (msgList.contains(msgId)) {
                    Log.e(TAG, "接收到重复消息");
                    return;
                } else {
                    msgList.offer(msgId);
                    if (msgList.size() > 50) {
                        msgList.poll();
                    }
                }

                HuxinSdkManager.instance().sendMsgReply(imChat.getMsgId());

                IMChat im = new IMChat(imChat);
                int groupType = im.getMsgBean().getGroupType();
                notifyMsg(im, false, false, groupType);
                parseCharMsg(im);

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 群组操作
     */
    private NotifyListener mGroupOptionNotify = new NotifyListener(
            YouMaiBasic.COMMANDID.CID_GROUP_OPT_NOTIFY_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiGroup.GroupOptNotify notify = YouMaiGroup.GroupOptNotify.parseFrom(data);
                int groupId = notify.getGroupId();
                if (notify.getOptType() == 0) {  //解散群
                    CacheMsgHelper.instance().delCacheMsgGroupId(mContext, groupId);
                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 添加删除群组成员
     */
    private NotifyListener mGroupChangeNotify = new NotifyListener(
            YouMaiBasic.COMMANDID.CID_GROUP_CHANGE_MEMBER_NOTIFY_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiGroup.GroupMemberChangeNotify notify = YouMaiGroup.GroupMemberChangeNotify.parseFrom(data);
                int groupId = notify.getGroupId();
                int groupType = notify.getGroupType().getNumber();
                List<YouMaiGroup.GroupMemberItem> list = notify.getMemberListList();
                YouMaiGroup.GroupMemberOptType type = notify.getType();

                ArrayList<String> changeList = new ArrayList<>();
                StringBuilder sb = new StringBuilder();
                if (type == YouMaiGroup.GroupMemberOptType.GROUP_MEMBER_OPT_DEL) {
                    for (int i = 0; i < list.size(); i++) {
                        YouMaiGroup.GroupMemberItem item = list.get(i);
                        String uuid = item.getMemberId();
                        changeList.add(uuid);

                        //自己被移除 删除消息入口
                        if (uuid.equals(HuxinSdkManager.instance().getUuid())) {
                            CacheMsgHelper.instance().delCacheMsgGroupId(mContext, groupId);
                        }
                        sb.append('"').append(item.getMemberName()).append('"');
                        if (i < list.size() - 1) {
                            sb.append('、');
                        }
                    }
                    sb.append("移出了群");
                    CacheMsgBean bean = getMemberChangedMsgBean(groupId, groupType, sb.toString());

                    List<CacheMsgBean> msgList = CacheMsgHelper.instance().toQueryCacheMsgList(mContext, groupId + "");
                    if (!ListUtils.isEmpty(msgList)) {
                        for (CacheMsgBean item : msgList) {
                            if (!TextUtils.isEmpty(item.getTargetName()) && item.getGroupType() != 0) {
                                bean.setTargetName(item.getTargetName());
                                bean.setGroupType(item.getGroupType());
                                CacheMsgHelper.instance().insertOrUpdate(mContext, bean);
                                break;
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(bean.getTargetName())) {
                        handlerIMMsgCallback(bean);
                        Intent intent = new Intent(IMGroupActivity.UPDATE_GROUP_REMOVE);
                        intent.putExtra("groupId", groupId);
                        intent.putExtra("changeList", changeList);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }

                } else if (type == YouMaiGroup.GroupMemberOptType.GROUP_MEMBER_OPT_ADD) {
                    for (int i = 0; i < list.size(); i++) {
                        YouMaiGroup.GroupMemberItem item = list.get(i);
                        String uuid = item.getMemberId();
                        changeList.add(uuid);
                        sb.append('"').append(item.getMemberName()).append('"');
                        if (i < list.size() - 1) {
                            sb.append('、');
                        }
                    }
                    sb.append("加入了群");
                    CacheMsgBean bean = getMemberChangedMsgBean(groupId, groupType, sb.toString());

                    List<CacheMsgBean> msgList = CacheMsgHelper.instance().toQueryCacheMsgList(mContext, groupId + "");
                    if (!ListUtils.isEmpty(msgList)) {
                        for (CacheMsgBean item : msgList) {
                            if (!TextUtils.isEmpty(item.getTargetName()) && item.getGroupType() != 0) {
                                bean.setTargetName(item.getTargetName());
                                bean.setGroupType(item.getGroupType());
                                CacheMsgHelper.instance().insertOrUpdate(mContext, bean);
                                break;
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(bean.getTargetName())) {
                        handlerIMMsgCallback(bean);

                        Intent intent = new Intent(IMGroupActivity.UPDATE_GROUP_ADD);
                        intent.putExtra("groupId", groupId);
                        intent.putExtra("changeList", changeList);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }

                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 群资料修改通知
     */
    private NotifyListener mGroupInfoModifyNotify = new NotifyListener(
            YouMaiBasic.COMMANDID.CID_GROUP_INFO_MODIFY__NOTIFY_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiGroup.IMGroupInfoModifyNotify notify = YouMaiGroup.IMGroupInfoModifyNotify.parseFrom(data);
                int groupId = notify.getGroupId();
                //String uuid = notify.getUserId();
                YouMaiGroup.GroupInfoModifyType type = notify.getType();
                String memberName = notify.getSrcOwnerName();

                if (type == YouMaiGroup.GroupInfoModifyType.MODIFY_NAME) {
                    YouMaiGroup.GroupInfo groupInfo = notify.getGroupInfo();
                    List<CacheMsgBean> list = CacheMsgHelper.instance().toQueryCacheAllMsgGroupId(mContext, groupId);
                    String groupName = groupInfo.getGroupName();
                    int groupType = groupInfo.getGroupType().getNumber();

                    if (!ListUtils.isEmpty(list)) {
                        for (CacheMsgBean item : list) {
                            item.setTargetName(groupName);
                        }
                        CacheMsgHelper.instance().updateList(mContext, list);
                    }

                    GroupInfoBean info = GroupInfoHelper.instance().toQueryByGroupId(mContext, groupId);
                    if (info == null) {
                        info = new GroupInfoBean();
                        info.setGroup_id(groupId);
                    }

                    info.setGroup_name(groupName);
                    GroupInfoHelper.instance().insertOrUpdate(mContext, info);

                    StringBuilder sb = new StringBuilder();
                    sb.append(memberName).append(" 修改群名为");
                    sb.append('"').append(groupName).append('"');

                    CacheMsgBean msgBean = getGroupNameChangedMsgBean(groupId, groupType, sb.toString());

                    msgBean.setTargetName(groupName);
                    CacheMsgHelper.instance().insertOrUpdate(mContext, msgBean);

                    handlerIMMsgCallback(msgBean);


                    Intent intent = new Intent(IMGroupActivity.UPDATE_GROUP_INFO);
                    intent.putExtra("GroupInfo", info);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                } else if (type == YouMaiGroup.GroupInfoModifyType.MODIFY_TOPIC) {
                    YouMaiGroup.GroupInfo groupInfo = notify.getGroupInfo();
                    String topic = groupInfo.getTopic();

                    GroupInfoBean info = GroupInfoHelper.instance().toQueryByGroupId(mContext, groupId);
                    if (info == null) {
                        info = new GroupInfoBean();
                    }

                    info.setTopic(topic);
                    GroupInfoHelper.instance().insertOrUpdate(mContext, info);

                    Intent intent = new Intent(IMGroupActivity.UPDATE_GROUP_INFO);
                    intent.putExtra("GroupInfo", info);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                } else if (type == YouMaiGroup.GroupInfoModifyType.MODIFY_OWNER) {

                    String srcName = notify.getSrcOwnerName();
                    String dstName = notify.getDstOwnerName();
                    String groupName = notify.getGroupInfo().getGroupName();
                    int groupType = notify.getGroupInfo().getGroupType().getNumber();

                    StringBuilder sb = new StringBuilder();
                    sb.append(srcName).append(" 将群主转让给 ").append(dstName);

                    CacheMsgBean msgBean = getGroupOwnerChangedMsgBean(groupId, groupType, sb.toString());

                    msgBean.setTargetName(groupName);
                    CacheMsgHelper.instance().insertOrUpdate(mContext, msgBean);

                    handlerIMMsgCallback(msgBean);
                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };

    ProtoCallback.BuddyNotify pushMsgNotify;

    public void setPushMsgNotify(ProtoCallback.BuddyNotify notify) {
        this.pushMsgNotify = notify;
    }

    /**
     * 群组操作
     */
    private NotifyListener mCommonPushMsg = new NotifyListener(
            YouMaiBasic.COMMANDID.CID_PUSH_MSG_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiMsg.PushMsg notify = YouMaiMsg.PushMsg.parseFrom(data);

                long msgId = notify.getMsgId();
                int cmdId = notify.getCmdId();
                ByteString notifyData = notify.getData();

                if (cmdId == YouMaiBasic.COMMANDID.CID_BUDDY_LIST_OPT_NOTIFY_VALUE) {
                    YouMaiBuddy.IMOptBuddyNotify optBuddyNotify = YouMaiBuddy.IMOptBuddyNotify.parseFrom(notifyData);
                    if (pushMsgNotify != null) {
                        pushMsgNotify.result(optBuddyNotify);
                    }

                    if (optBuddyNotify.getOptType() == YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_AGREE) { //对方同意添加好友
                        String srcUuid = optBuddyNotify.getSrcUserId(); //消息发送方

                        String dstUuid = optBuddyNotify.getDestUserId(); //消息接受方
                        String userName = optBuddyNotify.getUsername(); //消息发送方昵称
                        String nickName = optBuddyNotify.getNickname(); //消息发送方昵称
                        String srcAvatar = optBuddyNotify.getAvatar(); //消息发送方昵称
                        String realName = optBuddyNotify.getRealName(); //消息发送方真名

                        String displayName = "";
                        if (!TextUtils.isEmpty(realName)) {
                            displayName = realName;
                        } else if (!TextUtils.isEmpty(userName)) {
                            displayName = userName;
                        } else if (!TextUtils.isEmpty(nickName)) {
                            displayName = nickName;
                        }

                        //新朋友状态修改为已添加
                        String content = displayName + "通过了你的好友请求，可以开始聊天了";
                        CacheMsgBean cacheMsgBean = new CacheMsgBean()
                                .setMsgTime(System.currentTimeMillis())
                                .setMsgType(CacheMsgBean.BUDDY_AGREE)
                                .setMemberChanged(content)
                                .setSenderUserId(srcUuid)
                                .setTargetName(displayName)
                                .setTargetAvatar(srcAvatar)
                                .setTargetUuid(srcUuid);

                        CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);
                        handlerIMMsgCallback(cacheMsgBean);

                        ContactHelper.instance().updateStatusById(mContext, srcUuid, 1);
                    }

                }
                HuxinSdkManager.instance().sendPushMsgReply(msgId);


            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 公告默认监听器
     */
    private final NotifyListener OnRecvBulletin = new NotifyListener(
            YouMaiBasic.COMMANDID.BULLETIN_NOTIFY_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiBulletin.Bulletin_Notify notify = YouMaiBulletin.Bulletin_Notify.parseFrom(data);
                List<YouMaiBulletin.Bulletin> bulletin = notify.getBulletinsList();
                int size = bulletin.size();
                if (size > 0) {
                    YouMaiBulletin.Bulletin item = bulletin.get(0);

                    int msgId = item.getBulletinId();
                    HuxinSdkManager.instance().sendMsgReply(msgId);

                    String content = item.getContent();
                    parseBulletin(content);
                    //Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
                    LogFile.inStance().toFile(content);
                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };

    private final NotifyListener onDeviceKickedNotify = new NotifyListener(
            YouMaiBasic.COMMANDID.MULTI_DEVICE_KICKED_NOTIFY_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiUser.Multi_Device_Kicked_Notify notify = YouMaiUser
                        .Multi_Device_Kicked_Notify.parseFrom(data);

                String newImei = notify.getNewDeviceId();
                if (!TextUtils.isEmpty(newImei)) {
                    ProtocolCallBack sCallBack = RespBaseBean.getsCallBack();
                    if (sCallBack != null) {
                        sCallBack.sessionExpire();
                    }
                }

                HuxinSdkManager.LoginStatusListener listener = HuxinSdkManager.instance().getLoginStatusListener();
                if (listener != null) {
                    listener.onKickOut();
                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };


    public void parseBulletin(String content) {
        /*try {
            JSONObject object = new JSONObject(content);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }*/
    }


    /**
     * 添加消息监听
     */
    public void addChatListener() {
        HuxinSdkManager.instance().setNotifyListener(mNotifyListener);
        HuxinSdkManager.instance().setNotifyListener(mGroupListener);
        HuxinSdkManager.instance().setNotifyListener(mServiceListener);
        HuxinSdkManager.instance().setNotifyListener(mGroupOptionNotify);
        HuxinSdkManager.instance().setNotifyListener(mGroupChangeNotify);
        HuxinSdkManager.instance().setNotifyListener(mGroupInfoModifyNotify);
        HuxinSdkManager.instance().setNotifyListener(mCommonPushMsg);
        HuxinSdkManager.instance().setNotifyListener(OnRecvBulletin);
        HuxinSdkManager.instance().setNotifyListener(onDeviceKickedNotify);
    }

    /**
     * 移除消息监听
     */
    public void removeChatListener() {
        HuxinSdkManager.instance().clearNotifyListener(mNotifyListener);
        HuxinSdkManager.instance().clearNotifyListener(mGroupListener);
        HuxinSdkManager.instance().clearNotifyListener(mServiceListener);
        HuxinSdkManager.instance().clearNotifyListener(mGroupOptionNotify);
        HuxinSdkManager.instance().clearNotifyListener(mGroupChangeNotify);
        HuxinSdkManager.instance().clearNotifyListener(mGroupInfoModifyNotify);
        HuxinSdkManager.instance().clearNotifyListener(mCommonPushMsg);
        HuxinSdkManager.instance().clearNotifyListener(OnRecvBulletin);
        HuxinSdkManager.instance().clearNotifyListener(onDeviceKickedNotify);
    }


    private void handlerIMMsgCallback(CacheMsgBean cacheMsgBean) {
        if (imMsgCallbackList != null) {
            for (IMMsgCallback cb : imMsgCallbackList) {

                if (cacheMsgBean.getGroupType() == 101) {
                    cb.onOwnerMsgCallback(cacheMsgBean);
                } else if (cacheMsgBean.getGroupType() == YouMaiBasic.GroupType.GROUP_TYPE_COMMUNITY_VALUE) {
                    cb.onCommunityMsgCallback(cacheMsgBean);
                } else {
                    cb.onBuddyMsgCallback(cacheMsgBean);
                }

            }
        }
    }

    public void notifyMsg(IMChat msg, boolean isFormPush, boolean isGroup, int groupType) {
        String targetId;
        if (isGroup) {
            targetId = msg.getImChat().getGroupId() + "";
        } else {
            targetId = msg.getImChat().getSrcUserId();
        }

        String desName = msg.getImChat().getSrcRealname();
        boolean contains = desName.contains(ColorsConfig.GROUP_DEFAULT_NAME);
        if (contains) {
            desName = desName.replace(ColorsConfig.GROUP_DEFAULT_NAME, "");
        }

        String newMsgTip = mContext.getString(R.string.hx_hook_strategy_msg);

        if (msg.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE) {  //文字
            ContentText text = msg.getContent().getText();
            String content = text.getContent();
            if (content == null) {
                return;
            }
            if (content.startsWith("/")) { //自定义表情
                notifyMsg(mContext, targetId, desName, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_emoji), isFormPush, isGroup, groupType);
            } else {  //文字
                notifyMsg(mContext, targetId, desName, content, isFormPush, isGroup, groupType);
            }
        } else if (msg.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE_VALUE) { //图片
            notifyMsg(mContext, targetId, desName, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_pic), isFormPush, isGroup, groupType);
        } else if (msg.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO_VALUE) { //视频
            notifyMsg(mContext, targetId, desName, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_tv), isFormPush, isGroup, groupType);
        } else if (msg.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_VALUE) { //定位
            notifyMsg(mContext, targetId, desName, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_map), isFormPush, isGroup, groupType);
        } else if (msg.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO_VALUE) { //音频
            notifyMsg(mContext, targetId, desName, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_voice), isFormPush, isGroup, groupType);
        } else if (msg.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE_VALUE) { //文件
            notifyMsg(mContext, targetId, desName, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_file), isFormPush, isGroup, groupType);
        } else if (msg.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_SEND_RED_ENVELOPE_VALUE) { //红包
            notifyMsg(mContext, targetId, desName, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_packet), isFormPush, isGroup, groupType);
        }
    }


    private void parseCharMsg(IMChat im) {
        //todo_k:
        CacheMsgBean cacheMsgBean = im.getMsgBean();

        if (im.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE) {

            ContentText text = im.getContent().getText();
            String content = text.getContent();

            //todo_k: 12-6 文字 表情
            if (content.startsWith("/")) { //自定义表情
                cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_EMOTION)
                        .setJsonBodyObj(new CacheMsgEmotion().setEmotion(content, -1));
                //msg.setMsgType(ChatMsg.MsgType.EMO_TEXT);
            } else {  //文字
                cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_TEXT)
                        .setJsonBodyObj(new CacheMsgTxt().setMsgTxt(content));
            }

            //add to db
            CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);

            handlerIMMsgCallback(cacheMsgBean);

        } else if (im.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE_VALUE) { //图片
            String fid = im.getContent().getPicture().getPicUrl();
            String describe = im.getContent().getPicture().getDescribe();
            if (describe == null) {  //防止版本差异奔溃
                describe = "";
            }

            //todo_k 图片
            int type = describe.equals("original") ? CacheMsgImage.SEND_IS_ORI_RECV_NOT_ORI : CacheMsgImage.SEND_NOT_ORI_RECV_NOT_ORI;
            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_IMAGE)
                    .setJsonBodyObj(new CacheMsgImage().setFid(fid).setOriginalType(type));

            //add to db
            CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);

            handlerIMMsgCallback(cacheMsgBean);
        } else if (im.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_VALUE) { //定位

            ContentLocation location = im.getContent().getLocation();
            final String mLabelAddress = location.getLabelStr();
            final String scale = location.getScaleStr();

            String url = "http://restapi.amap.com/v3/staticmap?location="
                    + location.getLongitudeStr() + "," + location.getLatitudeStr() + "&zoom=" + scale
                    + "&size=720*550&traffic=1&markers=mid,0xff0000,A:" + location.getLongitudeStr()
                    + "," + location.getLatitudeStr() + "&key=" + AppConfig.staticMapKey;

            //todo_k: 地图
            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_LOCATION)
                    .setJsonBodyObj(new CacheMsgMap()
                            .setImgUrl(url)
                            .setLatitude(Double.parseDouble(location.getLatitudeStr()))
                            .setLongitude(Double.parseDouble(location.getLongitudeStr()))
                            .setScale(Integer.parseInt(location.getScaleStr()))
                            .setAddress(mLabelAddress));

            //add to db
            CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);
        } else if (im.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO_VALUE) { //音频

            String fid = im.getContent().getAudio().getAudioId();
            String url = AppConfig.getImageUrl(fid);
            String seconds = im.getContent().getAudio().getBarTime();
            String sourcePhone = im.getContent().getAudio().getSourcePhone();
            int forwardCount;
            try {
                forwardCount = Integer.valueOf(im.getContent().getAudio().getForwardCount());
            } catch (Exception e) {
                forwardCount = 0;
            }

            //todo_k: 音频
            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_VOICE)
                    .setJsonBodyObj(new CacheMsgVoice()
                            .setVoiceTime(seconds)
                            .setVoiceUrl(url)
                            .setFid(fid)
                            .setSourcePhone(sourcePhone)
                            .setForwardCount(forwardCount));
            downloadAudio(cacheMsgBean, url);

        } else if (im.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE_VALUE) { //文件
            String fid = im.getContent().getFile().getFid();
            String fileName = im.getContent().getFile().getFileName();
            String fileSize = im.getContent().getFile().getFileSize();

            long size = 0;
            try {
                size = Long.parseLong(fileSize);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            //todo_k: 文件
            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_FILE)
                    .setJsonBodyObj(new CacheMsgFile()
                            .setFid(fid)
                            .setFileName(fileName)
                            .setFileUrl(AppConfig.getImageUrl(fid))
                            .setFileRes(IMHelper.getFileImgRes(fileName, false))
                            .setFileSize(size));

            //add to db
            CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);
        } else if (im.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO_VALUE) {//视频
            ContentVideo contentVideo = im.getContent().getVideo();//获取解析jsonBoby的内容
            long time;
            try {
                time = Long.valueOf(contentVideo.getBarTime());
            } catch (Exception e) {
                time = 0L;
            }
            CacheMsgVideo cacheMsgVideo = new CacheMsgVideo();
            cacheMsgVideo.setVideoId(contentVideo.getVideoId())
                    .setFrameId(contentVideo.getFrameId())
                    .setName(contentVideo.getName())
                    .setSize(contentVideo.getSize())
                    .setTime(time);
            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_VIDEO).setJsonBodyObj(cacheMsgVideo);
            CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);
        } else if (im.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_SEND_RED_ENVELOPE_VALUE) {//红包
            ContentRedPackage redPackage = im.getContent().getRedPackage();//获取解析jsonBoby的内容

            CacheMsgRedPackage cacheMsgRedPackage = new CacheMsgRedPackage();
            cacheMsgRedPackage.setValue(redPackage.getValue());
            cacheMsgRedPackage.setRedTitle(redPackage.getTitle());
            cacheMsgRedPackage.setRedUuid(redPackage.getUuid());
            cacheMsgRedPackage.setRedStatus(CacheMsgRedPackage.RED_PACKET_RECEIVE);
            cacheMsgRedPackage.setMsgId(im.getImChat().getMsgId());


            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_REDPACKAGE).setJsonBodyObj(cacheMsgRedPackage);
            CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);
        } else if (im.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_GET_RED_ENVELOPE_VALUE) {//红包被打开
            ContentRedPackage redPackage = im.getContent().getRedPackage();//获取解析jsonBoby的内容

            CacheMsgRedPackage cacheMsgRedPackage = new CacheMsgRedPackage();

            cacheMsgRedPackage.setRedStatus(CacheMsgRedPackage.RED_PACKET_OPENED);
            cacheMsgRedPackage.setReceiveName(redPackage.getReceiveName());
            cacheMsgRedPackage.setReceiveDone(redPackage.getReceiveDone());
            cacheMsgRedPackage.setValue(redPackage.getValue());
            cacheMsgRedPackage.setRedTitle(redPackage.getTitle());
            cacheMsgRedPackage.setRedUuid(redPackage.getUuid());
            cacheMsgRedPackage.setMsgId(im.getImChat().getMsgId());

            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_PACKET_OPENED).setJsonBodyObj(cacheMsgRedPackage);
            CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);
        }
    }


    private void downloadAudio(final CacheMsgBean cacheMsgBean, final String audioUrl) {
        DownloadListener listener = new DownloadListener() {
            @Override
            public void onProgress(int cur, int total) {
            }

            @Override
            public void onFail(String err) {
            }

            @Override
            public void onSuccess(String path) {
                if (cacheMsgBean != null) {
                    if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVoice) {
                        CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) cacheMsgBean.getJsonBodyObj();
                        cacheMsgVoice.setVoicePath(path);
                        cacheMsgBean.setJsonBodyObj(cacheMsgVoice);
                        //add to db
                        CacheMsgHelper.instance().insertOrUpdate(mContext, cacheMsgBean);
                        handlerIMMsgCallback(cacheMsgBean);
                    }
                }
            }
        };
        FileAsyncTaskDownload fileDownload = new FileAsyncTaskDownload(listener);
        String fileName = AppUtils.md5(audioUrl);
        String path = FileConfig.getAudioDownLoadPath();
        fileDownload.setDownloadpath(path);
        fileDownload.setDownLoadFileName(fileName);

        fileDownload.execute(audioUrl);
    }

    /**
     * 清楚所有的通知栏消息
     */
    public void clearAllNotifyMsg() {
        NotificationManager nMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    private Map<String, Integer> notifyCount = new HashMap<>();

    private void addNotifyCount(String targetId, int notifyId) {
        Integer id = notifyCount.get(targetId);
        if (id == null) {
            notifyCount.put(targetId, notifyId);
        }
    }

    private void removeNotifyCount(String targetId) {
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Integer id = notifyCount.get(targetId);
        if (id != null) {
            notificationManager.cancel(id);
        }
    }

    private long notifyTime;
    private int notifyID;

    private void notifyMsg(Context context, String targetId, String desName, String content,
                           boolean isFormPush, boolean isGroup, int groupType) {
        if (!isGroup && AppUtils.isTopActiviy(mContext, IMConnectionActivity.class.getName())
                && !TextUtils.isEmpty(mTargetId)
                && mTargetId.equals(targetId)) {
            return;
        } else if (!isGroup && AppUtils.isTopActiviy(mContext, IMOwnerActivity.class.getName())
                && !TextUtils.isEmpty(mTargetId)
                && mTargetId.equals(targetId)) {
            return;
        } else if (isGroup && AppUtils.isTopActiviy(mContext, IMGroupActivity.class.getName())
                && !TextUtils.isEmpty(mTargetId)
                && mTargetId.equals(targetId)) {
            return;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "im_chat";
            CharSequence name = "im_channel";
            String Description = "im message notify";

            builder = new NotificationCompat.Builder(context, CHANNEL_ID);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(Description);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);

        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder.setContentTitle(context.getString(R.string.from) + desName)
                .setContentText(content)
                .setTicker(content)
                .setDefaults(Notification.DEFAULT_ALL)
                //.setColor(Color.GREEN)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.img_msg);
            builder.setColor(mContext.getResources().getColor(R.color.notification_color));
        } else {
            builder.setSmallIcon(R.drawable.hx_ic_launcher);
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent;
        if (isGroup) {
            resultIntent = new Intent(context, IMGroupActivity.class);
            try {
                int groupId = Integer.parseInt(targetId);
                resultIntent.putExtra(IMGroupActivity.DST_UUID, groupId);
                resultIntent.putExtra(IMGroupActivity.DST_NAME, desName);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            resultIntent = new Intent(context, IMConnectionActivity.class);
            resultIntent.putExtra(IMConnectionActivity.DST_UUID, targetId);
            resultIntent.putExtra(IMConnectionActivity.DST_NAME, desName);
        }

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)

        if (homeAct != null) {
            Intent intent = new Intent(context, homeAct);
            stackBuilder.addNextIntentWithParentStack(intent);
        }

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(content.hashCode(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);


        boolean isNotBadge = AppUtils.isTopActiviy(mContext, IMConnectionActivity.class.getName())
                || AppUtils.isTopActiviy(mContext, IMGroupActivity.class.getName());

        if (TextUtils.isEmpty(targetId)) {
            targetId = "";
        }
        if (TextUtils.isEmpty(mTargetId)) {
            mTargetId = "";
        }
        isNotBadge = isNotBadge && mTargetId.equals(targetId);

        if (isNotBadge) {
            //for 隐藏不该收到的通知,会崩 小米手机必须设置small icon
            /*NotificationCompat.Builder builderShark = new NotificationCompat.Builder(context);
            builderShark.setVibrate(new long[]{200, 200, 200, 200});
            notificationManager.notify(notifyID, builderShark.build());*/
        } else {
            // mId allows you to update the notification later on.
            //处理瞬间大量收到IM消息
            int tempId = targetId.hashCode();
            long time = System.currentTimeMillis() - notifyTime;

            if (!isFormPush) {
                if (groupType < 2) {
                    addBadge(targetId);   //添加桌面圆点
                } else if (groupType == 2) {
                    addBadgeComm(targetId);
                } else if (groupType == 101) {
                    addBadgeOwner(targetId);
                }

            }

            if (time <= 200 && notifyID == tempId) {
                return;
            }

            boolean isClosed = AppUtils.getBooleanSharedPreferences(mContext, "notify" + targetId, false);

            if (isNotify && notificationManager != null && time > 200 && !isClosed) {
                notificationManager.notify(tempId, builder.build());
                notifyID = tempId;
                notifyTime = System.currentTimeMillis();
            }

            addNotifyCount(targetId, tempId);   //添加通知栏消息
        }
    }

    private Map<String, Integer> badgeCount = new HashMap<>();
    private Map<String, Integer> badgeCountOwner = new HashMap<>();
    private Map<String, Integer> badgeCountComm = new HashMap<>();

    public void addBadge(String targetId) {
        Integer value = badgeCount.get(targetId);
        if (value == null) {
            badgeCount.put(targetId, 1);
        } else {
            badgeCount.put(targetId, value + 1);
        }

        String badge = BadgeUtil.mapToJson(badgeCount);
        AppUtils.setStringSharedPreferences(mContext, getBadgeSharedPreferenceKey(), badge);

        int sum = getAllBadgeCount();

        ShortcutBadger.applyCount(mContext, sum); //for 1.1.4+

    }


    public void addBadgeOwner(String targetId) {
        Integer value = badgeCountOwner.get(targetId);
        if (value == null) {
            badgeCountOwner.put(targetId, 1);
        } else {
            badgeCountOwner.put(targetId, value + 1);
        }

        String badge = BadgeUtil.mapToJson(badgeCountOwner);
        AppUtils.setStringSharedPreferences(mContext, getBadgeOwnerSharedPreferenceKey(), badge);

        int sum = getAllBadgeCount();

        ShortcutBadger.applyCount(mContext, sum); //for 1.1.4+

    }


    public void addBadgeComm(String targetId) {
        Integer value = badgeCountComm.get(targetId);
        if (value == null) {
            badgeCountComm.put(targetId, 1);
        } else {
            badgeCountComm.put(targetId, value + 1);
        }

        String badge = BadgeUtil.mapToJson(badgeCountComm);
        AppUtils.setStringSharedPreferences(mContext, getBadgeCommSharedPreferenceKey(), badge);

        int sum = getAllBadgeCount();

        ShortcutBadger.applyCount(mContext, sum); //for 1.1.4+

    }

    /**
     * 跟帐号走
     *
     * @return
     */
    public String getBadgeSharedPreferenceKey() {
        return "badge-" + HuxinSdkManager.instance().getUuid();
    }

    public String getBadgeOwnerSharedPreferenceKey() {
        return "badgeOwner-" + HuxinSdkManager.instance().getUuid();
    }

    public String getBadgeCommSharedPreferenceKey() {
        return "badgeComm-" + HuxinSdkManager.instance().getUuid();
    }


    public void removeBadge(int targetId) {
        removeBadge(String.valueOf(targetId));
    }

    public void removeBadge(String targetId) {
        mTargetId = targetId;
        Integer value = badgeCount.get(targetId);

        if (value != null) {
            int sum = getAllBadgeCount();

            badgeCount.remove(targetId);
            int res = sum - value;
            if (res >= 1) {
                ShortcutBadger.applyCount(mContext, res); //for 1.1.4+
            } else {
                ShortcutBadger.removeCount(mContext); //for 1.1.4+
            }

            String badge = BadgeUtil.mapToJson(badgeCount);
            AppUtils.setStringSharedPreferences(mContext, getBadgeSharedPreferenceKey(), badge);
        }

        removeNotifyCount(targetId);
    }


    public void removeBadgeOwner(int targetId) {
        removeBadgeOwner(String.valueOf(targetId));
    }

    public void removeBadgeOwner(String targetId) {
        mTargetId = targetId;
        Integer value = badgeCountOwner.get(targetId);

        if (value != null) {
            int sum = getAllBadgeCount();

            badgeCountOwner.remove(targetId);
            int res = sum - value;
            if (res >= 1) {
                ShortcutBadger.applyCount(mContext, res); //for 1.1.4+
            } else {
                ShortcutBadger.removeCount(mContext); //for 1.1.4+
            }

            String badge = BadgeUtil.mapToJson(badgeCountOwner);
            AppUtils.setStringSharedPreferences(mContext, getBadgeOwnerSharedPreferenceKey(), badge);
        }

        removeNotifyCount(targetId);
    }


    public void removeBadgeComm(int targetId) {
        removeBadgeComm(String.valueOf(targetId));
    }

    public void removeBadgeComm(String targetId) {
        mTargetId = targetId;
        Integer value = badgeCountComm.get(targetId);

        if (value != null) {
            int sum = getAllBadgeCount();

            badgeCountComm.remove(targetId);
            int res = sum - value;
            if (res >= 1) {
                ShortcutBadger.applyCount(mContext, res); //for 1.1.4+
            } else {
                ShortcutBadger.removeCount(mContext); //for 1.1.4+
            }

            String badge = BadgeUtil.mapToJson(badgeCountComm);
            AppUtils.setStringSharedPreferences(mContext, getBadgeCommSharedPreferenceKey(), badge);
        }

        removeNotifyCount(targetId);
    }


    public int getBadeCount(String targetId) {
        Integer value = badgeCount.get(targetId);
        if (value != null) {
            return value;
        } else {
            return 0;
        }
    }

    public int getBadeCountOwner(String targetId) {
        Integer value = badgeCountOwner.get(targetId);
        if (value != null) {
            return value;
        } else {
            return 0;
        }
    }

    public int getBadeCountComm(String targetId) {
        Integer value = badgeCountComm.get(targetId);
        if (value != null) {
            return value;
        } else {
            return 0;
        }
    }

    public void clearShortcutBadger() {
        badgeCount.clear();
        badgeCountOwner.clear();
        badgeCountComm.clear();
        ShortcutBadger.removeCount(mContext); //for 1.1.4+
    }


    /**
     * 清空本地未读消息计数
     *
     * @param context
     */
    public void clearMsgBadge(Context context) {
        AppUtils.setStringSharedPreferences(context, "notifyAll", "");
        AppUtils.setStringSharedPreferences(context, getBadgeSharedPreferenceKey(), "");
        AppUtils.setStringSharedPreferences(context, getBadgeOwnerSharedPreferenceKey(), "");
        AppUtils.setStringSharedPreferences(context, getBadgeCommSharedPreferenceKey(), "");
        IMMsgManager.instance().clearShortcutBadger();
    }


    public int getAllBadgeCount() {
        int sum = 0;

        if (badgeCount.isEmpty()) {
            String badge = AppUtils.getStringSharedPreferences(mContext, getBadgeSharedPreferenceKey(), "");
            if (!TextUtils.isEmpty(badge)) {
                try {
                    badgeCount = BadgeUtil.jsonToMap(new JSONObject(badge));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        if (badgeCountOwner.isEmpty()) {
            String badge = AppUtils.getStringSharedPreferences(mContext, getBadgeOwnerSharedPreferenceKey(), "");
            if (!TextUtils.isEmpty(badge)) {
                try {
                    badgeCountOwner = BadgeUtil.jsonToMap(new JSONObject(badge));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        if (badgeCountComm.isEmpty()) {
            String badge = AppUtils.getStringSharedPreferences(mContext, getBadgeCommSharedPreferenceKey(), "");
            if (!TextUtils.isEmpty(badge)) {
                try {
                    badgeCountComm = BadgeUtil.jsonToMap(new JSONObject(badge));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");

        for (Map.Entry<String, Integer> item : badgeCount.entrySet()) {
            String key = item.getKey();
            if (!temp.contains(key)) {
                Integer count = item.getValue();
                sum += count;
            }
        }

        for (Map.Entry<String, Integer> item : badgeCountOwner.entrySet()) {
            String key = item.getKey();
            if (!temp.contains(key)) {
                Integer count = item.getValue();
                sum += count;
            }
        }

        for (Map.Entry<String, Integer> item : badgeCountComm.entrySet()) {
            String key = item.getKey();
            if (!temp.contains(key)) {
                Integer count = item.getValue();
                sum += count;
            }
        }

        return sum;

    }


    public int getAllBadgeBubbyCount() {
        if (badgeCount.isEmpty()) {
            String badge = AppUtils.getStringSharedPreferences(mContext, getBadgeSharedPreferenceKey(), "");
            if (!TextUtils.isEmpty(badge)) {
                try {
                    badgeCount = BadgeUtil.jsonToMap(new JSONObject(badge));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        }

        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");

        int sum = 0;
        for (Map.Entry<String, Integer> item : badgeCount.entrySet()) {
            String key = item.getKey();
            if (!temp.contains(key)) {
                Integer count = item.getValue();
                sum += count;
            }
        }
        return sum;
    }


    public int getAllBadgeOwnerCount() {
        if (badgeCountOwner.isEmpty()) {
            String badge = AppUtils.getStringSharedPreferences(mContext, getBadgeOwnerSharedPreferenceKey(), "");
            if (!TextUtils.isEmpty(badge)) {
                try {
                    badgeCountOwner = BadgeUtil.jsonToMap(new JSONObject(badge));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        }

        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");

        int sum = 0;
        for (Map.Entry<String, Integer> item : badgeCountOwner.entrySet()) {
            String key = item.getKey();
            if (!temp.contains(key)) {
                Integer count = item.getValue();
                sum += count;
            }
        }
        return sum;
    }


    public int getAllBadgeCommCount() {
        if (badgeCountComm.isEmpty()) {
            String badge = AppUtils.getStringSharedPreferences(mContext, getBadgeCommSharedPreferenceKey(), "");
            if (!TextUtils.isEmpty(badge)) {
                try {
                    badgeCountComm = BadgeUtil.jsonToMap(new JSONObject(badge));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        }

        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");

        int sum = 0;
        for (Map.Entry<String, Integer> item : badgeCountComm.entrySet()) {
            String key = item.getKey();
            if (!temp.contains(key)) {
                Integer count = item.getValue();
                sum += count;
            }
        }
        return sum;
    }


    public List<Integer> getPushMsgNotifyIdList() {
        return pushMsgNotifyIdList;
    }

    public void cancelPushMsg() {
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (pushMsgNotifyIdList.size() > 0) {
            for (Integer item : pushMsgNotifyIdList) {
                notificationManager.cancel(item);
            }
        }
        pushMsgNotifyIdList.clear();
    }


    /**
     * 是否播放声音
     *
     * @return
     */
    private boolean isSound() {
        if (isRepeatSound()) {
            return false;
        } else {
            String actStr = AppUtils.getTopActiviy(mContext);
            if (actStr.contains("IMConnection")
                    || actStr.contains("com.youmai.huxin.app.activity.MainAct")) {
                return false;
            }
        }
        return true;
    }


    /**
     * 检查提示声音是否重复
     *
     * @return
     */
    private long curTime;

    private boolean isRepeatSound() {
        boolean res = false;
        long click = System.currentTimeMillis();
        if (click - curTime < 1000) {
            res = true;
        }
        curTime = click;
        return res;
    }


    private CacheMsgBean getMemberChangedMsgBean(int groupId, int groupType, String content) {
        return new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setMsgType(CacheMsgBean.GROUP_MEMBER_CHANGED)
                .setGroupType(groupType)
                .setMemberChanged(content)
                .setGroupId(groupId)
                .setTargetUuid(groupId + "");
    }

    private CacheMsgBean getGroupNameChangedMsgBean(int groupId, int groupType, String content) {
        return new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setMsgType(CacheMsgBean.GROUP_NAME_CHANGED)
                .setGroupType(groupType)
                .setMemberChanged(content)
                .setGroupId(groupId)
                .setTargetUuid(groupId + "");
    }


    private CacheMsgBean getGroupOwnerChangedMsgBean(int groupId, int groupType, String content) {
        return new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setMsgType(CacheMsgBean.GROUP_TRANSFER_OWNER)
                .setGroupType(groupType)
                .setMemberChanged(content)
                .setGroupId(groupId)
                .setTargetUuid(groupId + "");
    }


    public boolean isMeInGroup(int groupId) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "atList", "");
        return temp.contains("@" + groupId);
    }


    public void addMeInGroup(int groupId) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "atList", "");
        if (!temp.contains("@" + groupId)) {
            temp += "@" + groupId;
            AppUtils.setStringSharedPreferences(mContext, "atList", temp);
        }
    }


    public void removeMeInGroup(int groupId) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "atList", "");
        temp = temp.replaceAll("@" + groupId, "");
        AppUtils.setStringSharedPreferences(mContext, "atList", temp);
    }

}
