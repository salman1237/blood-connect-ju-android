package com.deshlet.bloodconnectju.notifications

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.deshlet.bloodconnectju.MainActivity
import com.deshlet.bloodconnectju.R
import com.deshlet.bloodconnectju.data.PushTokenRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/** Intent extras a notification tap carries into MainActivity — read there to deep-link into the relevant screen. */
object PushIntentExtras {
    const val REQUEST_ID = "request_id"
}

/**
 * FCM only calls onMessageReceived reliably while the app is in the
 * foreground. When it's backgrounded, Android auto-displays a system
 * notification straight from the message's `notification` block and
 * launches the app with the `data` block attached as Intent extras on tap —
 * no code of ours runs, but MainActivity's extra-reading still handles it
 * the same way. This class only has to build the notification by hand for
 * the foreground case.
 */
@AndroidEntryPoint
class BloodConnectFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var pushTokenRepository: PushTokenRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // onNewToken(String) is soft-deprecated in favor of the newer, opt-in
    // onRegistered(installationId) callback — see the note on
    // PushTokenRepository for why that migration isn't taken on here.
    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        scope.launch { pushTokenRepository.syncCurrentToken() }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: return
        val body = message.notification?.body ?: ""
        val requestId = message.data["request_id"]?.toIntOrNull()

        showNotification(title, body, requestId)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun showNotification(title: String, body: String, requestId: Int?) {
        // Belt-and-suspenders — BloodConnectApplication.onCreate() already
        // does this eagerly, but costs nothing to confirm here too.
        ensureBloodRequestsNotificationChannel(this)

        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (requestId != null) putExtra(PushIntentExtras.REQUEST_ID, requestId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestId ?: 0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, BLOOD_REQUESTS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Posting can throw on API 33+ if POST_NOTIFICATIONS was denied —
        // best-effort, same as every other push-related failure.
        runCatching {
            NotificationManagerCompat.from(this).notify(Random.nextInt(), notification)
        }
    }
}
