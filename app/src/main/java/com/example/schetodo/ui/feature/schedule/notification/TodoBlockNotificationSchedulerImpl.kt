package com.example.schetodo.ui.feature.schedule.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.notification.NotificationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import javax.inject.Inject

const val TODO_BLOCK_ID_EXTRA_KEY = "todoBlockId"

class TodoBlockNotificationSchedulerImpl @Inject constructor(
    private val notificationRepository: NotificationRepository,
    @ApplicationContext private val context: Context
) : TodoBlockNotificationScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override suspend fun scheduleNextNotificationIfExists() {
        val nextNotification = notificationRepository.getNextNotification()

        if (nextNotification == null) {
            // todo deactivate reboot broadcast receiver
            cancelActiveAlarmForNotification()
            return
        }

        scheduleNotification(nextNotification)
    }

    private fun scheduleNotification(notification: Notification) {
        val triggerDateTimeMilliseconds =
            notification.dateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

        val notificationIntent = getNotificationIntent(notification)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerDateTimeMilliseconds,
            notificationIntent
        )
    }

    private fun cancelActiveAlarmForNotification() {
        alarmManager.cancel(getNotificationIntent())
    }

    private fun getNotificationIntent(notification: Notification? = null): PendingIntent {
        val intent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, NotificationBroadcastReceiver::class.java).apply {
                notification?.let { putExtra(TODO_BLOCK_ID_EXTRA_KEY, it.todoBlockId) }
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return intent
    }
}