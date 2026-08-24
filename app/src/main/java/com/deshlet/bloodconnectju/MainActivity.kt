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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.deshlet.bloodconnectju.notifications.PushIntentExtras
import com.deshlet.bloodconnectju.ui.navigation.BloodConnectNavHost
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BloodConnectNavHost(
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
