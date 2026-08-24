package com.deshlet.bloodconnectju.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterPushTokenRequest(
    val token: String,
    val device_name: String? = null,
)

@Serializable
data class UnregisterPushTokenRequest(
    val token: String,
)
