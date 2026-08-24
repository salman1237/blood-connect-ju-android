@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.requests

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.data.remote.dto.DonorSummaryDto
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground

@Composable
fun MatchingDonorsScreen(
    requestId: Int,
    onBack: () -> Unit,
    viewModel: MatchingDonorsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(requestId) { viewModel.load(requestId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Matching donors") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) }
                },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.donors.isEmpty() -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("No available donors right now", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Everyone compatible has already been notified.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(modifier = Modifier.padding(top = 12.dp)) {
                        Button(onClick = onBack) { Text("Back to requests") }
                    }
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.donors, key = { it.id }) { donor ->
                        DonorRow(donor) {
                            donor.whatsapp_url?.let { url ->
                                context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DonorRow(donor: DonorSummaryDto, onWhatsApp: () -> Unit) {
    Card {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(BcAccent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(donor.blood_group, color = BcAccentForeground, style = MaterialTheme.typography.labelLarge)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(donor.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    donor.department ?: donor.hall ?: "Campus",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (donor.whatsapp_url != null) {
                Button(onClick = onWhatsApp) { Text("WhatsApp") }
            }
        }
    }
}
