package com.deshlet.bloodconnectju.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
)

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val repository: RequestRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestsUiState())
    val uiState: StateFlow<RequestsUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val filter = _uiState.value.bloodGroupFilter
            val stats = repository.stats()
            val requests = repository.listRequests(bloodGroup = filter)
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
}
