package com.example.schetodo.data.notification

import com.example.schetodo.data.todo_block.TodoBlockDao
import com.example.schetodo.di.CoroutineScopeModule.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
    private val todoBlockDao: TodoBlockDao,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope
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

    override fun getAllNotifications(): Flow<List<Notification>> =
        notificationDao.getAllNotifications()

    override fun getNotificationById(id: Int) = notificationDao.getNotificationById(id)

    override fun getNotificationsOfTodoBlock(todoBlockId: Int) =
        notificationDao.getNotificationsOfTodoBlock(todoBlockId)

    override suspend fun insertNotification(notification: Notification) =
        withContext(applicationCoroutineScope.coroutineContext) {
            notificationDao.insertNotification(notification)
        }

    override suspend fun insertNotifications(notifications: List<Notification>) =
        withContext(applicationCoroutineScope.coroutineContext) {
            notificationDao.insertNotifications(notifications)
        }

    override suspend fun deleteNotification(notification: Notification) =
        withContext(applicationCoroutineScope.coroutineContext) {
            notificationDao.deleteNotification(notification)
        }

    override suspend fun updateNotificationsOfTodoBlock(
        todoBlockId: Int,
        notifications: List<Notification>
    ) {
        applicationCoroutineScope.launch {
            notificationDao.deleteAllNotificationsOfTodoBlock(todoBlockId)

            if (notifications.isNotEmpty())
                notificationDao.insertNotifications(
                    notifications.map { it.copy(todoBlockId = todoBlockId) }
                )
        }
    }
}