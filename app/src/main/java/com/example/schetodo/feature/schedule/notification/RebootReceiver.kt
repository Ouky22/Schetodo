package com.example.schetodo.feature.schedule.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RebootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationScheduler: TodoBlockNotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.intent.action.BOOT_COMPLETED")
            return

        MainScope().launch {
            notificationScheduler.scheduleNextNotificationIfExists()
        }
    }
}