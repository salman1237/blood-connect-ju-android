package com.deshlet.bloodconnectju.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.R
import com.deshlet.bloodconnectju.ui.components.selectedChipColors
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcMutedForeground

private val roleOptions = listOf("student" to "Student", "staff" to "Staff", "faculty" to "Faculty")
private val genderOptions = listOf("male" to "Male", "female" to "Female", "other" to "Other")

/** Same design language as the redesigned LoginScreen — logo header + a single grouped card, not bare fields on the page. */
@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    // Separate from onRegistered: a Google sign-in from this screen might
    // resolve to an *existing*, already-onboarded account (e.g. someone
    // who first signed up via web with Google), unlike a brand-new
    // email/password account, which never has a completed profile yet.
    onGoogleSignedIn: (hasCompletedOnboarding: Boolean) -> Unit,
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
    var googleError by rememberSaveable { mutableStateOf<String?>(null) }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(28.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(44.dp).background(BcAccent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(painter = painterResource(R.drawable.logo_mark), contentDescription = null, modifier = Modifier.size(26.dp))
                }
                Column {
                    Text("Create your account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Join Blood Connect JU as a donor", style = MaterialTheme.typography.bodySmall, color = BcMutedForeground)
                }
            }
            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    GoogleSignInButton(
                        enabled = !uiState.isLoading,
                        onResult = { result ->
                            when (result) {
                                is GoogleSignInResult.Success -> {
                                    googleError = null
                                    authViewModel.loginWithGoogle(result.idToken, onSuccess = onGoogleSignedIn)
                                }
                                is GoogleSignInResult.Failure -> googleError = result.message
                                GoogleSignInResult.Cancelled -> Unit
                            }
                        },
                    )
                    googleError?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text("or", style = MaterialTheme.typography.bodySmall, color = BcMutedForeground)
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    LabeledField("Name", name, { name = it }, uiState.fieldErrors["name"])
                    LabeledField("Email", email, { email = it }, uiState.fieldErrors["email"], keyboardType = KeyboardType.Email)
                    LabeledField("Password", password, { password = it }, uiState.fieldErrors["password"], isPassword = true)
                    LabeledField("Confirm password", passwordConfirmation, { passwordConfirmation = it }, null, isPassword = true)
                    LabeledField(
                        "Date of birth (YYYY-MM-DD)",
                        dateOfBirth,
                        { dateOfBirth = it },
                        uiState.fieldErrors["date_of_birth"],
                    )

                    Column {
                        Text("I am a", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.size(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            roleOptions.forEach { (value, label) ->
                                FilterChip(selected = role == value, onClick = { role = value }, label = { Text(label) }, colors = selectedChipColors())
                            }
                        }
                    }

                    Column {
                        Text("Gender", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.size(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            genderOptions.forEach { (value, label) ->
                                FilterChip(selected = gender == value, onClick = { gender = value }, label = { Text(label) }, colors = selectedChipColors())
                            }
                        }
                    }

                    uiState.errorMessage?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

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
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Create account", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onNavigateToLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Already have an account? Log in")
            }
            Spacer(Modifier.height(24.dp))
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
        shape = RoundedCornerShape(14.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        isError = !errors.isNullOrEmpty(),
        supportingText = errors?.firstOrNull()?.let { message -> { Text(message) } },
        modifier = Modifier.fillMaxWidth(),
    )
}
