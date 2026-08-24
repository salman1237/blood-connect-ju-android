@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.requests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.deshlet.bloodconnectju.ui.components.StatusPill
import com.deshlet.bloodconnectju.ui.components.UrgencyBadge

@Composable
fun RequestDetailScreen(
    requestId: Int,
    onBack: () -> Unit,
    viewModel: RequestDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(requestId) { viewModel.load(requestId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request details") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) }
                },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val request = uiState.request
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                request == null -> Text(
                    "Couldn't load this request.",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UrgencyBadge(request.urgency)
                        StatusPill(request.status)
                    }
                    Spacer(Modifier.size(12.dp))
                    Text(
                        "${request.blood_group} · ${request.units_needed} unit${if (request.units_needed == 1) "" else "s"} needed",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        request.hospital_name + (request.location?.let { ", $it" } ?: ""),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    request.patient_context?.let { context ->
                        Spacer(Modifier.size(16.dp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(context, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(Modifier.size(20.dp))
                    DetailRow("Posted by", request.requester.name + (request.requester.hall?.let { " · $it" } ?: request.requester.department?.let { " · $it" } ?: ""))
                    DetailRow("Contact", request.contact_method)

                    Spacer(Modifier.size(20.dp))
                    HorizontalDivider()
                    Spacer(Modifier.size(16.dp))
                    Text("Responses (${request.responses?.size ?: 0})", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.size(8.dp))
                    if (request.responses.isNullOrEmpty()) {
                        Text(
                            "No one has responded yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        request.responses.forEach { response ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(response.donor.name ?: "Donor")
                                Text(
                                    if (response.is_mutually_confirmed) "Confirmed" else response.status,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
