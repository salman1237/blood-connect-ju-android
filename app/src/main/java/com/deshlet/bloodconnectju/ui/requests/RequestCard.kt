package com.deshlet.bloodconnectju.ui.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.deshlet.bloodconnectju.data.remote.dto.BloodRequestDto
import com.deshlet.bloodconnectju.ui.components.StatusPill
import com.deshlet.bloodconnectju.ui.components.UrgencyBadge
import com.deshlet.bloodconnectju.ui.components.VerifiedBadge
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground

/** Same fields/order as the web's partials/request-card.blade.php. */
@Composable
fun RequestCard(request: BloodRequestDto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(BcAccent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(request.blood_group, color = BcAccentForeground, style = MaterialTheme.typography.labelLarge)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${request.units_needed} unit${if (request.units_needed == 1) "" else "s"} needed",
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                Spacer(Modifier.size(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    UrgencyBadge(request.urgency)
                    // Mirrors web's request-card.blade.php order exactly:
                    // urgency, then verified (if applicable), then status.
                    if (request.is_verified) VerifiedBadge()
                    StatusPill(request.status)
                }
                Spacer(Modifier.size(6.dp))
                Text(
                    request.hospital_name + (request.location?.let { ", $it" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.size(2.dp))
                Text(
                    request.requester.name + (request.requester.hall?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
