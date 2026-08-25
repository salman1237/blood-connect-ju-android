package com.deshlet.bloodconnectju.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.RequestRepository
import com.deshlet.bloodconnectju.data.remote.dto.BloodRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyRequestsUiState(
    val isLoading: Boolean = true,
    val requests: List<BloodRequestDto> = emptyList(),
)

@HiltViewModel
class MyRequestsViewModel @Inject constructor(
    private val repository: RequestRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyRequestsUiState())
    val uiState: StateFlow<MyRequestsUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val requests = repository.listMine()
            _uiState.value = _uiState.value.copy(isLoading = false, requests = requests ?: _uiState.value.requests)
        }
    }
}
