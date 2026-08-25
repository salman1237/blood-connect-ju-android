@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.donations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.data.remote.dto.BadgeDto
import com.deshlet.bloodconnectju.data.remote.dto.DonationHistoryEntryDto
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground
import com.deshlet.bloodconnectju.ui.theme.BcPrimary

/** Confirmed donations + earned badges — the API twin of web's donations/index.blade.php. */
@Composable
fun DonationHistoryScreen(
    onBack: () -> Unit,
    viewModel: DonationHistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donation history") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (uiState.badges.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(uiState.badges, key = { it.slug }) { badge -> BadgePill(badge) }
                }
            }

            when {
                uiState.isLoading && uiState.donationHistory.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                uiState.donationHistory.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("No confirmed donations yet", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Once a request you helped with is mutually confirmed, it'll show up here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.donationHistory) { entry -> DonationRow(entry) }
                }
            }
        }
    }
}

@Composable
private fun BadgePill(badge: BadgeDto) {
    Text(
        badge.name,
        style = MaterialTheme.typography.labelSmall,
        color = BcAccentForeground,
        modifier = Modifier
            .background(BcAccent, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
private fun DonationRow(entry: DonationHistoryEntryDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                entry.hospital_name ?: "Off-platform donation",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                entry.confirmed_at.take(10),
                style = MaterialTheme.typography.bodySmall,
                color = BcPrimary,
            )
        }
    }
}
