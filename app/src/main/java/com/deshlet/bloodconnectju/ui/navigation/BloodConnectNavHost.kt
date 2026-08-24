package com.deshlet.bloodconnectju.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
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

    when (isLoggedIn) {
        null -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        else -> NavHost(
            navController = navController,
            startDestination = if (isLoggedIn == true) Routes.HOME else Routes.LOGIN,
            modifier = modifier,
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoggedIn = { navController.navigate(Routes.HOME) { popUpTo(0) } },
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegistered = { navController.navigate(Routes.HOME) { popUpTo(0) } },
                    onNavigateToLogin = { navController.popBackStack() },
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onLoggedOut = { navController.navigate(Routes.LOGIN) { popUpTo(0) } },
                )
            }
        }
    }
}
