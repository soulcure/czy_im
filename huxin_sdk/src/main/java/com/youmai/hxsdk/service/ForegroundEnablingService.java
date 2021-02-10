package com.youmai.hxsdk.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.youmai.hxsdk.R;

/**
 * Created by colin on 2017/3/27.
 */

public class ForegroundEnablingService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (HuxinService.instance == null) {
            //throw new RuntimeException(HuxinService.class.getSimpleName() + " not running");
        } else {
            //Set both services to foreground using the same notification id, resulting in just one notification
            startForeground(HuxinService.instance);
            startForeground(this);

            //Cancel this service's notification, resulting in zero notifications
            stopForeground(true);

            //Stop this service so we don't waste RAM.
            //Must only be called *after* doing the work or the notification won't be hidden.
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private static final int NOTIFICATION_ID = 10;

    private static void startForeground(Service service) {
        Notification.Builder builder = new Notification.Builder(service);
        builder.setSmallIcon(R.drawable.img_msg);
        service.startForeground(NOTIFICATION_ID, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}