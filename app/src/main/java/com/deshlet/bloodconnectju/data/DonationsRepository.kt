package com.deshlet.bloodconnectju.data

import com.deshlet.bloodconnectju.data.remote.ApiService
import com.deshlet.bloodconnectju.data.remote.dto.DonationsResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DonationsRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun get(): DonationsResponseDto? =
        runCatching { api.getDonations() }.getOrNull()?.takeIf { it.isSuccessful }?.body()
}
