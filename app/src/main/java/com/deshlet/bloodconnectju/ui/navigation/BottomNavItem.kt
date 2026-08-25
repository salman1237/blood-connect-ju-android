package com.deshlet.bloodconnectju.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The app's four top-level destinations, reachable at all times from the
 * bottom bar — same tier as web's primary sidebar nav (Home/Donors/Profile),
 * minus Alerts (no in-app notification center yet) and Ranks (demoted to a
 * Home quick-action instead of a permanent tab, along with My Requests and
 * Donation History — six destinations is one too many for a bottom bar to
 * hold without crowding, so the four people reach for constantly get a
 * permanent tab and the rest live one tap away on the Home dashboard).
 */
enum class BottomNavItem(
    val route: String,
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
) {
    HOME(Routes.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    // Filled.Water and Outlined.WaterDrop are two genuinely different glyphs
    // (waves vs. a drop) despite the similar names — that mismatch was the
    // "tab icon changes shape on tap" bug; both variants need to be the
    // same icon family (WaterDrop) so selecting the tab only fills it in.
    REQUESTS(Routes.REQUESTS, "Requests", Icons.Filled.WaterDrop, Icons.Outlined.WaterDrop),
    DONORS(Routes.DONORS, "Donors", Icons.Filled.Search, Icons.Outlined.Search),
    PROFILE(Routes.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person),
}
