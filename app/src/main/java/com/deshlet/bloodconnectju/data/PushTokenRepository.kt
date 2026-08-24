package com.deshlet.bloodconnectju.data

import android.os.Build
import android.util.Log
import com.deshlet.bloodconnectju.data.remote.ApiService
import com.deshlet.bloodconnectju.data.remote.dto.RegisterPushTokenRequest
import com.deshlet.bloodconnectju.data.remote.dto.UnregisterPushTokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PushTokenRepository"

/**
 * Keeps the backend's push_tokens table in sync with this device's current
 * FCM registration token. Called after every successful login/register
 * (AuthRepository) and whenever FCM itself rotates the token
 * (BloodConnectFirebaseMessagingService.onNewToken) — either can be the
 * first to learn about a fresh token, so both paths funnel through here.
 *
 * Every method is best-effort: a failed sync just means this device misses
 * push notifications until the next successful one (e.g. next login) — never
 * worth surfacing as an error to the user, and never worth blocking on.
 *
 * `FirebaseMessaging.getToken()` is soft-deprecated in the SDK in favor of
 * `register()`/`onRegistered(installationId)` — a newer, opt-in (manifest
 * flag), callback-driven model built around Firebase Installation IDs
 * instead of a directly-readable token. It's not yet what current Firebase
 * documentation/codelabs use for a standard FCM setup, and switching would
 * mean verifying the backend's send call still works with an installation
 * ID in place of a token — deliberately not taken on in this phase.
 */
@Singleton
class PushTokenRepository @Inject constructor(
    private val api: ApiService,
) {
    @Suppress("DEPRECATION")
    suspend fun syncCurrentToken() {
        runCatching {
            val token = FirebaseMessaging.getInstance().token.await()
            registerToken(token)
        }.onFailure { Log.w(TAG, "Couldn't sync FCM token", it) }
    }

    suspend fun registerToken(token: String) {
        runCatching {
            api.registerPushToken(RegisterPushTokenRequest(token = token, device_name = deviceName()))
        }.onFailure { Log.w(TAG, "Couldn't register FCM token", it) }
    }

    /** Called on logout so this device stops receiving pushes meant for the account that just signed out. */
    @Suppress("DEPRECATION")
    suspend fun unregisterCurrentToken() {
        runCatching {
            val token = FirebaseMessaging.getInstance().token.await()
            api.unregisterPushToken(UnregisterPushTokenRequest(token = token))
        }.onFailure { Log.w(TAG, "Couldn't unregister FCM token", it) }
    }

    private fun deviceName(): String =
        "${Build.MANUFACTURER} ${Build.MODEL}".trim().ifBlank { "Android device" }
}
