package com.example.schetodo.ui.feature.schedule.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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


    override fun onReceive(context: Context, intent: Intent) {
        val todoBlockId = intent.getIntExtra(TODO_BLOCK_ID_EXTRA_KEY, -1)
        if (todoBlockId <= 0)
            return

        MainScope().launch {
            todoBlockNotificationScheduler.scheduleNextNotificationIfExists()

            val scheduleBlock =
                scheduleBlockRepository.getScheduleBlockByTodoBlockId(todoBlockId).first()
                    ?: return@launch

            val notificationService = NotificationService(context)
            notificationService.showScheduleNotification(
                scheduleBlock, scheduleBlock.todoBlock.todoBlockId
            )
        }
    }
}