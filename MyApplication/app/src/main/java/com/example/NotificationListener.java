package com.example;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

@SuppressLint("OverrideAbstract")
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class NotificationListener extends NotificationListenerService {
    public static String NOTIFICATION_INTENT = "notification_event";
    public static String NOTIFICATION_PACKAGE_NAME = "notification_package_name";
    public static String NOTIFICATION_MESSAGE = "notification_message";
    public static String NOTIFICATION_TITLE = "notification_title";



    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        // Retrieve package name to set as title.
        String packageName = notification.getPackageName();
        // Retrieve extra object from notification to extract payload.
        Bundle extras = notification.getNotification().extras;

        Log.d("NOTIFICATION", packageName);
    }
}
