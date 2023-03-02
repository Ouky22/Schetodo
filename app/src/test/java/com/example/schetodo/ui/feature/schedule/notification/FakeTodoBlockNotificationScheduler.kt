package com.example.schetodo.ui.feature.schedule.notification

import com.example.schetodo.data.notification.FakeNotificationRepository
import com.example.schetodo.data.notification.Notification
import javax.inject.Inject

class FakeTodoBlockNotificationScheduler @Inject constructor(
    private val notificationRepository: FakeNotificationRepository
) : TodoBlockNotificationScheduler {

    var currentlyScheduledNotification: Notification? = null

    override suspend fun scheduleNextNotificationIfExists() {
        currentlyScheduledNotification = notificationRepository.getNextNotification()
    }
}