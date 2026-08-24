@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.requests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val bloodGroupOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
private val urgencyOptions = listOf("critical" to "Critical", "within_24h" to "Within 24h", "planned" to "Planned")

@Composable
fun CreateRequestScreen(
    onCreated: (requestId: Int) -> Unit,
    onBack: () -> Unit,
    viewModel: CreateRequestViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var bloodGroup by rememberSaveable { mutableStateOf("") }
    var unitsNeeded by rememberSaveable { mutableStateOf("1") }
    var hospitalName by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var urgency by rememberSaveable { mutableStateOf("critical") }
    var patientContext by rememberSaveable { mutableStateOf("") }
    var contactMethod by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post a request") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Text("Blood group", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.size(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                bloodGroupOptions.chunked(4).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { group ->
                            FilterChip(
                                selected = bloodGroup == group,
                                onClick = { bloodGroup = group },
                                label = { Text(group) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.size(16.dp))
            OutlinedTextField(
                value = unitsNeeded,
                onValueChange = { unitsNeeded = it.filter(Char::isDigit) },
                label = { Text("Units needed") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.fieldErrors.containsKey("units_needed"),
                supportingText = uiState.fieldErrors["units_needed"]?.firstOrNull()?.let { m -> { Text(m) } },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.size(12.dp))
            OutlinedTextField(
                value = hospitalName,
                onValueChange = { hospitalName = it },
                label = { Text("Hospital name") },
                singleLine = true,
                isError = uiState.fieldErrors.containsKey("hospital_name"),
                supportingText = uiState.fieldErrors["hospital_name"]?.firstOrNull()?.let { m -> { Text(m) } },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.size(12.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.size(16.dp))
            Text("Urgency", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.size(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                urgencyOptions.forEach { (value, label) ->
                    FilterChip(selected = urgency == value, onClick = { urgency = value }, label = { Text(label) })
                }
            }

            Spacer(Modifier.size(16.dp))
            OutlinedTextField(
                value = patientContext,
                onValueChange = { patientContext = it },
                label = { Text("Patient context (optional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.size(12.dp))
            OutlinedTextField(
                value = contactMethod,
                onValueChange = { contactMethod = it },
                label = { Text("Contact number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = uiState.fieldErrors.containsKey("contact_method"),
                supportingText = uiState.fieldErrors["contact_method"]?.firstOrNull()?.let { m -> { Text(m) } },
                modifier = Modifier.fillMaxWidth(),
            )

            uiState.errorMessage?.let { message ->
                Spacer(Modifier.size(12.dp))
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.size(24.dp))
            val canSubmit = bloodGroup.isNotBlank() &&
                unitsNeeded.toIntOrNull()?.let { it in 1..20 } == true &&
                hospitalName.isNotBlank() &&
                contactMethod.isNotBlank()
            Button(
                onClick = {
                    viewModel.submit(
                        bloodGroup = bloodGroup,
                        unitsNeeded = unitsNeeded.toIntOrNull() ?: 1,
                        hospitalName = hospitalName,
                        location = location,
                        urgency = urgency,
                        patientContext = patientContext,
                        contactMethod = contactMethod,
                        onSuccess = onCreated,
                    )
                },
                enabled = canSubmit && !uiState.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Post request")
                }
            }
        }
    }
}
