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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.data.remote.dto.ResponseSummaryDto
import com.deshlet.bloodconnectju.ui.components.StatusPill
import com.deshlet.bloodconnectju.ui.components.UrgencyBadge

@Composable
fun RequestDetailScreen(
    requestId: Int,
    onBack: () -> Unit,
    onViewMatchingDonors: (Int) -> Unit,
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
                    DetailRow(
                        "Posted by",
                        request.requester.name + (
                            request.requester.hall?.let { " · $it" }
                                ?: request.requester.department?.let { " · $it" }
                                ?: ""
                            ),
                    )
                    DetailRow("Contact", request.contact_method)

                    if (request.can_respond || request.can_fulfill) {
                        Spacer(Modifier.size(20.dp))
                        if (request.can_respond) {
                            Button(
                                onClick = { viewModel.respond() },
                                enabled = !uiState.isActing,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("I can donate")
                            }
                        }
                        if (request.can_fulfill) {
                            val label = if (request.status == "open") "Mark donor found" else "Mark fulfilled"
                            OutlinedButton(
                                onClick = { viewModel.fulfill() },
                                enabled = !uiState.isActing,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            ) {
                                Text(label)
                            }
                        }
                    }

                    uiState.errorMessage?.let { message ->
                        Spacer(Modifier.size(8.dp))
                        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // Always visible, same as web's requests/show.blade.php — not
                    // gated on can_respond/can_fulfill, and not just a one-time
                    // thing shown right after creating the request. Previously
                    // this screen had no way back into matching donors at all
                    // once you navigated away from the post-create screen.
                    TextButton(onClick = { onViewMatchingDonors(request.id) }, modifier = Modifier.padding(top = 4.dp)) {
                        Text("See available matching donors", style = MaterialTheme.typography.labelMedium)
                    }

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
                            ResponseRow(response, uiState.isActing, viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResponseRow(
    response: ResponseSummaryDto,
    isActing: Boolean,
    viewModel: RequestDetailViewModel,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(response.donor.name ?: "Donor")
            Text(
                statusLabel(response),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (response.can_confirm) {
            Button(
                onClick = { viewModel.confirmResponse(response.id) },
                enabled = !isActing,
                modifier = Modifier.padding(top = 6.dp),
            ) {
                Text("Confirm this donor")
            }
        }
        if (response.can_confirm_donation) {
            Button(
                onClick = { viewModel.confirmDonation(response.id) },
                enabled = !isActing,
                modifier = Modifier.padding(top = 6.dp),
            ) {
                Text("Confirm the donation happened")
            }
        }
    }
}

private fun statusLabel(response: ResponseSummaryDto): String = when {
    response.is_mutually_confirmed -> "Donation confirmed"
    response.status == "confirmed" && (response.requester_confirmed || response.donor_confirmed) -> "Awaiting confirmation"
    response.status == "confirmed" -> "Confirmed donor"
    else -> "Responded"
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
