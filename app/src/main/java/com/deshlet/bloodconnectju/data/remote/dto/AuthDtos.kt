package com.deshlet.bloodconnectju.data.remote.dto

import kotlinx.serialization.Serializable

// Field names mirror the Laravel API's JSON snake_case exactly (see
// App\Support\RegistrationValidation and App\Http\Resources\Api\UserResource
// on the backend) — no manual key-mapping needed on either side.

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val role: String,
    val gender: String,
    val date_of_birth: String,
    val device_name: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val device_name: String,
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto,
)

@Serializable
data class DonorProfileDto(
    val blood_group: String? = null,
    val is_available: Boolean? = null,
    val last_donation_date: String? = null,
    val trust_score: Int? = null,
)

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified: Boolean,
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
    val email_notifications_enabled: Boolean? = null,
    val is_active: Boolean,
    val is_admin: Boolean,
    val is_verifier: Boolean,
    val has_completed_onboarding: Boolean,
    val donor_profile: DonorProfileDto? = null,
)

@Serializable
data class MessageResponse(
    val message: String,
)

/** Shape of a Laravel ValidationException JSON response (422). */
@Serializable
data class ValidationErrorResponse(
    val message: String,
    val errors: Map<String, List<String>> = emptyMap(),
)
