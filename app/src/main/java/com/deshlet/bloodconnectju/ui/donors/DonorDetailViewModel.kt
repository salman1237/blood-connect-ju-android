package com.deshlet.bloodconnectju.ui.donors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.DonorRepository
import com.deshlet.bloodconnectju.data.remote.dto.DonorDetailDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonorDetailUiState(
    val isLoading: Boolean = true,
    val donor: DonorDetailDto? = null,
)

@HiltViewModel
class DonorDetailViewModel @Inject constructor(
    private val repository: DonorRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonorDetailUiState())
    val uiState: StateFlow<DonorDetailUiState> = _uiState

    fun load(donorId: Int) {
        viewModelScope.launch {
            _uiState.value = DonorDetailUiState(isLoading = true)
            val donor = repository.get(donorId)
            _uiState.value = DonorDetailUiState(isLoading = false, donor = donor)
        }
    }
}
