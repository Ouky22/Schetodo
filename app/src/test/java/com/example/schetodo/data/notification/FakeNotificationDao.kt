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

    override fun getNotificationById(notificationId: Int): Flow<Notification?> {
        return flow {
            emit(notifications.firstOrNull { it.notificationId == notificationId })
        }
    }

    override suspend fun insertNotification(notification: Notification): Long {
        notifications.add(notification)
        return notification.notificationId.toLong()
    }

    override suspend fun insertNotifications(notifications: List<Notification>) {
        this.notifications.addAll(notifications)
    }

    override suspend fun deleteNotification(notification: Notification) {
        notifications.remove(notification)
    }

    override suspend fun deleteAllNotificationsOfTodoBlock(todoBlockId: Int) {
        notifications.removeIf {
            it.todoBlockId == todoBlockId
        }
    }
}