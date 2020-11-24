package com.aap.medicore.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.aap.medicore.Activities.Splash;
import com.aap.medicore.Models.InboxMessage;
import com.aap.medicore.R;

public class NotificationsHandler {

    public static final String TAG="NotificationsHandler";
    private static NotificationsHandler notificationsHandler;
    private static final String MAIN_CHANNEL_ID = "com.aap.medicore.Utils.notifications.channel";
    private final String GROUP_CHANNEL_ID = "com.aap.medicore.Utils.notifications.groupchannel";
    public static final Integer GROUP_NOTIFICATION_ID = 777;

    private final Context context;

    private NotificationsHandler(Context context) {
        this.context = context;
        createMainNotificationChannel();
    }

    public static NotificationsHandler getInstance(Context context) {
        if (notificationsHandler == null) {
            notificationsHandler = new NotificationsHandler(context);
        }
        return notificationsHandler;
    }

    public void publishNotification(String title, String content) {
        publishNotification(getNotification(title, content), (int) System.currentTimeMillis());
    }

    public void publishMessageNotification(InboxMessage msg) {
        publishNotification(getMessageNotification(msg), (int) msg.sentTime);
    }


    private void createMainNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = context.getString(R.string.channel_name);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            int importance = NotificationManager.IMPORTANCE_HIGH;


            NotificationChannel mChannel = new NotificationChannel(MAIN_CHANNEL_ID, name, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setSound(uri, audioAttributes);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }
    }


    private void publishNotification(Notification notification, int id) {
        createMainNotificationChannel();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (notificationManager != null) {
                if (notification != null) {
                    notificationManager.notify(id, notification);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "publishNotification("+notification.toString()+","+id+")");
        }

    }

    public void cancelNotification(Integer id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (id != null && notificationManager != null) {
            notificationManager.cancel(id);
            notificationManager.cancel(GROUP_NOTIFICATION_ID);
        }
    }

    private Notification getNotification(String title, String content) {
        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0,
                new Intent(context.getApplicationContext(), Splash.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(context, MAIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setGroup(GROUP_CHANNEL_ID)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentIntent)
                .setChannelId(MAIN_CHANNEL_ID)
                .setVibrate(new long[]{100, 200, 100})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        return mBuilder.build();
    }

    private Notification getMessageNotification(InboxMessage msg) {
        Intent openMsgIntent = new Intent(context.getApplicationContext(), Splash.class);
        openMsgIntent.setAction(Constants.OPEN_INBOX_MESSAGE_ACTION);
        openMsgIntent.putExtra(Constants.INBOX_MESSAGE_ID, msg.id);
        openMsgIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        openMsgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openMsgIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0,
                openMsgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(context, MAIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(msg.title)
                .setContentText(msg.content)
                .setGroup(GROUP_CHANNEL_ID)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentIntent)
                .setChannelId(MAIN_CHANNEL_ID)
                .setVibrate(new long[]{100, 200, 100})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        return mBuilder.build();
    }

}