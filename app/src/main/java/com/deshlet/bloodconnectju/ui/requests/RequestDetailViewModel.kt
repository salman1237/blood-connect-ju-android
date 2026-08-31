package com.deshlet.bloodconnectju.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.RealtimeService
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
    private val realtimeService: RealtimeService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestDetailUiState())
    val uiState: StateFlow<RequestDetailUiState> = _uiState

    private var currentId: Int? = null

    // load(id) can in principle run more than once for the same ViewModel
    // instance (this composable's own back-stack entry survives
    // recomposition) — close any previous subscription before opening the
    // new one so a stale request's channel doesn't stay subscribed.
    private var activitySubscription: AutoCloseable? = null

    fun load(id: Int) {
        currentId = id
        activitySubscription?.close()
        activitySubscription = realtimeService.subscribeToRequestActivity(id) { refetch(id) }

        viewModelScope.launch {
            _uiState.value = RequestDetailUiState(isLoading = true)
            val request = repository.get(id)
            _uiState.value = RequestDetailUiState(isLoading = false, request = request)
        }
    }

    /** Same fetch as load(), minus the loading-state flash and the subscription setup — used for live-update refetches while this screen stays open. */
    private fun refetch(id: Int) {
        if (id != currentId) return
        viewModelScope.launch {
            val request = repository.get(id)
            if (request != null) _uiState.value = _uiState.value.copy(request = request)
        }
    }

    override fun onCleared() {
        super.onCleared()
        activitySubscription?.close()
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
