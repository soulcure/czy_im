package com.color.czy;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.color.czy.ui.BuddyActivity;
import com.color.czy.ui.MainActivity;
import com.facebook.stetho.Stetho;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.proto.YouMaiBuddy;

import java.util.ArrayList;
import java.util.List;


public class CZYApplication extends MultiDexApplication {

    private Context mContext;

    private List<String> mobile = new ArrayList<>();
    private List<String> uuid = new ArrayList<>();
    private List<String> userId = new ArrayList<>();
    private List<String> avatar = new ArrayList<>();
    private List<String> gender = new ArrayList<>();
    private List<String> nickName = new ArrayList<>();
    private List<String> userName = new ArrayList<>();
    private List<String> displayName = new ArrayList<>();
    private List<String> community_name = new ArrayList<>();
    private List<String> community_uuid = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        List<ContactBean> list = TestData.contactList(this);

        for (ContactBean item : list) {
            mobile.add(item.getMobile());
            uuid.add(item.getUuid());
            userId.add(item.getUserId());
            avatar.add(item.getAvatar());
            gender.add(item.getSex());
            nickName.add(item.getNickName());
            userName.add(item.getUserName());
            displayName.add(item.getDisplayName());
            community_name.add(item.getOrgName());
            community_uuid.add(item.getOrgId());
        }


        HuxinSdkManager.instance().init(this);
        HuxinSdkManager.instance().setHomeAct(MainActivity.class);

        Stetho.initializeWithDefaults(this);


        //监听联系人操作通知
        HuxinSdkManager.instance().regeditCommonPushMsg(new ProtoCallback.BuddyNotify() {
            @Override
            public void result(YouMaiBuddy.IMOptBuddyNotify notify) {
                String srcUuid = notify.getSrcUserId();
                String dstUuid = notify.getDestUserId();
                String optRemark = notify.getOptRemark();
                String nickName = notify.getNickname();
                String userName = notify.getUsername();
                String avatar = notify.getAvatar();
                String orgName = notify.getOrgName();
                String realName = notify.getRealName();

                YouMaiBuddy.BuddyOptType type = notify.getOptType();

                if (type == YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_REQ) { //请求添加好友
                    notifyMsg(nickName, optRemark, true);    //新朋友状态添加一条，并有同意按钮
                } else if (type == YouMaiBuddy.BuddyOptType.BUDDY_OPT_ADD_AGREE) { //对方同意添加好友
                    //新朋友状态修改为已添加
                    notifyMsg(nickName, optRemark, false);    //新朋友状态添加一条，并有同意按钮
                }
            }
        });
    }


    private void notifyMsg(String nickName, String optRemark, boolean isAdd) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "im_chat";
            CharSequence name = "im_channel";
            String Description = "im message notify";

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(Description);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);

        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setContentTitle(getString(R.string.from) + nickName);

        if (isAdd) {
            builder.setContentText(getString(R.string.add_friend))
                    .setTicker(getString(R.string.add_friend));
        } else {
            builder.setContentText(getString(R.string.add_friend_agree))
                    .setTicker(getString(R.string.add_friend_agree));
        }

        builder.setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(com.youmai.hxsdk.R.drawable.img_msg);
            builder.setColor(mContext.getResources().getColor(com.youmai.hxsdk.R.color.notification_color));
        } else {
            builder.setSmallIcon(com.youmai.hxsdk.R.drawable.hx_ic_launcher);
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, BuddyActivity.class);  //点击打开的activity
        resultIntent.putExtra("optRemark", optRemark);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)

        Intent intent = new Intent();
        intent.setClassName(this, "com.color.czy.ui.MainActivity"); //点击打开的activity后，返回的activity
        stackBuilder.addNextIntentWithParentStack(intent);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(nickName.hashCode(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(nickName.hashCode(), builder.build());

    }


    public List<String> getMobile() {
        return mobile;
    }

    public List<String> getUuid() {
        return uuid;
    }

    public List<String> getUserId() {
        return userId;
    }

    public List<String> getAvatar() {
        return avatar;
    }

    public List<String> getGender() {
        return gender;
    }

    public List<String> getNickName() {
        return nickName;
    }

    public List<String> getUserName() {
        return userName;
    }

    public List<String> getDisplayName() {
        return displayName;
    }

    public List<String> getCommunityUuid() {
        return community_uuid;
    }

    public List<String> getCommunityName() {
        return community_name;
    }


}

