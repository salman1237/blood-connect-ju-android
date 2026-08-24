package com.deshlet.bloodconnectju

import android.app.Application
import com.deshlet.bloodconnectju.notifications.ensureBloodRequestsNotificationChannel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BloodConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Must exist before the first push can possibly arrive — see the
        // doc comment on this function for why it can't just be created
        // lazily inside the messaging service.
        ensureBloodRequestsNotificationChannel(this)
    }
}
