package com.deshlet.bloodconnectju.data

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "RealtimeService"

// Public app key — safe to embed, this is the client-facing identifier
// Reverb/Pusher clients present when connecting, not a secret (mirrors
// web's window.__reverbConfig.key, rendered server-side into every
// authenticated page for the same reason). REVERB_APP_SECRET never leaves
// the server on either platform.
private const val REVERB_KEY = "f63f5a56acb164fb10de190ed1072f57"
private const val REVERB_HOST = "ws.bloodconnectju.org"
private const val BROADCASTING_AUTH_URL = "https://bloodconnectju.org/api/v1/broadcasting/auth"

// The event name Laravel's default per-notification broadcasting puts on
// the wire when a Notification doesn't override broadcastAs() (none of
// this app's do) — the notification's own fully-qualified PHP class name,
// via Illuminate\Notifications\Events\BroadcastNotificationCreated::broadcastAs().
// Laravel Echo's JS .notification() helper hides this from web's own code;
// this low-level client has to bind to it directly.
private const val NOTIFICATION_EVENT = "Illuminate\\Notifications\\Events\\BroadcastNotificationCreated"

/**
 * Thin wrapper around the Pusher-protocol client (Reverb deliberately
 * speaks the same wire protocol for exactly this kind of compatibility —
 * see .claude-progress.md's "real-time updates" phase). Public channels
 * (requests feed, a single request's activity) need no auth at all; the
 * private per-user notification channel needs a Bearer-token authorizer
 * pointed at /api/v1/broadcasting/auth, since this app has no session
 * cookie the way web's Echo client does.
 *
 * Every subscribe* method returns an AutoCloseable a ViewModel closes when
 * its own scope ends — same lifecycle-tied pattern as
 * `LaunchedEffect(Unit) { viewModel.refresh() }` already uses everywhere
 * else in this app, just for a subscription instead of a one-shot fetch.
 */
@Singleton
class RealtimeService @Inject constructor(
    private val tokenStore: TokenStore,
) {
    private var pusher: Pusher? = null

    @Synchronized
    private fun client(): Pusher {
        pusher?.let { return it }

        val token = runBlocking { tokenStore.currentToken() }
        val authorizer = HttpAuthorizer(BROADCASTING_AUTH_URL).apply {
            if (!token.isNullOrBlank()) setHeaders(mapOf("Authorization" to "Bearer $token"))
        }
        val options = PusherOptions()
            .setHost(REVERB_HOST)
            .setWssPort(443)
            .setWsPort(443)
            .setEncrypted(true)
            .setAuthorizer(authorizer)

        return Pusher(REVERB_KEY, options).also {
            it.connect(
                object : ConnectionEventListener {
                    override fun onConnectionStateChange(change: ConnectionStateChange) {
                        Log.i(TAG, "Connection state: ${change.previousState} -> ${change.currentState}")
                    }

                    override fun onError(message: String, code: String?, e: Exception?) {
                        Log.w(TAG, "Connection error: $message (code=$code)", e)
                    }
                },
            )
            pusher = it
        }
    }

    fun subscribeToRequestsFeed(onUpdate: () -> Unit): AutoCloseable =
        subscribePublic("requests", "RequestFeedUpdated", onUpdate)

    fun subscribeToRequestActivity(requestId: Int, onUpdate: () -> Unit): AutoCloseable =
        subscribePublic("request.$requestId", "RequestActivityUpdated", onUpdate)

    /** userId is the current account's own id — only ever subscribed to your own channel. */
    fun subscribeToOwnNotifications(userId: Int, onUpdate: () -> Unit): AutoCloseable {
        val channelName = "App.Models.User.$userId"
        return runCatching {
            val channel = client().subscribePrivate(channelName)
            val listener = SubscriptionEventListener { _, _, _ -> onUpdate() }
            channel.bind(NOTIFICATION_EVENT, listener)
            AutoCloseable {
                runCatching { channel.unbind(NOTIFICATION_EVENT, listener) }
                runCatching { client().unsubscribe(channelName) }
            }
        }.onFailure { Log.w(TAG, "Couldn't subscribe to $channelName", it) }
            .getOrElse { AutoCloseable {} }
    }

    private fun subscribePublic(channelName: String, eventName: String, onUpdate: () -> Unit): AutoCloseable =
        runCatching {
            val channel = client().subscribe(channelName)
            val listener = SubscriptionEventListener { _, ev, _ ->
                Log.i(TAG, "Event on $channelName: $ev")
                onUpdate()
            }
            channel.bind(eventName, listener)
            Log.i(TAG, "Subscribed to $channelName, bound to $eventName")
            AutoCloseable {
                runCatching { channel.unbind(eventName, listener) }
                runCatching { client().unsubscribe(channelName) }
            }
        }.onFailure { Log.w(TAG, "Couldn't subscribe to $channelName", it) }
            .getOrElse { AutoCloseable {} }

    /** Called on logout — no reason to hold a socket open for a signed-out session. */
    fun disconnect() {
        pusher?.disconnect()
        pusher = null
    }
}
