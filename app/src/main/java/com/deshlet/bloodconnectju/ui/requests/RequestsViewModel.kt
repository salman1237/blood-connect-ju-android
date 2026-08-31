package com.deshlet.bloodconnectju.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.ProfileRepository
import com.deshlet.bloodconnectju.data.RealtimeService
import com.deshlet.bloodconnectju.data.RequestRepository
import com.deshlet.bloodconnectju.data.remote.dto.BloodRequestDto
import com.deshlet.bloodconnectju.data.remote.dto.RequestStatsDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestsUiState(
    val isLoading: Boolean = true,
    val requests: List<BloodRequestDto> = emptyList(),
    val stats: RequestStatsDto? = null,
    val bloodGroupFilter: String? = null,
    val hallFilter: String? = null,
    val halls: List<String> = emptyList(),
)

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val repository: RequestRepository,
    // Only for its meta() call (the hall list) — same cross-repository
    // reach OnboardingViewModel/ProfileViewModel already use meta() from,
    // rather than duplicating the endpoint on RequestRepository too.
    private val profileRepository: ProfileRepository,
    private val realtimeService: RealtimeService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestsUiState())
    val uiState: StateFlow<RequestsUiState> = _uiState

    // Held for the life of this ViewModel (this screen's own back-stack
    // entry) and closed in onCleared — same subscribe-on-create,
    // unsubscribe-on-leave shape RequestDetailViewModel/
    // NotificationsViewModel use for their own real-time channels.
    private val requestsFeedSubscription = realtimeService.subscribeToRequestsFeed(::refresh)

    init {
        viewModelScope.launch {
            val meta = profileRepository.meta()
            if (meta != null) _uiState.value = _uiState.value.copy(halls = meta.halls)
        }
        refresh()
    }

    override fun onCleared() {
        super.onCleared()
        requestsFeedSubscription.close()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val state = _uiState.value
            val stats = repository.stats()
            val requests = repository.listRequests(bloodGroup = state.bloodGroupFilter, hall = state.hallFilter)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                requests = requests ?: _uiState.value.requests,
                stats = stats ?: _uiState.value.stats,
            )
        }
    }

    fun setBloodGroupFilter(group: String?) {
        _uiState.value = _uiState.value.copy(bloodGroupFilter = group)
        refresh()
    }

    fun setHallFilter(hall: String?) {
        _uiState.value = _uiState.value.copy(hallFilter = hall)
        refresh()
    }
}
