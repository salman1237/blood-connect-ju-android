package com.deshlet.bloodconnectju.ui.donations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.DonationsRepository
import com.deshlet.bloodconnectju.data.remote.dto.BadgeDto
import com.deshlet.bloodconnectju.data.remote.dto.DonationHistoryEntryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationHistoryUiState(
    val isLoading: Boolean = true,
    val donationHistory: List<DonationHistoryEntryDto> = emptyList(),
    val badges: List<BadgeDto> = emptyList(),
)

@HiltViewModel
class DonationHistoryViewModel @Inject constructor(
    private val repository: DonationsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationHistoryUiState())
    val uiState: StateFlow<DonationHistoryUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val donations = repository.get()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                donationHistory = donations?.donation_history ?: _uiState.value.donationHistory,
                badges = donations?.badges ?: _uiState.value.badges,
            )
        }
    }
}
