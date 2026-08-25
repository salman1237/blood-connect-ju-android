package com.deshlet.bloodconnectju.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.R
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import com.deshlet.bloodconnectju.ui.auth.AuthViewModel
import com.deshlet.bloodconnectju.ui.components.UserAvatar
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground
import com.deshlet.bloodconnectju.ui.theme.BcMutedForeground
import com.deshlet.bloodconnectju.ui.theme.BcPrimary
import com.deshlet.bloodconnectju.ui.theme.BcPrimaryForeground
import com.deshlet.bloodconnectju.ui.theme.BcSuccess

/**
 * The dashboard behind the "Home" tab of the bottom bar — a real landing
 * page (branded header, profile summary, a primary call to action, quick
 * links to the sections that didn't earn a permanent tab) rather than the
 * bare stack of buttons this screen used to be before every other section
 * existed. Browsing (Requests), searching (Donors), and account management
 * (Profile) all moved to their own permanent tabs — see BottomNavItem — so
 * this screen's own job narrowed to "what does this specific person need
 * to see/do right now", not "list every screen in the app".
 */
@Composable
fun HomeScreen(
    onCreateRequest: () -> Unit,
    onViewMyRequests: () -> Unit,
    onViewDonationHistory: () -> Unit,
    onViewLeaderboard: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    var user by remember { mutableStateOf<UserDto?>(null) }
    var loading by remember { mutableStateOf(true) }

    // Re-fetches every time this tab becomes visible again, not just the
    // first time it's created — same reasoning as every other screen fixed
    // in the stale-on-return pass (see .claude-progress.md).
    LaunchedEffect(Unit) {
        user = authViewModel.fetchProfile()
        loading = false
    }

    if (loading && user == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        BrandHeader()
        Spacer(Modifier.height(20.dp))

        user?.let { GreetingCard(it) }
        Spacer(Modifier.height(20.dp))

        CreateRequestCta(onClick = onCreateRequest)
        Spacer(Modifier.height(24.dp))

        Text("Quick links", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickActionCard(
                icon = Icons.Filled.Inbox,
                label = "My requests",
                onClick = onViewMyRequests,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                icon = Icons.Filled.Favorite,
                label = "Donations",
                onClick = onViewDonationHistory,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                icon = Icons.Filled.EmojiEvents,
                label = "Leaderboard",
                onClick = onViewLeaderboard,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun BrandHeader() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Image(
            painter = painterResource(R.drawable.logo_mark),
            contentDescription = null,
            modifier = Modifier.height(34.dp),
        )
        Column {
            Text(
                "Blood Connect JU",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "Jahangirnagar University",
                style = MaterialTheme.typography.labelSmall,
                color = BcMutedForeground,
            )
        }
    }
}

@Composable
private fun GreetingCard(user: UserDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BcAccent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                UserAvatar(
                    name = user.name,
                    avatarUrl = user.avatar_url,
                    modifier = Modifier.size(52.dp),
                    backgroundColor = Color.White.copy(alpha = 0.55f),
                    contentColor = BcAccentForeground,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Welcome back,",
                        style = MaterialTheme.typography.bodySmall,
                        color = BcAccentForeground.copy(alpha = 0.75f),
                    )
                    Text(
                        user.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BcAccentForeground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            val bloodGroup = user.donor_profile?.blood_group
            val place = user.hall ?: user.department
            if (bloodGroup != null || place != null) {
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    bloodGroup?.let {
                        Surface(shape = RoundedCornerShape(50), color = BcPrimary) {
                            Text(
                                it,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = BcPrimaryForeground,
                            )
                        }
                    }
                    place?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = BcAccentForeground.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (user.donor_profile?.is_available == true) {
                        Spacer(Modifier.weight(1f))
                        Box(Modifier.size(7.dp).background(BcSuccess, CircleShape))
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateRequestCta(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BcPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = BcPrimaryForeground)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Post a blood request",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = BcPrimaryForeground,
                )
                Text(
                    "Let nearby compatible donors know",
                    style = MaterialTheme.typography.bodySmall,
                    color = BcPrimaryForeground.copy(alpha = 0.85f),
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BcAccent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = BcAccentForeground, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
