package com.deshlet.bloodconnectju.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.AuthRepository
import com.deshlet.bloodconnectju.data.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, List<String>> = emptyMap(),
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    /** null = not yet known (still reading from disk), then true/false. */
    val isLoggedIn: StateFlow<Boolean?> = authRepository.isLoggedIn
        .map<Boolean, Boolean?> { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** onSuccess receives whether onboarding is already complete, so the caller can route straight there without an extra round trip — the login/register response already carries it. */
    fun login(email: String, password: String, onSuccess: (hasCompletedOnboarding: Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.login(email, password)) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState()
                    onSuccess(result.user.has_completed_onboarding)
                }

                is AuthResult.Failure -> {
                    _uiState.value = AuthUiState(errorMessage = result.message, fieldErrors = result.fieldErrors)
                }
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        role: String,
        gender: String,
        dateOfBirth: String,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.register(
                name = name,
                email = email,
                password = password,
                passwordConfirmation = passwordConfirmation,
                role = role,
                gender = gender,
                dateOfBirth = dateOfBirth,
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState()
                    onSuccess()
                }

                is AuthResult.Failure -> {
                    _uiState.value = AuthUiState(errorMessage = result.message, fieldErrors = result.fieldErrors)
                }
            }
        }
    }

    suspend fun fetchProfile() = authRepository.me()

    suspend fun logout() = authRepository.logout()
}
