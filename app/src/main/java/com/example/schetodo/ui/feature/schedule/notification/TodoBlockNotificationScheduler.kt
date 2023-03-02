package com.example.schetodo.ui.feature.schedule.notification

interface TodoBlockNotificationScheduler {
    suspend fun scheduleNextNotificationIfExists()
}