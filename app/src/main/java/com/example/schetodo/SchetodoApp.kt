package com.example.schetodo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.example.schetodo.feature.schedule.notification.TodoBlockNotificationScheduler
import com.example.schetodo.ui.theme.primaryDark
import com.example.schetodo.ui.util.NotificationService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SchetodoApp : Application() {

    @Inject
    lateinit var todoBlockNotificationScheduler: TodoBlockNotificationScheduler

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        todoBlockNotificationScheduler.startObservationOfNotifications()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NotificationService.SCHEDULE_NOTIFICATION_CHANNEL_ID,
            getString(R.string.schedule_block_notification),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        channel.description = getString(R.string.schedule_notification_description)
        channel.enableLights(true)
        channel.lightColor = primaryDark.toArgb()

        with(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
            createNotificationChannel(channel)
        }
    }
}