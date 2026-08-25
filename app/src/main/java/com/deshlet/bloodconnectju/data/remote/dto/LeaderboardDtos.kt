package com.deshlet.bloodconnectju.data.remote.dto

import kotlinx.serialization.Serializable

/** Mirrors Api\V1\LeaderboardController — rank is derived from array position, not sent by the server. */
@Serializable
data class LeaderboardEntryDto(
    val group_name: String,
    val donations: Int,
)
