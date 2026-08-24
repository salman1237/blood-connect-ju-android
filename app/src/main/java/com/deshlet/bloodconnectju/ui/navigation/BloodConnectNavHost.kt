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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deshlet.bloodconnectju.ui.auth.AuthViewModel
import com.deshlet.bloodconnectju.ui.auth.LoginScreen
import com.deshlet.bloodconnectju.ui.auth.RegisterScreen
import com.deshlet.bloodconnectju.ui.home.HomeScreen
import com.deshlet.bloodconnectju.ui.onboarding.OnboardingScreen

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
}

@Composable
fun BloodConnectNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
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
            )
        }
    }
}
