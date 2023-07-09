package com.example.schetodo.feature.schedule.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.schetodo.data.notification.NotificationRepository
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.ui.util.NotificationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var todoBlockNotificationScheduler: TodoBlockNotificationScheduler

    @Inject
    lateinit var scheduleBlockRepository: ScheduleBlockRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository


    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(NOTIFICATION_ID_EXTRA_KEY, -1)
        if (notificationId <= 0)
            return

        MainScope().launch {
            val notification =
                notificationRepository.getNotificationById(notificationId).first() ?: return@launch

            val scheduleBlock = scheduleBlockRepository.getScheduleBlockByTodoBlockId(
                notification.todoBlockId
            ).first() ?: return@launch

            val notificationService = NotificationService(context)
            notificationService.showScheduleNotification(
                scheduleBlock,
                scheduleBlock.todoBlock.todoBlockId
            )

            notificationRepository.deleteNotification(notification)
            todoBlockNotificationScheduler.scheduleNextNotificationIfExists()
        }
    }
}