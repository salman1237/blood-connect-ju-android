package com.deshlet.bloodconnectju.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground

/**
 * Only rendered by MainActivity while the current destination is one of
 * BottomNavItem's four routes — everything else (auth, onboarding, create
 * request, detail screens) is a focused task, not a tab, so it gets the full
 * screen instead of permanent tab chrome underneath it.
 */
@Composable
fun AppBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        BottomNavItem.entries.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            // Standard bottom-nav pattern: each tab keeps its
                            // own back stack/scroll position (restoreState),
                            // switching tabs never piles up duplicate copies
                            // of the same destination (launchSingleTop), and
                            // "back" from any tab returns to Home rather than
                            // walking through every tab that was ever visited.
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        if (selected) item.filledIcon else item.outlinedIcon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BcAccentForeground,
                    indicatorColor = BcAccent,
                ),
            )
        }
    }
}
