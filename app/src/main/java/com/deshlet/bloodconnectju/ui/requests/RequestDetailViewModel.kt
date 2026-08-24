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

data class RequestDetailUiState(
    val isLoading: Boolean = true,
    val request: BloodRequestDto? = null,
)

@HiltViewModel
class RequestDetailViewModel @Inject constructor(
    private val repository: RequestRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestDetailUiState())
    val uiState: StateFlow<RequestDetailUiState> = _uiState

    fun load(id: Int) {
        viewModelScope.launch {
            _uiState.value = RequestDetailUiState(isLoading = true)
            val request = repository.get(id)
            _uiState.value = RequestDetailUiState(isLoading = false, request = request)
        }
    }
}
