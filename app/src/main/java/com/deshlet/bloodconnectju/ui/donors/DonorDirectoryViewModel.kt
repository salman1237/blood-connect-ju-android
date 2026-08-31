package com.deshlet.bloodconnectju.ui.donors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.DonorRepository
import com.deshlet.bloodconnectju.data.ProfileRepository
import com.deshlet.bloodconnectju.data.remote.dto.DonorSummaryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonorDirectoryUiState(
    val isLoading: Boolean = true,
    val donors: List<DonorSummaryDto> = emptyList(),
    val search: String = "",
    val bloodGroupFilter: String? = null,
    val hallFilter: String? = null,
    val halls: List<String> = emptyList(),
)

@HiltViewModel
class DonorDirectoryViewModel @Inject constructor(
    private val repository: DonorRepository,
    // Only for its meta() call (the hall list) — same cross-repository
    // reach OnboardingViewModel/ProfileViewModel already use meta() from.
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonorDirectoryUiState())
    val uiState: StateFlow<DonorDirectoryUiState> = _uiState

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            val meta = profileRepository.meta()
            if (meta != null) _uiState.value = _uiState.value.copy(halls = meta.halls)
        }
        refresh()
    }

    fun setSearch(value: String) {
        _uiState.value = _uiState.value.copy(search = value)
        // Debounced — typing a name shouldn't fire a request per keystroke.
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            refresh()
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

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val state = _uiState.value
            val donors = repository.list(
                search = state.search.ifBlank { null },
                bloodGroup = state.bloodGroupFilter,
                hall = state.hallFilter,
            )
            _uiState.value = _uiState.value.copy(isLoading = false, donors = donors ?: _uiState.value.donors)
        }
    }
}
