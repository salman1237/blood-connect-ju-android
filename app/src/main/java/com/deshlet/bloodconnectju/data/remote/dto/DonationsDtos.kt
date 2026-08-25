package com.deshlet.bloodconnectju.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Mirrors Api\V1\DonationsController's response shape — the same
 * BadgeDto/DonationHistoryEntryDto shapes DonorDetailResource already
 * established (see DonorDtos.kt), reused rather than redeclared, since
 * both endpoints emit identical per-entry JSON.
 */
@Serializable
data class DonationsResponseDto(
    val donation_history: List<DonationHistoryEntryDto>,
    val badges: List<BadgeDto>,
)
