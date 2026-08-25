@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.leaderboard

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.data.remote.dto.LeaderboardEntryDto
import com.deshlet.bloodconnectju.ui.theme.BcPrimary
import com.deshlet.bloodconnectju.ui.theme.BcPrimaryForeground
import com.deshlet.bloodconnectju.ui.theme.BcSecondary
import com.deshlet.bloodconnectju.ui.theme.BcMutedForeground

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Same reasoning as RequestsScreen — refetch every time this screen
    // becomes visible again, not just the first time it's created.
    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) }
                },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                uiState.isLoading && uiState.rankings.isEmpty() -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                uiState.rankings.isEmpty() -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("No confirmed donations yet", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Once donations start getting mutually confirmed, rankings will show up here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(uiState.rankings) { index, entry -> RankingRow(index + 1, entry) }
                }
            }
        }
    }
}

@Composable
private fun RankingRow(rank: Int, entry: LeaderboardEntryDto) {
    val topThree = rank <= 3
    Card {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(if (topThree) BcPrimary else BcSecondary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    rank.toString(),
                    color = if (topThree) BcPrimaryForeground else BcMutedForeground,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Text(
                entry.group_name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                if (entry.donations == 1) "1 donation" else "${entry.donations} donations",
                style = MaterialTheme.typography.labelLarge,
                color = BcPrimary,
            )
        }
    }
}
