package com.deshlet.bloodconnectju.ui.auth

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.ui.components.selectedChipColors

private val roleOptions = listOf("student" to "Student", "staff" to "Staff", "faculty" to "Faculty")
private val genderOptions = listOf("male" to "Male", "female" to "Female", "other" to "Other")

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by authViewModel.uiState.collectAsState()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordConfirmation by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf("student") }
    var gender by rememberSaveable { mutableStateOf("male") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text("Create your account", style = MaterialTheme.typography.headlineMedium)
        Text("Join Blood Connect JU as a donor", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.size(20.dp))

        LabeledField("Name", name, { name = it }, uiState.fieldErrors["name"])
        Spacer(Modifier.size(12.dp))
        LabeledField("Email", email, { email = it }, uiState.fieldErrors["email"], keyboardType = KeyboardType.Email)
        Spacer(Modifier.size(12.dp))
        LabeledField("Password", password, { password = it }, uiState.fieldErrors["password"], isPassword = true)
        Spacer(Modifier.size(12.dp))
        LabeledField(
            "Confirm password",
            passwordConfirmation,
            { passwordConfirmation = it },
            null,
            isPassword = true,
        )
        Spacer(Modifier.size(12.dp))
        LabeledField(
            "Date of birth (YYYY-MM-DD)",
            dateOfBirth,
            { dateOfBirth = it },
            uiState.fieldErrors["date_of_birth"],
        )

        Spacer(Modifier.size(16.dp))
        Text("I am a", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.size(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            roleOptions.forEach { (value, label) ->
                FilterChip(selected = role == value, onClick = { role = value }, label = { Text(label) }, colors = selectedChipColors())
            }
        }

        Spacer(Modifier.size(16.dp))
        Text("Gender", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.size(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            genderOptions.forEach { (value, label) ->
                FilterChip(selected = gender == value, onClick = { gender = value }, label = { Text(label) }, colors = selectedChipColors())
            }
        }

        uiState.errorMessage?.let { message ->
            Spacer(Modifier.size(12.dp))
            Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.size(20.dp))
        Button(
            onClick = {
                authViewModel.register(
                    name = name.trim(),
                    email = email.trim(),
                    password = password,
                    passwordConfirmation = passwordConfirmation,
                    role = role,
                    gender = gender,
                    dateOfBirth = dateOfBirth.trim(),
                    onSuccess = onRegistered,
                )
            },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Create account")
            }
        }
        Spacer(Modifier.size(12.dp))
        TextButton(onClick = onNavigateToLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account? Log in")
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    errors: List<String>?,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        isError = !errors.isNullOrEmpty(),
        supportingText = errors?.firstOrNull()?.let { message -> { Text(message) } },
        modifier = Modifier.fillMaxWidth(),
    )
}
