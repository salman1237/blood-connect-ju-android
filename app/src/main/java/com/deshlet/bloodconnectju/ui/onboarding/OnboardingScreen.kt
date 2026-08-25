package com.deshlet.bloodconnectju.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.ui.components.GroupedDropdownField
import com.deshlet.bloodconnectju.ui.components.SimpleDropdownField
import com.deshlet.bloodconnectju.ui.components.selectedChipColors

private val roleOptions = listOf("student" to "Student", "staff" to "Staff", "faculty" to "Teacher")
private val genderOptions = listOf("male" to "Male", "female" to "Female", "other" to "Other")
private val bloodGroupOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
private const val TOTAL_STEPS = 4

/** Same 4-step flow as the web onboarding wizard (resources/views/onboarding.blade.php). */
@Composable
fun OnboardingScreen(
    onCompleted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var step by rememberSaveable { mutableIntStateOf(1) }
    var bloodGroup by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf("student") }
    var gender by rememberSaveable { mutableStateOf("male") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    var department by rememberSaveable { mutableStateOf("") }
    var hall by rememberSaveable { mutableStateOf("") }
    var batch by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var hasWhatsapp by rememberSaveable { mutableStateOf(true) }
    var whatsappNumber by rememberSaveable { mutableStateOf("") }
    var isAvailable by rememberSaveable { mutableStateOf(true) }
    var lastDonationDate by rememberSaveable { mutableStateOf("") }
    var prefilled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading && !prefilled) {
            role = uiState.prefillRole
            gender = uiState.prefillGender
            dateOfBirth = uiState.prefillDateOfBirth
            prefilled = true
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val meta = uiState.meta
    val titles = listOf("Your blood group", "About you", "Where you are on campus", "Your availability")
    val subtitles = listOf(
        "Donors are matched to requests by compatible blood group.",
        "Helps us route requests to the right people.",
        "We use this to alert you about nearby requests first.",
        "You can change this any time from your profile.",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            "Step $step of $TOTAL_STEPS",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.size(4.dp))
        Text(titles[step - 1], style = MaterialTheme.typography.headlineSmall)
        Text(
            subtitles[step - 1],
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.size(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            repeat(TOTAL_STEPS) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (i < step) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        ),
                )
            }
        }

        Spacer(Modifier.size(24.dp))

        when (step) {
            1 -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    bloodGroupOptions.chunked(4).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            rowItems.forEach { group ->
                                FilterChip(
                                    selected = bloodGroup == group,
                                    onClick = { bloodGroup = group },
                                    label = { Text(group) },
                                    colors = selectedChipColors(),
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
                uiState.fieldErrors["blood_group"]?.firstOrNull()?.let { message ->
                    Spacer(Modifier.size(6.dp))
                    Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            2 -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("I am a", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.size(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            roleOptions.forEach { (value, label) ->
                                FilterChip(selected = role == value, onClick = { role = value }, label = { Text(label) }, colors = selectedChipColors())
                            }
                        }
                    }
                    Column {
                        Text("Gender", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.size(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            genderOptions.forEach { (value, label) ->
                                FilterChip(selected = gender == value, onClick = { gender = value }, label = { Text(label) }, colors = selectedChipColors())
                            }
                        }
                    }
                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = { Text("Date of birth (YYYY-MM-DD)") },
                        singleLine = true,
                        isError = uiState.fieldErrors.containsKey("date_of_birth"),
                        supportingText = uiState.fieldErrors["date_of_birth"]?.firstOrNull()?.let { m -> { Text(m) } },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            3 -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (role == "student") {
                        SimpleDropdownField(
                            label = "Hall",
                            selected = hall,
                            options = meta?.halls ?: emptyList(),
                            onSelect = { hall = it },
                            errorText = uiState.fieldErrors["hall"]?.firstOrNull(),
                        )
                        SimpleDropdownField(
                            label = "Batch",
                            selected = batch,
                            options = meta?.batches ?: emptyList(),
                            onSelect = { batch = it },
                            errorText = uiState.fieldErrors["batch"]?.firstOrNull(),
                        )
                    }
                    GroupedDropdownField(
                        label = "Department",
                        selected = department,
                        groups = meta?.departments ?: emptyMap(),
                        onSelect = { department = it },
                        errorText = uiState.fieldErrors["department"]?.firstOrNull(),
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone (optional)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                    ) {
                        Text("This number has WhatsApp", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = hasWhatsapp, onCheckedChange = { hasWhatsapp = it })
                    }
                    if (!hasWhatsapp) {
                        OutlinedTextField(
                            value = whatsappNumber,
                            onValueChange = { whatsappNumber = it },
                            label = { Text("WhatsApp number (optional)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                    ) {
                        Column {
                            Text("Available to donate", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Show me in donor search results",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
                    }
                    OutlinedTextField(
                        value = lastDonationDate,
                        onValueChange = { lastDonationDate = it },
                        label = { Text("Last donation date (optional, YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        uiState.errorMessage?.let { message ->
            Spacer(Modifier.size(12.dp))
            Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.size(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (step > 1) {
                OutlinedButton(onClick = { step-- }, modifier = Modifier.weight(1f)) { Text("Back") }
            }
            if (step < TOTAL_STEPS) {
                val canContinue = when (step) {
                    1 -> bloodGroup.isNotBlank()
                    2 -> dateOfBirth.isNotBlank()
                    else -> true
                }
                Button(onClick = { step++ }, enabled = canContinue, modifier = Modifier.weight(1f)) { Text("Continue") }
            } else {
                Button(
                    onClick = {
                        viewModel.submit(
                            bloodGroup = bloodGroup,
                            role = role,
                            gender = gender,
                            dateOfBirth = dateOfBirth,
                            department = department,
                            hall = hall.ifBlank { null },
                            batch = batch.ifBlank { null },
                            phone = phone.ifBlank { null },
                            hasWhatsapp = hasWhatsapp,
                            whatsappNumber = whatsappNumber.ifBlank { null },
                            isAvailable = isAvailable,
                            lastDonationDate = lastDonationDate.ifBlank { null },
                            onSuccess = onCompleted,
                        )
                    },
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier.weight(1f),
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Finish setup")
                    }
                }
            }
        }
    }
}
