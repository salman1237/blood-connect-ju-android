package com.deshlet.bloodconnectju.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.AuthRepository
import com.deshlet.bloodconnectju.data.NotificationRepository
import com.deshlet.bloodconnectju.data.RealtimeService
import com.deshlet.bloodconnectju.data.remote.dto.NotificationDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationDto> = emptyList(),
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository,
    private val realtimeService: RealtimeService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState

    private var ownChannelSubscription: AutoCloseable? = null

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val notifications = notificationRepository.list() ?: emptyList()
            _uiState.value = _uiState.value.copy(isLoading = false, notifications = notifications)
        }

        // Only needs to happen once per ViewModel instance, but refresh()
        // is what every entry point into this screen already calls
        // (LaunchedEffect(Unit) on open), so piggybacking here avoids a
        // separate init-time network round trip just to learn the id.
        if (ownChannelSubscription == null) {
            viewModelScope.launch {
                val userId = authRepository.me()?.id ?: return@launch
                ownChannelSubscription = realtimeService.subscribeToOwnNotifications(userId, ::refresh)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ownChannelSubscription?.close()
    }

    /** Re-fetches the list afterward rather than patching state locally — one extra call, no fake timestamp to fabricate for read_at. */
    fun markRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markRead(id)
            refresh()
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            notificationRepository.markAllRead()
            refresh()
        }
    }
}
