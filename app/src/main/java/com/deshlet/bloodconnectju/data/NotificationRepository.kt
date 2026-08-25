package com.deshlet.bloodconnectju.data

import com.deshlet.bloodconnectju.data.remote.ApiService
import com.deshlet.bloodconnectju.data.remote.dto.NotificationDto
import javax.inject.Inject
import javax.inject.Singleton

/** The current user's own notifications — API twin of web's NotificationController. */
@Singleton
class NotificationRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun list(): List<NotificationDto>? =
        runCatching { api.listNotifications() }.getOrNull()?.takeIf { it.isSuccessful }?.body()

    suspend fun markRead(id: String): Boolean =
        runCatching { api.markNotificationRead(id) }.getOrNull()?.isSuccessful == true

    suspend fun markAllRead(): Boolean =
        runCatching { api.markAllNotificationsRead() }.getOrNull()?.isSuccessful == true
}
