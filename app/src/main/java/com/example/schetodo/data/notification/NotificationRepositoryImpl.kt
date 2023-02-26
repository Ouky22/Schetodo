package com.example.schetodo.data.notification

import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override suspend fun getNextNotification(): Notification? {
        val allNotifications = notificationDao.getAllNotifications().first()
        if (allNotifications.isEmpty())
            return null

        return allNotifications
            .filter { notification ->
                val currentDateTime = LocalDateTime.now(ZoneOffset.UTC)
                notification.dateTime.isAfter(currentDateTime)
            }.minByOrNull { notification ->
                notification.dateTime
            }
    }

    override fun getNotificationsOfTodoBlock(todoBlockId: Int) =
        notificationDao.getNotificationsOfTodoBlock(todoBlockId)

    override suspend fun insertNotification(notification: Notification) =
        notificationDao.insertNotification(notification)

    override suspend fun deleteNotification(notification: Notification) =
        notificationDao.deleteNotification(notification)
}