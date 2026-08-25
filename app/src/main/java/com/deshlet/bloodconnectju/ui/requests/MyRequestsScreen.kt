@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.requests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/** Every status the caller has ever posted — the API twin of web's requests/mine.blade.php. */
@Composable
fun MyRequestsScreen(
    onRequestClick: (Int) -> Unit,
    onCreateRequest: () -> Unit,
    onBack: () -> Unit,
    viewModel: MyRequestsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Same reasoning as RequestsScreen — refetch every time this screen
    // becomes visible again (e.g. after posting a request and navigating
    // back), not just the first time it's created.
    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My requests") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) }
                },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                uiState.isLoading && uiState.requests.isEmpty() -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                uiState.requests.isEmpty() -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("You haven't posted a request yet", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Requests you post will show up here so you can track their status.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Box(Modifier.size(16.dp))
                    Button(onClick = onCreateRequest) { Text("Post a request") }
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.requests, key = { it.id }) { request ->
                        RequestCard(request = request, onClick = { onRequestClick(request.id) })
                    }
                }
            }
        }
    }
}
