package com.deshlet.bloodconnectju.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.RequestRepository
import com.deshlet.bloodconnectju.data.remote.dto.DonorSummaryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchingDonorsUiState(
    val isLoading: Boolean = true,
    val donors: List<DonorSummaryDto> = emptyList(),
)

@HiltViewModel
class MatchingDonorsViewModel @Inject constructor(
    private val repository: RequestRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchingDonorsUiState())
    val uiState: StateFlow<MatchingDonorsUiState> = _uiState

    fun load(requestId: Int) {
        viewModelScope.launch {
            _uiState.value = MatchingDonorsUiState(isLoading = true)
            val donors = repository.matchingDonors(requestId)
            _uiState.value = MatchingDonorsUiState(isLoading = false, donors = donors ?: emptyList())
        }
    }
}
