package com.deshlet.bloodconnectju.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import com.deshlet.bloodconnectju.R

const val BLOOD_REQUESTS_CHANNEL_ID = "blood_requests"

/**
 * Must run before any push can arrive, not lazily inside the messaging
 * service — a backgrounded app never calls into our code for a message that
 * carries a `notification` block (FCM auto-displays it straight from system
 * code using the manifest's default_notification_channel_id), and if that
 * channel doesn't exist yet at that point, FCM silently substitutes its own
 * generic "fcm_fallback_notification_channel" instead. Found via a live push
 * to a real device that landed correctly but under the wrong channel — this
 * is called from BloodConnectApplication.onCreate() so the channel always
 * exists by the time the first push can possibly arrive.
 */
fun ensureBloodRequestsNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val manager = context.getSystemService<NotificationManager>() ?: return
    if (manager.getNotificationChannel(BLOOD_REQUESTS_CHANNEL_ID) != null) return

    manager.createNotificationChannel(
        NotificationChannel(
            BLOOD_REQUESTS_CHANNEL_ID,
            context.getString(R.string.notification_channel_blood_requests),
            NotificationManager.IMPORTANCE_HIGH,
        ),
    )
}
