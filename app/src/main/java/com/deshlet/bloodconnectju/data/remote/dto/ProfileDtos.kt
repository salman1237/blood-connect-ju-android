package com.deshlet.bloodconnectju.data.remote.dto

import kotlinx.serialization.Serializable

/** GET /api/v1/meta — mirrors config/juniv.php + the same static lists OnboardingController passes to the web view. */
@Serializable
data class MetaResponse(
    val halls: List<String>,
    val departments: Map<String, List<String>>,
    val blood_groups: List<String>,
    val batches: List<String>,
    val org: OrgSettingsDto? = null,
)

/**
 * Admin-editable org credit — mirrors web's partials/org-credit.blade.php,
 * backed by App\Models\AppSetting. Each credit line has its own logo (JUCSU's
 * next to funded_by, Badhan's next to maintained_by), not one shared logo.
 */
@Serializable
data class OrgSettingsDto(
    val funded_by: String? = null,
    val funded_by_logo_url: String? = null,
    val maintained_by: String? = null,
    val maintained_by_logo_url: String? = null,
)

/**
 * PATCH /api/v1/donor-profile body — mirrors UpdateDonorProfileRequest's
 * rules exactly (shared on the backend by onboarding and the profile-edit
 * form, so this one DTO backs both screens on Android too).
 */
@Serializable
data class DonorProfileUpdateRequest(
    val blood_group: String,
    val role: String? = null,
    val gender: String,
    val date_of_birth: String,
    val department: String,
    val hall: String? = null,
    val batch: String? = null,
    val phone: String? = null,
    val phone_has_whatsapp: Boolean = true,
    val whatsapp_number: String? = null,
    val phone_visibility: String = "public",
    val is_available: Boolean = true,
    val last_donation_date: String? = null,
)

/** PATCH /api/v1/profile body — mirrors App\Http\Requests\ProfileUpdateRequest (account-level fields, not donor-specific). */
@Serializable
data class AccountUpdateRequest(
    val name: String,
    val email: String,
)

/** DELETE /api/v1/profile body — password-confirmed account deletion. */
@Serializable
data class DeleteAccountRequest(
    val password: String,
)
