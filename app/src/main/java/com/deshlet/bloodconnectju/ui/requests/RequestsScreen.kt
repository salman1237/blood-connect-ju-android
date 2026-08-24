@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.requests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

@Composable
fun RequestsScreen(
    onRequestClick: (Int) -> Unit,
    onCreateRequest: () -> Unit,
    viewModel: RequestsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Blood requests") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRequest) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            uiState.stats?.let { stats ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatTile("Active", stats.active.toString(), Modifier.weight(1f))
                    StatTile("Critical", stats.critical.toString(), Modifier.weight(1f))
                    StatTile("Donors", stats.registered_donors.toString(), Modifier.weight(1f))
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                item {
                    FilterChip(
                        selected = uiState.bloodGroupFilter == null,
                        onClick = { viewModel.setBloodGroupFilter(null) },
                        label = { Text("All") },
                    )
                }
                items(bloodGroups) { group ->
                    FilterChip(
                        selected = uiState.bloodGroupFilter == group,
                        onClick = { viewModel.setBloodGroupFilter(group) },
                        label = { Text(group) },
                    )
                }
            }

            Spacer(Modifier.size(12.dp))

            when {
                uiState.isLoading && uiState.requests.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                uiState.requests.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { Text("No open requests right now.", color = MaterialTheme.colorScheme.onSurfaceVariant) }

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

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}
