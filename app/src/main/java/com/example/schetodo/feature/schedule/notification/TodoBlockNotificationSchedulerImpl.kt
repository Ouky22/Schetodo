package com.example.schetodo.feature.schedule.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.notification.NotificationRepository
import com.example.schetodo.data.todo_block.TodoBlockRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

const val NOTIFICATION_ID_EXTRA_KEY = "notificationIdExtra"

class TodoBlockNotificationSchedulerImpl @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val todoBlockRepository: TodoBlockRepository,
    @ApplicationContext private val context: Context
) : TodoBlockNotificationScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private var previousScheduleNotification: Notification? = null

    override fun startObservationOfNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                notificationRepository.getAllNotifications().collectLatest {
                    scheduleNextNotificationIfExists()
                }
            }
            launch {
                todoBlockRepository.getAllTodoBlocks().collectLatest {
                    scheduleNextNotificationIfExists()
                }
            }
        }
    }

    override suspend fun scheduleNextNotificationIfExists() {
        val nextNotification = notificationRepository.getNextNotification()

        if (nextNotification == null) {
            cancelActiveAlarmForNotification()
            deactivateRebootBroadcastReceiver()
            return
        }

        if (nextNotification != previousScheduleNotification) {
            scheduleNotification(nextNotification)
            activateRebootBroadcastReceiver()
        }

        previousScheduleNotification = nextNotification
    }

    private fun scheduleNotification(notification: Notification) {
        val triggerDateTimeMilliseconds =
            notification.dateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

        val notificationIntent = getNotificationIntent(notification)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms())
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerDateTimeMilliseconds, null),
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
                notification?.let { putExtra(NOTIFICATION_ID_EXTRA_KEY, it.notificationId) }
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return intent
    }

    private fun activateRebootBroadcastReceiver() {
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context.applicationContext, RebootReceiver::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun deactivateRebootBroadcastReceiver() {
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context.applicationContext, RebootReceiver::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}