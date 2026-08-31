package com.deshlet.bloodconnectju.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.deshlet.bloodconnectju.ui.theme.BcInfo
import com.deshlet.bloodconnectju.ui.theme.BcMuted
import com.deshlet.bloodconnectju.ui.theme.BcMutedForeground
import com.deshlet.bloodconnectju.ui.theme.BcPrimary
import com.deshlet.bloodconnectju.ui.theme.BcPrimaryForeground
import com.deshlet.bloodconnectju.ui.theme.BcSecondary
import com.deshlet.bloodconnectju.ui.theme.BcSecondaryForeground
import com.deshlet.bloodconnectju.ui.theme.BcSuccess
import com.deshlet.bloodconnectju.ui.theme.BcWarning
import com.deshlet.bloodconnectju.ui.theme.BcWarningForeground

// Same labels/colors as the web components (urgency-badge.blade.php,
// status-pill.blade.php) — kept as two small composables here rather than
// one parameterized one, since the two badge "shapes" (pulsing dot vs
// plain) genuinely differ, not just their color inputs.

@Composable
fun UrgencyBadge(urgency: String, modifier: Modifier = Modifier) {
    val label = when (urgency) {
        "critical" -> "Critical"
        "within_24h" -> "Within 24h"
        "planned" -> "Planned"
        else -> urgency.replaceFirstChar { it.uppercase() }
    }
    val (background, foreground) = when (urgency) {
        "critical" -> BcPrimary to BcPrimaryForeground
        "within_24h" -> BcWarning.copy(alpha = 0.2f) to BcWarningForeground
        else -> BcSecondary to BcSecondaryForeground
    }
    Row(
        modifier = modifier
            .background(background, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp),
        // Row defaults to Alignment.Top — fine when every child is the same
        // height, but the 6dp dot next to a full text line sat pinned to
        // the top of the row instead of level with the label.
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (urgency == "critical") {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(foreground, CircleShape),
            )
            Box(Modifier.size(4.dp))
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = foreground)
    }
}

@Composable
fun StatusPill(status: String, modifier: Modifier = Modifier) {
    val label = when (status) {
        "open" -> "Open"
        "donor_found" -> "Donor found"
        "fulfilled" -> "Fulfilled"
        "expired" -> "Expired"
        else -> status.replaceFirstChar { it.uppercase() }
    }
    val (background, foreground) = when (status) {
        "fulfilled" -> BcSuccess.copy(alpha = 0.15f) to BcSuccess
        "donor_found" -> BcInfo.copy(alpha = 0.15f) to BcInfo
        "expired" -> BcMuted to BcMutedForeground
        else -> Color.Transparent to BcMutedForeground
    }
    Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = foreground,
        modifier = modifier
            .background(background, RoundedCornerShape(50))
            .padding(PaddingValues(horizontal = 10.dp, vertical = 3.dp)),
    )
}
