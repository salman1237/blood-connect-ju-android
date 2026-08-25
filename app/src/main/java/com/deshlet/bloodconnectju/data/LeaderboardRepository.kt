package com.deshlet.bloodconnectju.data

import com.deshlet.bloodconnectju.data.remote.ApiService
import com.deshlet.bloodconnectju.data.remote.dto.LeaderboardEntryDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun get(): List<LeaderboardEntryDto>? =
        runCatching { api.getLeaderboard() }.getOrNull()?.takeIf { it.isSuccessful }?.body()
}
