package com.example.schetodo.ui.feature.schedule.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationBroadcastReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var todoBlockNotificationScheduler: TodoBlockNotificationScheduler
    
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "notification", Toast.LENGTH_LONG).show()

        CoroutineScope(IO).launch {
            todoBlockNotificationScheduler.scheduleNextNotificationIfExists()
        }
    }
}