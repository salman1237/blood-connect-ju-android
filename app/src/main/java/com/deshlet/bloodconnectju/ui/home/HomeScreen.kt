package com.deshlet.bloodconnectju.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import com.deshlet.bloodconnectju.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

/**
 * Confirms the token round-trips correctly by calling the authenticated
 * /api/v1/user endpoint, and is the jumping-off point to the rest of the
 * app. No bottom-nav/drawer shell yet — that's its own later phase once
 * there's more than one section to switch between (requests, donor
 * directory, leaderboard, profile).
 */
@Composable
fun HomeScreen(
    onLoggedOut: () -> Unit,
    onViewRequests: () -> Unit,
    onViewDonors: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    var user by remember { mutableStateOf<UserDto?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        user = authViewModel.fetchProfile()
        loading = false
    }

    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Welcome, ${user?.name ?: "donor"}!", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.size(8.dp))
                Text(user?.email ?: "", style = MaterialTheme.typography.bodyMedium)
                val currentUser = user
                currentUser?.donor_profile?.blood_group?.let { bloodGroup ->
                    Spacer(Modifier.size(4.dp))
                    Text(
                        "$bloodGroup · ${currentUser.department ?: currentUser.hall ?: "Campus"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(Modifier.size(24.dp))
                Button(onClick = onViewRequests) {
                    Text("Blood requests")
                }
                Spacer(Modifier.size(12.dp))
                Button(onClick = onViewDonors) {
                    Text("Donors")
                }
                Spacer(Modifier.size(12.dp))
                Button(onClick = {
                    scope.launch {
                        authViewModel.logout()
                        onLoggedOut()
                    }
                }) {
                    Text("Log out")
                }
            }
        }
    }
}
