package com.example.schetodo.data.notification

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeNotificationDao : NotificationDao {

    private val notifications = mutableListOf<Notification>()

    override fun getAllNotifications(): Flow<List<Notification>> {
        return flow {
            emit(notifications)
        }
    }

    override fun getNotificationsOfTodoBlock(todoBlockId: Int): Flow<List<Notification>> {
        return flow {
            emit(notifications.filter { it.todoBlockId == todoBlockId })
        }
    }

    override suspend fun insertNotification(notification: Notification): Long {
        notifications.add(notification)
        return notification.notificationId.toLong()
    }

    override suspend fun deleteNotification(notification: Notification) {
        notifications.remove(notification)
    }
}