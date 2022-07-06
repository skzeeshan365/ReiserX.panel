package com.reiserx.myapplication24.Service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.reiserx.myapplication24.Classes.NotificationUtils;

import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        remoteMessage.getData();

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String content = data.get("content");
        String id = data.get("id");
                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.sendNotification(this, title, content, Integer.parseInt(Objects.requireNonNull(id)));
        }
    }
