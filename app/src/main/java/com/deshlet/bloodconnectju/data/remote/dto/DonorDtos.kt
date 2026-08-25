package com.deshlet.bloodconnectju.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DonorProfileDetailDto(
    val blood_group: String,
    val is_available: Boolean,
    val is_eligible: Boolean,
    val next_eligible_date: String? = null,
    val trust_score: Int,
)

@Serializable
data class BadgeDto(
    val name: String,
    val slug: String,
    val description: String,
    val earned_at: String,
)

@Serializable
data class DonationHistoryEntryDto(
    val hospital_name: String? = null,
    val confirmed_at: String,
)

/** Mirrors App\Http\Resources\Api\DonorDetailResource — deliberately no email, same as the web donor-profile page. */
@Serializable
data class DonorDetailDto(
    val id: Int,
    val name: String,
    val role: String,
    val gender: String? = null,
    val date_of_birth: String? = null,
    val age: Int? = null,
    val hall: String? = null,
    val department: String? = null,
    val batch: String? = null,
    val phone: String? = null,
    val whatsapp_number: String? = null,
    val phone_has_whatsapp: Boolean? = null,
    val whatsapp_url: String? = null,
    val avatar_url: String? = null,
    val donor_profile: DonorProfileDetailDto,
    val badges: List<BadgeDto> = emptyList(),
    val donation_history: List<DonationHistoryEntryDto> = emptyList(),
)
