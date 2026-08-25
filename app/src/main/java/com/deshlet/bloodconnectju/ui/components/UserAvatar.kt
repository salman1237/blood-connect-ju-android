package com.deshlet.bloodconnectju.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground

/**
 * Mirrors web's x-user-avatar component: a real photo when the account has
 * one (avatar_url — currently only ever set via whatever the web app's own
 * upload/Google-sign-in flow populated it with; Android has no avatar
 * upload of its own yet), falling back to up-to-two initials in a colored
 * circle otherwise. Like its web counterpart, this bakes in neither size
 * nor shape-adjacent layout — callers always pass `Modifier.size(...)`
 * (clipped to a circle here either way, so no need to pass a shape too).
 *
 * Defaults to BcAccent/BcAccentForeground rather than web's literal
 * bg-secondary — this app's BcSecondary is nearly indistinguishable from
 * Card's own surfaceContainer background (the same root cause as the
 * selected-FilterChip and bottom-nav-label invisibility bugs), so the
 * initials circle would render with no visible contrast against the
 * Profile card it sits on. BcAccent is this codebase's already-established
 * "needs real contrast" color, used the same way by DonorRow's own circle.
 */
@Composable
fun UserAvatar(
    name: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BcAccent,
    contentColor: Color = BcAccentForeground,
) {
    if (avatarUrl != null) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = name,
            modifier = modifier.clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    } else {
        val initials = name
            .trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.take(1) }
            .uppercase()
            .ifBlank { "?" }

        Box(
            modifier = modifier.background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(initials, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}
