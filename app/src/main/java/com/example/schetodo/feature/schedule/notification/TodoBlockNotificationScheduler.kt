package com.example.schetodo.feature.schedule.notification

interface TodoBlockNotificationScheduler {
    suspend fun scheduleNextNotificationIfExists()
}