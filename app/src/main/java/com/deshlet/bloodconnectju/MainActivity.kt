package com.deshlet.bloodconnectju

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.deshlet.bloodconnectju.notifications.PushIntentExtras
import com.deshlet.bloodconnectju.ui.navigation.AppBottomBar
import com.deshlet.bloodconnectju.ui.navigation.BloodConnectNavHost
import com.deshlet.bloodconnectju.ui.navigation.BottomNavItem
import com.deshlet.bloodconnectju.ui.theme.BloodConnectJUTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op either way — pushes just won't show if denied */ }

    // Backed by mutableStateOf so a tap on a notification while the app is
    // already running (onNewIntent, singleTop launch mode) flows straight
    // into Compose without needing to recreate the Activity.
    private var pendingDeepLinkRequestId by mutableStateOf<Int?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        pendingDeepLinkRequestId = extractRequestId(intent)

        setContent {
            BloodConnectJUTheme {
                // imePadding() here, once, at the root — rather than on each
                // screen individually — is what actually makes the keyboard
                // push content up instead of covering it. enableEdgeToEdge()
                // above turns off the system's own window-resize-on-IME
                // behavior (that's what windowSoftInputMode="adjustResize" in
                // the manifest used to handle before edge-to-edge), so
                // without this every screen's bottom-most field — Danger
                // Zone's password confirm, Create Request's contact number,
                // Register's whole form — sat directly under the keyboard
                // with no way to scroll it into view.
                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryAsState()
                val showBottomBar = BottomNavItem.entries.any { it.route == currentRoute?.destination?.route }

                Scaffold(
                    modifier = Modifier.fillMaxSize().imePadding(),
                    // Only the four tab roots (Home/Requests/Donors/Profile)
                    // get the bottom bar — everything else (auth, onboarding,
                    // create request, any detail screen) is a focused task
                    // that gets the full screen instead of permanent tab
                    // chrome sitting underneath it.
                    bottomBar = { if (showBottomBar) AppBottomBar(navController) },
                ) { innerPadding ->
                    BloodConnectNavHost(
                        navController = navController,
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        pendingDeepLinkRequestId = pendingDeepLinkRequestId,
                        onDeepLinkConsumed = { pendingDeepLinkRequestId = null },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingDeepLinkRequestId = extractRequestId(intent)
    }

    private fun extractRequestId(intent: Intent?): Int? =
        intent?.getIntExtra(PushIntentExtras.REQUEST_ID, -1)?.takeIf { it > 0 }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
