package com.example.schetodo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.example.schetodo.ui.theme.md_theme_dark_primary
import com.example.schetodo.ui.util.NotificationService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SchetodoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NotificationService.SCHEDULE_NOTIFICATION_CHANNEL_ID,
            getString(R.string.schedule_block_notification),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        channel.description = getString(R.string.schedule_notification_description)
        channel.enableLights(true)
        channel.lightColor = md_theme_dark_primary.toArgb()

        with(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
            createNotificationChannel(channel)
        }
    }
}