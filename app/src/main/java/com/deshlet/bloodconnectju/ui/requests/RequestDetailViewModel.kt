package com.deshlet.bloodconnectju.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.RequestRepository
import com.deshlet.bloodconnectju.data.RequestResult
import com.deshlet.bloodconnectju.data.remote.dto.BloodRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestDetailUiState(
    val isLoading: Boolean = true,
    val request: BloodRequestDto? = null,
    val isActing: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class RequestDetailViewModel @Inject constructor(
    private val repository: RequestRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestDetailUiState())
    val uiState: StateFlow<RequestDetailUiState> = _uiState

    private var currentId: Int? = null

    fun load(id: Int) {
        currentId = id
        viewModelScope.launch {
            _uiState.value = RequestDetailUiState(isLoading = true)
            val request = repository.get(id)
            _uiState.value = RequestDetailUiState(isLoading = false, request = request)
        }
    }

    fun respond() = runAction { repository.respond(it) }

    fun fulfill() = runAction { repository.fulfill(it) }

    fun confirmResponse(responseId: Int) = runAction { repository.confirmResponse(it, responseId) }

    fun confirmDonation(responseId: Int) = runAction { repository.confirmDonation(it, responseId) }

    private fun runAction(call: suspend (requestId: Int) -> RequestResult) {
        val id = currentId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isActing = true, errorMessage = null)
            when (val result = call(id)) {
                is RequestResult.Success -> {
                    _uiState.value = _uiState.value.copy(isActing = false, request = result.request)
                }

                is RequestResult.Failure -> {
                    _uiState.value = _uiState.value.copy(isActing = false, errorMessage = result.message)
                }
            }
        }
    }
}
