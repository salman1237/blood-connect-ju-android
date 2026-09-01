package com.deshlet.bloodconnectju.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.AuthRepository
import com.deshlet.bloodconnectju.data.ProfileRepository
import com.deshlet.bloodconnectju.data.ProfileResult
import com.deshlet.bloodconnectju.data.remote.dto.OrgSettingsDto
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = true,
    val user: UserDto? = null,
    val org: OrgSettingsDto? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val user = authRepository.me()
            val org = profileRepository.meta()?.org
            _uiState.value = _uiState.value.copy(isLoading = false, user = user, org = org)
        }
    }

    fun setEmailNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            when (val result = profileRepository.updateNotificationSettings(enabled)) {
                is ProfileResult.Success -> _uiState.value = _uiState.value.copy(isSaving = false, user = result.user)
                is ProfileResult.Failure -> _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = result.message)
            }
        }
    }

    suspend fun logout() = authRepository.logout()
}
