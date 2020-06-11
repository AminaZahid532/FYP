package com.example.juniorhome.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.juniorhome.Messaging.activities.ChatActivity;
import com.example.juniorhome.R;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotification extends FirebaseMessagingService {

    private static final String TAG = "PushNotification";
    Notification.InboxStyle inboxStyle = new Notification.InboxStyle();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "%s on new token called");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "%s onCreate");
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "%s onMessageReceived");
        String fromUserId = remoteMessage.getData().get("userId");
        String fromUser = remoteMessage.getData().get("fromUser");
        String userPicUrl = remoteMessage.getData().get("userPicUrl");
        String notificationText = remoteMessage.getData().get("notificationText");

        if (fromUserId == null || fromUser == null || notificationText == null) {
            return;
        }

        // This is the ChatNotification Channel ID. More about this in the next section
        final String NOTIFICATION_CHANNEL_ID = "channel_id";
        //ChatNotification Channel ID passed as a parameter here will be ignored for all the Android versions below 8.0
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(fromUser);
        builder.setContentText(notificationText);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        //
        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra("visit_user_id", fromUserId);
        resultIntent.putExtra("visit_user_name", fromUser);
        resultIntent.putExtra("visit_image", userPicUrl);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(resultPendingIntent);
        Notification notification = builder.build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        //ChatNotification channel should only be created for devices running Android 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //User visible Channel Name
            String CHANNEL_NAME = "ChatNotification Channel";

            // Importance applicable to all the notifications in this Channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);

            //Boolean value to set if lights are enabled for Notifications from this Channel
            notificationChannel.enableLights(false);

            //Boolean value to set if vibration are enabled for Notifications from this Channel
            notificationChannel.enableVibration(false);

            //Sets the color of ChatNotification Light
            notificationChannel.setLightColor(Color.GREEN);

            //Set the vibration pattern for notifications. Pattern is in milliseconds with the format {delay,play,sleep,play,sleep...}
            notificationChannel.setVibrationPattern(new long[]{
                    500,
                    500,
                    500,
                    500,
                    500
            });
            notificationChannel.setDescription("Testing");
            notificationChannel.setShowBadge(true);

            //Sets whether notifications from these Channel should be visible on Lockscreen or not
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManagerCompat.createNotificationChannel(notificationChannel);
        }
        notificationManagerCompat.notify(0, notification);
    }
}
