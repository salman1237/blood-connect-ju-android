package com.deshlet.bloodconnectju.data.remote.dto

import kotlinx.serialization.Serializable

/** Mirrors App\Http\Controllers\Api\V1\NotificationController::index() — every notification already carries a plain 'message' string and, where relevant, a 'request_id' for deep-linking. */
@Serializable
data class NotificationDto(
    val id: String,
    val message: String,
    val request_id: Int? = null,
    val read_at: String? = null,
    val created_at: String,
)

@Serializable
data class UpdateNotificationSettingsRequest(
    val email_notifications_enabled: Boolean,
)
