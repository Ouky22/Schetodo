package com.example.schetodo.data.notification

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNextNotification(): Notification?
    fun getNotificationsOfTodoBlock(todoBlockId: Int): Flow<List<Notification>>
    suspend fun insertNotification(notification: Notification): Long
    suspend fun deleteNotification(notification: Notification)
    suspend fun setNotificationsOfTodoBlock(todoBlockId: Int, notifications: List<Notification>)
    fun getNotificationById(id: Int): Flow<Notification?>
    fun getAllNotifications(): Flow<List<Notification>>
}