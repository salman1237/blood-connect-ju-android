package com.deshlet.bloodconnectju.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.deshlet.bloodconnectju.ui.auth.AuthViewModel
import com.deshlet.bloodconnectju.ui.auth.LoginScreen
import com.deshlet.bloodconnectju.ui.auth.RegisterScreen
import com.deshlet.bloodconnectju.ui.donations.DonationHistoryScreen
import com.deshlet.bloodconnectju.ui.donors.DonorDetailScreen
import com.deshlet.bloodconnectju.ui.donors.DonorDirectoryScreen
import com.deshlet.bloodconnectju.ui.home.HomeScreen
import com.deshlet.bloodconnectju.ui.leaderboard.LeaderboardScreen
import com.deshlet.bloodconnectju.ui.onboarding.OnboardingScreen
import com.deshlet.bloodconnectju.ui.profile.ProfileScreen
import com.deshlet.bloodconnectju.ui.requests.CreateRequestScreen
import com.deshlet.bloodconnectju.ui.requests.MatchingDonorsScreen
import com.deshlet.bloodconnectju.ui.requests.MyRequestsScreen
import com.deshlet.bloodconnectju.ui.requests.RequestDetailScreen
import com.deshlet.bloodconnectju.ui.requests.RequestsScreen

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val REQUESTS = "requests"
    const val MY_REQUESTS = "my-requests"
    const val CREATE_REQUEST = "requests/create"
    const val REQUEST_DETAIL = "requests/{id}"
    const val MATCHING_DONORS = "requests/{id}/donors"
    const val DONORS = "donors"
    const val DONOR_DETAIL = "donors/{id}"
    const val LEADERBOARD = "leaderboard"
    const val PROFILE = "profile"
    const val DONATIONS = "donations"

    fun requestDetail(id: Int) = "requests/$id"
    fun matchingDonors(id: Int) = "requests/$id/donors"
    fun donorDetail(id: Int) = "donors/$id"
}

@Composable
fun BloodConnectNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    pendingDeepLinkRequestId: Int? = null,
    onDeepLinkConsumed: () -> Unit = {},
) {
    // Held once at this level purely to read isLoggedIn and pick a start
    // destination — the underlying token state is a Hilt-@Singleton-scoped
    // repository, so this is consistent with whatever instance each screen
    // below separately resolves via its own hiltViewModel() call.
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // A stored token alone doesn't say whether onboarding is done — that
    // needs one /api/v1/user call, only on cold start (a fresh
    // login/register already gets this in its own response, see
    // LoginScreen/RegisterScreen below, so this path is skipped then).
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isLoggedIn) {
        startDestination = when (isLoggedIn) {
            null -> null
            false -> Routes.LOGIN
            true -> {
                val user = authViewModel.fetchProfile()
                if (user?.has_completed_onboarding == true) Routes.HOME else Routes.ONBOARDING
            }
        }
    }

    val resolvedStart = startDestination
    if (resolvedStart == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = resolvedStart,
        modifier = modifier,
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoggedIn = { hasCompletedOnboarding ->
                    val target = if (hasCompletedOnboarding) Routes.HOME else Routes.ONBOARDING
                    navController.navigate(target) { popUpTo(0) }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                // A brand-new account never has a donor profile yet, so this
                // always goes to onboarding — no ambiguity to check.
                onRegistered = { navController.navigate(Routes.ONBOARDING) { popUpTo(0) } },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onCompleted = { navController.navigate(Routes.HOME) { popUpTo(0) } },
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onLoggedOut = { navController.navigate(Routes.LOGIN) { popUpTo(0) } },
                onViewRequests = { navController.navigate(Routes.REQUESTS) },
                onViewMyRequests = { navController.navigate(Routes.MY_REQUESTS) },
                onViewDonationHistory = { navController.navigate(Routes.DONATIONS) },
                onViewDonors = { navController.navigate(Routes.DONORS) },
                onViewLeaderboard = { navController.navigate(Routes.LEADERBOARD) },
                onViewProfile = { navController.navigate(Routes.PROFILE) },
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLoggedOut = { navController.navigate(Routes.LOGIN) { popUpTo(0) } },
                onAccountDeleted = { navController.navigate(Routes.LOGIN) { popUpTo(0) } },
            )
        }
        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.DONORS) {
            DonorDirectoryScreen(
                onDonorClick = { id -> navController.navigate(Routes.donorDetail(id)) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.DONOR_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            DonorDetailScreen(donorId = id, onBack = { navController.popBackStack() })
        }
        composable(Routes.REQUESTS) {
            RequestsScreen(
                onRequestClick = { id -> navController.navigate(Routes.requestDetail(id)) },
                onCreateRequest = { navController.navigate(Routes.CREATE_REQUEST) },
            )
        }
        composable(Routes.MY_REQUESTS) {
            MyRequestsScreen(
                onRequestClick = { id -> navController.navigate(Routes.requestDetail(id)) },
                onCreateRequest = { navController.navigate(Routes.CREATE_REQUEST) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.DONATIONS) {
            DonationHistoryScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.CREATE_REQUEST) {
            CreateRequestScreen(
                onCreated = { id ->
                    // Pop CreateRequest off before pushing MatchingDonors,
                    // rather than popUpTo(Routes.REQUESTS) — this screen is
                    // reachable from both the feed and My Requests, and
                    // popUpTo a route that isn't actually in the current
                    // stack (e.g. arriving via My Requests, which never
                    // pushed a REQUESTS entry) silently does nothing,
                    // leaving CreateRequest sitting in the stack under
                    // MatchingDonors — so "back" from there would show the
                    // create form again instead of returning to wherever the
                    // user actually started. Popping unconditionally leaves
                    // MatchingDonorsScreen's own plain popBackStack() onBack
                    // correct no matter which screen led here.
                    navController.popBackStack()
                    navController.navigate(Routes.matchingDonors(id))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.REQUEST_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            RequestDetailScreen(
                requestId = id,
                onBack = { navController.popBackStack() },
                onViewMatchingDonors = { requestId -> navController.navigate(Routes.matchingDonors(requestId)) },
            )
        }
        composable(
            route = Routes.MATCHING_DONORS,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            MatchingDonorsScreen(
                requestId = id,
                // A plain pop rather than a hardcoded "reset to the feed" —
                // this screen is now reachable both right after creating a
                // request (where the back stack already resolves to the feed
                // underneath it) and from a request's own detail page (where
                // it should return to that detail page, not jump away from
                // it), so it needs to go back to wherever it was actually
                // opened from rather than assuming one specific origin.
                onBack = { navController.popBackStack() },
            )
        }
    }

    // A notification tap can arrive before onboarding/home is even resolved
    // (cold start) — only act on it once the user has actually landed
    // somewhere inside the app (HOME); onboarding is deliberately not a
    // valid target, since none of the notifications that carry a request id
    // can fire for an account that hasn't finished onboarding yet.
    LaunchedEffect(pendingDeepLinkRequestId, resolvedStart) {
        if (pendingDeepLinkRequestId != null && resolvedStart == Routes.HOME) {
            navController.navigate(Routes.requestDetail(pendingDeepLinkRequestId))
            onDeepLinkConsumed()
        }
    }
}
