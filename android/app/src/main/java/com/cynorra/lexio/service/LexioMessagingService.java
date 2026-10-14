package com.cynorra.lexio.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Announcements sent from the Firebase console (Messaging) to the topics the app subscribes to:
 * "all" and "country_XX". When the app is in the background the system shows notification messages by itself
 * (default channel set in the manifest); this service handles the foreground case and data-only messages
 * ({@code title} / {@code body} keys).
 */
public class LexioMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if (!NotificationSettings.isEnabled(this)) return;

        String title = null;
        String body = null;
        RemoteMessage.Notification n = message.getNotification();
        if (n != null) {
            title = n.getTitle();
            body = n.getBody();
        }
        Map<String, String> data = message.getData();
        if (title == null) title = data.get("title");
        if (body == null) body = data.get("body");
        if (title == null || body == null) return;

        new LexioNotificationManager(this).showAnnouncement(title, body);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        // Topic subscriptions are tied to the installation and survive token refresh; nothing to store.
    }
}
