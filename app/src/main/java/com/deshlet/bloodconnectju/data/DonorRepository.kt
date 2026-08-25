package com.deshlet.bloodconnectju.data

import com.deshlet.bloodconnectju.data.remote.ApiService
import com.deshlet.bloodconnectju.data.remote.dto.DonorDetailDto
import com.deshlet.bloodconnectju.data.remote.dto.DonorSummaryDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DonorRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun list(search: String? = null, bloodGroup: String? = null, hall: String? = null): List<DonorSummaryDto>? =
        runCatching { api.listDonors(search, bloodGroup, hall) }.getOrNull()?.takeIf { it.isSuccessful }?.body()

    suspend fun get(id: Int): DonorDetailDto? =
        runCatching { api.getDonor(id) }.getOrNull()?.takeIf { it.isSuccessful }?.body()
}
