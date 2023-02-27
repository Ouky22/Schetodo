package com.example.schetodo.data.notification

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeNotificationRepository : NotificationRepository {
    private val notifications = mutableListOf<Notification>()

    override suspend fun getNextNotification(): Notification? {
        return notifications.minByOrNull { it.dateTime }
    }

    override fun getNotificationsOfTodoBlock(todoBlockId: Int): Flow<List<Notification>> {
        return flow {
            emit(notifications.filter { it.todoBlockId == todoBlockId })
        }
    }

    override suspend fun insertNotification(notification: Notification): Long {
        notifications += notification
        return notification.notificationId.toLong()
    }

    override suspend fun deleteNotification(notification: Notification) {
        notifications -= notification
    }

    override suspend fun setNotificationsOfTodoBlock(
        todoBlockId: Int,
        notifications: List<Notification>
    ) {
        this.notifications.removeIf { it.todoBlockId == todoBlockId }
        this.notifications += notifications
    }
}