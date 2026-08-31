package com.deshlet.bloodconnectju.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.AuthRepository
import com.deshlet.bloodconnectju.data.ProfileRepository
import com.deshlet.bloodconnectju.data.ProfileResult
import com.deshlet.bloodconnectju.data.remote.dto.DonorProfileUpdateRequest
import com.deshlet.bloodconnectju.data.remote.dto.MetaResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isLoading: Boolean = true,
    val meta: MetaResponse? = null,
    // Pre-filled from the account's existing role/gender/DOB — set at
    // registration already, editable again here since UpdateDonorProfileRequest
    // (shared with the profile-edit screen) always requires them.
    val prefillRole: String = "student",
    val prefillGender: String = "male",
    val prefillDateOfBirth: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, List<String>> = emptyMap(),
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    init {
        viewModelScope.launch {
            val meta = profileRepository.meta()
            val user = authRepository.me()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                meta = meta,
                prefillRole = user?.role ?: "student",
                prefillGender = user?.gender ?: "male",
                prefillDateOfBirth = user?.date_of_birth ?: "",
            )
        }
    }

    fun submit(
        bloodGroup: String,
        role: String,
        gender: String,
        dateOfBirth: String,
        department: String,
        hall: String?,
        batch: String?,
        phone: String?,
        hasWhatsapp: Boolean,
        whatsappNumber: String?,
        phoneVisibility: String,
        isAvailable: Boolean,
        lastDonationDate: String?,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null, fieldErrors = emptyMap())
            val request = DonorProfileUpdateRequest(
                blood_group = bloodGroup,
                role = role,
                gender = gender,
                date_of_birth = dateOfBirth,
                department = department,
                hall = hall?.ifBlank { null },
                batch = batch?.ifBlank { null },
                phone = phone?.ifBlank { null },
                phone_has_whatsapp = hasWhatsapp,
                whatsapp_number = if (hasWhatsapp) null else whatsappNumber?.ifBlank { null },
                phone_visibility = phoneVisibility,
                is_available = isAvailable,
                last_donation_date = lastDonationDate?.ifBlank { null },
            )
            when (val result = profileRepository.updateDonorProfile(request)) {
                is ProfileResult.Success -> {
                    _uiState.value = _uiState.value.copy(isSubmitting = false)
                    onSuccess()
                }

                is ProfileResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        errorMessage = result.message,
                        fieldErrors = result.fieldErrors,
                    )
                }
            }
        }
    }
}
