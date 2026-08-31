@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.requests

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.ui.components.selectedChipColors
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground

private val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

@Composable
fun RequestsScreen(
    onRequestClick: (Int) -> Unit,
    onCreateRequest: () -> Unit,
    viewModel: RequestsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // ViewModel.init{} only fires once per instance, and the instance
    // survives while this destination sits lower in the back stack (e.g.
    // behind Create Request or a request's detail page) — without this,
    // coming back here after posting a request or acting on one showed
    // stale data until the app was fully restarted. Re-running on every
    // recomposition of this composable (which happens each time it becomes
    // the visible destination again, not just on first creation) is what
    // makes "go back and see the update" actually work.
    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Blood requests") }) },
        floatingActionButton = {
            // FloatingActionButton defaults to primaryContainer/onPrimaryContainer
            // (BcAccent — a pale pink in this theme, see Theme.kt), which reads as
            // washed-out next to every other primary-action surface in the app
            // (the Home CTA card, Save/Post buttons, ...), all of which use the
            // vivid BcPrimary red. Matching that explicitly here instead.
            FloatingActionButton(
                onClick = onCreateRequest,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Post a blood request")
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            uiState.stats?.let { stats ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatTile("Active", stats.active.toString(), Icons.Filled.Bloodtype, Modifier.weight(1f))
                    StatTile("Critical", stats.critical.toString(), Icons.Filled.PriorityHigh, Modifier.weight(1f))
                    StatTile("Donors", stats.registered_donors.toString(), Icons.Filled.Groups, Modifier.weight(1f))
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
                        colors = selectedChipColors(),
                    )
                }
                items(bloodGroups) { group ->
                    FilterChip(
                        selected = uiState.bloodGroupFilter == group,
                        onClick = { viewModel.setBloodGroupFilter(group) },
                        label = { Text(group) },
                        colors = selectedChipColors(),
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
private fun StatTile(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier.size(28.dp).background(BcAccent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = BcAccentForeground, modifier = Modifier.size(15.dp))
            }
            Spacer(Modifier.size(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
