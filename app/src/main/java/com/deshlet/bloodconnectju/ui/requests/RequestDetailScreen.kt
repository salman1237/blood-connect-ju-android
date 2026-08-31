@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.requests

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.data.remote.dto.ResponseSummaryDto
import com.deshlet.bloodconnectju.ui.components.StatusPill
import com.deshlet.bloodconnectju.ui.components.UrgencyBadge
import com.deshlet.bloodconnectju.ui.components.VerifiedBadge
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground

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
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BcAccent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Box(
                                    modifier = Modifier.size(52.dp).background(BcAccentForeground.copy(alpha = 0.12f), CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(request.blood_group, color = BcAccentForeground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${request.units_needed} unit${if (request.units_needed == 1) "" else "s"} needed",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BcAccentForeground,
                                    )
                                    Text(
                                        request.hospital_name + (request.location?.let { ", $it" } ?: ""),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BcAccentForeground.copy(alpha = 0.85f),
                                    )
                                }
                            }
                            Spacer(Modifier.size(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                UrgencyBadge(request.urgency)
                                if (request.is_verified) VerifiedBadge()
                                StatusPill(request.status)
                            }
                        }
                    }

                    request.patient_context?.let { context ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Patient context", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.size(6.dp))
                                Text(context, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            DetailRow(
                                "Posted by",
                                request.requester.name + (
                                    request.requester.hall?.let { " · $it" }
                                        ?: request.requester.department?.let { " · $it" }
                                        ?: ""
                                    ),
                            )
                            DetailRow("Contact", request.contact_method)
                        }
                    }

                    if (request.can_respond || request.can_fulfill) {
                        Column {
                            if (request.can_respond) {
                                Button(
                                    onClick = { viewModel.respond() },
                                    enabled = !uiState.isActing,
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                ) {
                                    Text("I can donate", fontWeight = FontWeight.SemiBold)
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
                    }

                    uiState.errorMessage?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // Always visible, same as web's requests/show.blade.php — not
                    // gated on can_respond/can_fulfill, and not just a one-time
                    // thing shown right after creating the request. Previously
                    // this screen had no way back into matching donors at all
                    // once you navigated away from the post-create screen.
                    TextButton(onClick = { onViewMatchingDonors(request.id) }) {
                        Text("See available matching donors", style = MaterialTheme.typography.labelMedium)
                    }

                    HorizontalDivider()

                    Text("Responses (${request.responses?.size ?: 0})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    if (request.responses.isNullOrEmpty()) {
                        Text(
                            "No one has responded yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(horizontal = 16.dp)) {
                                request.responses.forEachIndexed { index, response ->
                                    if (index > 0) HorizontalDivider()
                                    ResponseRow(response, uiState.isActing, viewModel)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.size(8.dp))
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
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // weight(1f) + ellipsis on the name — same fix as
            // DonorDetailScreen's DetailRow/DonationHistoryCard: a plain
            // SpaceBetween Row lets a long donor name run straight into the
            // status label with no gap instead of truncating gracefully.
            Text(
                response.donor.name ?: "Donor",
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
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
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Confirm this donor")
            }
        }
        if (response.can_confirm_donation) {
            Button(
                onClick = { viewModel.confirmDonation(response.id) },
                enabled = !isActing,
                modifier = Modifier.padding(top = 8.dp),
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
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
