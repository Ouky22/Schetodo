package com.example.schetodo.data.notification

import com.example.schetodo.data.todo_block.TodoBlockDao
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
    private val todoBlockDao: TodoBlockDao
) : NotificationRepository {

    override suspend fun getNextNotification(): Notification? {
        val allNotifications = notificationDao.getAllNotifications().first()
        if (allNotifications.isEmpty())
            return null

        return allNotifications
            .filter { notification ->
                val todoBlock = todoBlockDao.getTodoBlockById(notification.todoBlockId).first()
                    ?: return@filter false

                val todoBlockNotMarkedForDeletion = !todoBlock.markedForDeletion
                val todoBlockNotFromTemplate = todoBlock.templateId == null
                todoBlockNotMarkedForDeletion && todoBlockNotFromTemplate
            }
            .filter { notification ->
                val currentDateTime = LocalDateTime.now().withSecond(0).withNano(0)
                !notification.dateTime.isBefore(currentDateTime)
            }.minByOrNull { notification ->
                notification.dateTime
            }
    }

    override fun getNotificationById(id: Int) = notificationDao.getNotificationById(id)

    override fun getNotificationsOfTodoBlock(todoBlockId: Int) =
        notificationDao.getNotificationsOfTodoBlock(todoBlockId)

    override suspend fun insertNotification(notification: Notification) =
        notificationDao.insertNotification(notification)

    override suspend fun deleteNotification(notification: Notification) =
        notificationDao.deleteNotification(notification)

    override suspend fun setNotificationsOfTodoBlock(
        todoBlockId: Int,
        notifications: List<Notification>
    ) {
        notificationDao.deleteAllNotificationsOfTodoBlock(todoBlockId)
        notifications.forEach { notification ->
            notificationDao.insertNotification(notification)
        }
    }
}