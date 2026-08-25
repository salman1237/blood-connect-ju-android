package com.deshlet.bloodconnectju.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.AuthRepository
import com.deshlet.bloodconnectju.data.DeleteAccountResult
import com.deshlet.bloodconnectju.data.ProfileRepository
import com.deshlet.bloodconnectju.data.ProfileResult
import com.deshlet.bloodconnectju.data.remote.dto.DonorProfileUpdateRequest
import com.deshlet.bloodconnectju.data.remote.dto.MetaResponse
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: UserDto? = null,
    val meta: MetaResponse? = null,
    val isSavingAccount: Boolean = false,
    val isSavingDonorProfile: Boolean = false,
    val isSavingPhoto: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, List<String>> = emptyMap(),
    val statusMessage: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val user = authRepository.me()
            val meta = profileRepository.meta()
            _uiState.value = _uiState.value.copy(isLoading = false, user = user, meta = meta)
        }
    }

    fun updateAccount(name: String, email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingAccount = true, errorMessage = null, fieldErrors = emptyMap(), statusMessage = null)
            when (val result = profileRepository.updateAccount(name, email)) {
                is ProfileResult.Success -> _uiState.value = _uiState.value.copy(
                    isSavingAccount = false,
                    user = result.user,
                    statusMessage = "Account details updated.",
                )

                is ProfileResult.Failure -> _uiState.value = _uiState.value.copy(
                    isSavingAccount = false,
                    errorMessage = result.message,
                    fieldErrors = result.fieldErrors,
                )
            }
        }
    }

    fun updateDonorProfile(request: DonorProfileUpdateRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingDonorProfile = true, errorMessage = null, fieldErrors = emptyMap(), statusMessage = null)
            when (val result = profileRepository.updateDonorProfile(request)) {
                is ProfileResult.Success -> _uiState.value = _uiState.value.copy(
                    isSavingDonorProfile = false,
                    user = result.user,
                    statusMessage = "Donor profile updated.",
                )

                is ProfileResult.Failure -> _uiState.value = _uiState.value.copy(
                    isSavingDonorProfile = false,
                    errorMessage = result.message,
                    fieldErrors = result.fieldErrors,
                )
            }
        }
    }

    fun uploadPhoto(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingPhoto = true, errorMessage = null, statusMessage = null)
            when (val result = profileRepository.uploadPhoto(uri)) {
                is ProfileResult.Success -> _uiState.value = _uiState.value.copy(
                    isSavingPhoto = false,
                    user = result.user,
                    statusMessage = "Photo updated.",
                )

                is ProfileResult.Failure -> _uiState.value = _uiState.value.copy(
                    isSavingPhoto = false,
                    errorMessage = result.message,
                )
            }
        }
    }

    fun removePhoto() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingPhoto = true, errorMessage = null, statusMessage = null)
            when (val result = profileRepository.removePhoto()) {
                is ProfileResult.Success -> _uiState.value = _uiState.value.copy(
                    isSavingPhoto = false,
                    user = result.user,
                    statusMessage = "Photo removed.",
                )

                is ProfileResult.Failure -> _uiState.value = _uiState.value.copy(
                    isSavingPhoto = false,
                    errorMessage = result.message,
                )
            }
        }
    }

    fun deleteAccount(password: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, errorMessage = null, fieldErrors = emptyMap())
            when (val result = profileRepository.deleteAccount(password)) {
                is DeleteAccountResult.Success -> {
                    // The account (and its token) is already gone server-side —
                    // clear the local one too so the app doesn't keep trying to
                    // use it, then let the caller navigate back to Login.
                    authRepository.forgetLocalSession()
                    _uiState.value = _uiState.value.copy(isDeleting = false)
                    onDeleted()
                }

                is DeleteAccountResult.Failure -> _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = result.message,
                    fieldErrors = result.fieldErrors,
                )
            }
        }
    }

    suspend fun logout() = authRepository.logout()
}
