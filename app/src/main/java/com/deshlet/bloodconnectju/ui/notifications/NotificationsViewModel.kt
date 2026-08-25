package com.deshlet.bloodconnectju.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.NotificationRepository
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val notifications = notificationRepository.list() ?: emptyList()
            _uiState.value = _uiState.value.copy(isLoading = false, notifications = notifications)
        }
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
