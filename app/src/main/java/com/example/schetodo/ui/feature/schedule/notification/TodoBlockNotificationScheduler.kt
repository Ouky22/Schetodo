package com.example.schetodo.ui.feature.schedule.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.notification.NotificationRepository
import java.time.ZoneId
import javax.inject.Inject

const val TODO_BLOCK_ID_EXTRA_KEY = "todoBlockId"

class TodoBlockNotificationScheduler @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val context: Context
) {
    suspend fun scheduleNextNotificationIfExists() {
        val nextNotification = notificationRepository.getNextNotification()

        if (nextNotification == null) {
            // todo cancel active notification intents and deactivate reboot broadcast receiver
            return
        }

        scheduleNotification(nextNotification)
    }

    private fun scheduleNotification(notification: Notification) {
        val notificationIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, NotificationBroadcastReceiver::class.java)
                .putExtra(TODO_BLOCK_ID_EXTRA_KEY, notification.todoBlockId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerDateTimeMilliseconds =
            notification.dateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, triggerDateTimeMilliseconds, notificationIntent
        )
    }
}