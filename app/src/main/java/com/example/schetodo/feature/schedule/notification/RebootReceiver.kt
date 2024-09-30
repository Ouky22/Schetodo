package com.example.schetodo.feature.schedule.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RebootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.intent.action.BOOT_COMPLETED")
            return

        // when the app is started an instance of TodoBlockNotificationScheduler is created and
        // the next notification (is exists) is scheduled
    }
}