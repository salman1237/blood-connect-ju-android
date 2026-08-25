package com.deshlet.bloodconnectju.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.R
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcMutedForeground

/**
 * The front door of the app — first thing anyone sees before ever logging
 * in, so it carries the brand in a way most other screens don't need to:
 * the real logo mark, not just a name in text, and the same
 * card-on-tinted-background language HomeScreen established rather than
 * bare fields floating on a plain white page.
 */
@Composable
fun LoginScreen(
    onLoggedIn: (hasCompletedOnboarding: Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by authViewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(BcAccent),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_mark),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "Blood Connect JU",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Welcome back — sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = BcMutedForeground,
            )
            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = uiState.fieldErrors.containsKey("email"),
                        supportingText = uiState.fieldErrors["email"]?.firstOrNull()?.let { message -> { Text(message) } },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = uiState.fieldErrors.containsKey("password"),
                        supportingText = uiState.fieldErrors["password"]?.firstOrNull()?.let { message -> { Text(message) } },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    uiState.errorMessage?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Button(
                        onClick = { authViewModel.login(email.trim(), password, onSuccess = onLoggedIn) },
                        enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Log in", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onNavigateToRegister, modifier = Modifier.fillMaxWidth()) {
                Text("New here? Create an account")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
