package com.deshlet.bloodconnectju.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deshlet.bloodconnectju.data.RequestRepository
import com.deshlet.bloodconnectju.data.RequestResult
import com.deshlet.bloodconnectju.data.remote.dto.CreateRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateRequestUiState(
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, List<String>> = emptyMap(),
)

@HiltViewModel
class CreateRequestViewModel @Inject constructor(
    private val repository: RequestRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRequestUiState())
    val uiState: StateFlow<CreateRequestUiState> = _uiState

    fun submit(
        bloodGroup: String,
        unitsNeeded: Int,
        hospitalName: String,
        location: String?,
        urgency: String,
        patientContext: String?,
        contactMethod: String,
        onSuccess: (requestId: Int) -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.value = CreateRequestUiState(isSubmitting = true)
            val result = repository.create(
                CreateRequestBody(
                    blood_group = bloodGroup,
                    units_needed = unitsNeeded,
                    hospital_name = hospitalName,
                    location = location?.ifBlank { null },
                    urgency = urgency,
                    patient_context = patientContext?.ifBlank { null },
                    contact_method = contactMethod,
                ),
            )
            when (result) {
                is RequestResult.Success -> {
                    _uiState.value = CreateRequestUiState()
                    onSuccess(result.request.id)
                }

                is RequestResult.Failure -> {
                    _uiState.value = CreateRequestUiState(
                        errorMessage = result.message,
                        fieldErrors = result.fieldErrors,
                    )
                }
            }
        }
    }
}
