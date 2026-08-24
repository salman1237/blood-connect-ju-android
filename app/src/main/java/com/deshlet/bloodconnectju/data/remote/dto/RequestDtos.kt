package com.deshlet.bloodconnectju.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RequesterSummaryDto(
    val id: Int,
    val name: String,
    val hall: String? = null,
    val department: String? = null,
)

@Serializable
data class DonorRefDto(
    val id: Int? = null,
    val name: String? = null,
)

@Serializable
data class ResponseSummaryDto(
    val id: Int,
    val status: String,
    val donor: DonorRefDto,
    val is_mutually_confirmed: Boolean,
)

/** Mirrors App\Http\Resources\Api\BloodRequestResource. */
@Serializable
data class BloodRequestDto(
    val id: Int,
    val blood_group: String,
    val units_needed: Int,
    val hospital_name: String,
    val location: String? = null,
    val urgency: String,
    val patient_context: String? = null,
    val contact_method: String,
    val status: String,
    val is_verified: Boolean,
    val expires_at: String? = null,
    val created_at: String? = null,
    val requester: RequesterSummaryDto,
    val responses: List<ResponseSummaryDto>? = null,
)

@Serializable
data class CreateRequestBody(
    val blood_group: String,
    val units_needed: Int,
    val hospital_name: String,
    val location: String? = null,
    val urgency: String,
    val patient_context: String? = null,
    val contact_method: String,
)

/** Mirrors Api\V1\RequestController::stats(). */
@Serializable
data class RequestStatsDto(
    val active: Int,
    val critical: Int,
    val fulfilled_today: Int,
    val registered_donors: Int,
)

/** Mirrors App\Http\Resources\Api\DonorSummaryResource. */
@Serializable
data class DonorSummaryDto(
    val id: Int,
    val name: String,
    val blood_group: String,
    val hall: String? = null,
    val department: String? = null,
    val avatar_url: String? = null,
    val whatsapp_url: String? = null,
)
