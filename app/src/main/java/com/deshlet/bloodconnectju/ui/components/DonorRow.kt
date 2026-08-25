package com.deshlet.bloodconnectju.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.deshlet.bloodconnectju.data.remote.dto.DonorSummaryDto
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground

/**
 * A donor as they appear in a list — matching donors (Phase 3) and the
 * donor directory (Phase 5) share this exact row rather than each building
 * their own; only whether the row itself is clickable (directory: yes, to
 * open the full profile; matching donors: no, WhatsApp is the only action)
 * differs between the two call sites.
 */
@Composable
fun DonorRow(donor: DonorSummaryDto, onClick: (() -> Unit)? = null) {
    val context = LocalContext.current

    Card(modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier) {
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
                Button(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, donor.whatsapp_url.toUri())) }) {
                    Text("WhatsApp")
                }
            }
        }
    }
}
