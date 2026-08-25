@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.donors

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.R
import com.deshlet.bloodconnectju.data.remote.dto.BadgeDto
import com.deshlet.bloodconnectju.data.remote.dto.DonationHistoryEntryDto
import com.deshlet.bloodconnectju.data.remote.dto.DonorDetailDto
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground
import com.deshlet.bloodconnectju.ui.theme.BcMuted
import com.deshlet.bloodconnectju.ui.theme.BcMutedForeground
import com.deshlet.bloodconnectju.ui.theme.BcSuccess
import com.deshlet.bloodconnectju.ui.theme.BcWarning
import com.deshlet.bloodconnectju.ui.theme.BcWarningForeground

/** WhatsApp's own brand green — a one-off, same as the web app's bg-[#25D366], not a design-system token. */
private val WhatsAppGreen = Color(0xFF25D366)

private val roleLabels = mapOf(
    "student" to "Student",
    "staff" to "Staff",
    "faculty" to "Teacher",
    "verifier" to "Verifier",
    "admin" to "Admin",
)

@Composable
fun DonorDetailScreen(
    donorId: Int,
    onBack: () -> Unit,
    viewModel: DonorDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(donorId) { viewModel.load(donorId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donor profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) }
                },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val donor = uiState.donor
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                donor == null -> Text(
                    "Couldn't load this profile.",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                else -> DonorDetailContent(donor)
            }
        }
    }
}

@Composable
private fun DonorDetailContent(donor: DonorDetailDto) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { HeaderCard(donor) }
        item { DetailsCard(donor) }
        if (donor.badges.isNotEmpty()) {
            item { BadgesCard(donor.badges) }
        }
        item { DonationHistoryCard(donor.donation_history) }
    }
}

@Composable
private fun HeaderCard(donor: DonorDetailDto) {
    val context = LocalContext.current

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(56.dp).background(BcAccent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(donor.donor_profile.blood_group, color = BcAccentForeground, style = MaterialTheme.typography.titleMedium)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(donor.name, style = MaterialTheme.typography.titleLarge)
                    Text(
                        donor.hall ?: donor.department ?: "Campus",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Pill(
                    text = if (donor.donor_profile.is_available) "Available" else "Unavailable",
                    background = if (donor.donor_profile.is_available) BcSuccess.copy(alpha = 0.15f) else BcMuted,
                    foreground = if (donor.donor_profile.is_available) BcSuccess else BcMutedForeground,
                )
                Pill(
                    text = if (donor.donor_profile.is_eligible) "Eligible to donate" else "Not yet eligible",
                    background = if (donor.donor_profile.is_eligible) BcSuccess.copy(alpha = 0.15f) else BcWarning.copy(alpha = 0.2f),
                    foreground = if (donor.donor_profile.is_eligible) BcSuccess else BcWarningForeground,
                )
                Text(
                    "Trust score: ${donor.donor_profile.trust_score}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (donor.whatsapp_url != null) {
                Button(
                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, donor.whatsapp_url.toUri())) },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_whatsapp),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.size(8.dp))
                    Text("Message on WhatsApp")
                }
            }
        }
    }
}

@Composable
private fun Pill(text: String, background: androidx.compose.ui.graphics.Color, foreground: androidx.compose.ui.graphics.Color) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = foreground,
        modifier = Modifier
            .background(background, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp),
    )
}

@Composable
private fun DetailsCard(donor: DonorDetailDto) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Details", style = MaterialTheme.typography.titleSmall)
            Box(Modifier.size(8.dp))

            DetailRow("Gender", donor.gender?.replaceFirstChar { it.uppercase() } ?: "—")
            DetailRow("Role", roleLabels[donor.role] ?: donor.role.replaceFirstChar { it.uppercase() })
            DetailRow("Age", donor.age?.let { "$it years" } ?: "—")
            if (donor.role == "student") {
                DetailRow("Hall", donor.hall ?: "—")
                DetailRow("Batch", donor.batch ?: "—")
            }
            DetailRow("Department", donor.department ?: "—")
            DetailRow("Phone", donor.phone ?: "—")
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    // Fixed-width label + a weighted, right-aligned value column — plain
    // SpaceBetween left the label touching a wrapped value's first line
    // with no gap (e.g. "Department" against a two-line department name)
    // since SpaceBetween only reserves space when there's width left over.
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            modifier = Modifier.weight(1.4f),
        )
    }
}

@Composable
private fun BadgesCard(badges: List<BadgeDto>) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Badges", style = MaterialTheme.typography.titleSmall)
            Box(Modifier.size(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(badges, key = { it.slug }) { badge ->
                    Pill(text = badge.name, background = BcAccent, foreground = BcAccentForeground)
                }
            }
        }
    }
}

@Composable
private fun DonationHistoryCard(history: List<DonationHistoryEntryDto>) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Donation history (${history.size})", style = MaterialTheme.typography.titleSmall)
            Box(Modifier.size(8.dp))
            if (history.isEmpty()) {
                Text(
                    "No confirmed donations yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                history.forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(entry.hospital_name ?: "Off-platform donation", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            entry.confirmed_at.take(10),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
