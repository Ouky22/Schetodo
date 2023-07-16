package com.example.schetodo.data.notification

import com.example.schetodo.data.todo_block.FakeTodoBlockDao
import com.example.schetodo.data.todo_block.TodoBlock
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
internal class NotificationRepositoryImplTest {

    private val fakeNotificationDao = FakeNotificationDao()
    private val fakeTodoBlockDao = FakeTodoBlockDao()
    private val notificationRepository = NotificationRepositoryImpl(
        fakeNotificationDao,
        fakeTodoBlockDao,
        CoroutineScope(SupervisorJob())
    )


    @Test
    fun next_notification_is_not_notification_of_todo_block_from_schedule_template() = runTest {
        val currentDate = LocalDate.now()
        val nextDate = currentDate.plusDays(1)
        val startTime = LocalTime.now()
        val endTime = startTime.plusHours(1)
        val todoBlockFromTemplate = TodoBlock(1, "", currentDate, startTime, endTime, 1)
        val todoBlockNotFromTemplate = TodoBlock(2, "", nextDate, startTime, endTime, null)
        val notificationForTodoBlockFromTemplate = Notification(
            1,
            LocalDateTime.of(currentDate, startTime),
            todoBlockFromTemplate.todoBlockId
        )
        val notificationForTodoBlockNotFromTemplate = Notification(
            2,
            LocalDateTime.of(nextDate, startTime),
            todoBlockNotFromTemplate.todoBlockId
        )
        fakeTodoBlockDao.insertTodoBlock(todoBlockFromTemplate)
        fakeTodoBlockDao.insertTodoBlock(todoBlockNotFromTemplate)
        fakeNotificationDao.insertNotification(notificationForTodoBlockFromTemplate)
        fakeNotificationDao.insertNotification(notificationForTodoBlockNotFromTemplate)

        assertThat(notificationRepository.getNextNotification()).isEqualTo(
            notificationForTodoBlockNotFromTemplate
        )
    }

    @Test
    fun next_notification_is_not_notification_of_todo_block_marked_for_deletion() = runTest {
        val currentDate = LocalDate.now()
        val nextDate = currentDate.plusDays(1)
        val startTime = LocalTime.now()
        val endTime = startTime.plusHours(1)
        val todoBlockMarkedForDeletion =
            TodoBlock(1, "", currentDate, startTime, endTime, null, true)
        val todoBlockNotMarkedForDeletion =
            TodoBlock(2, "", nextDate, startTime, endTime, null)
        val notificationForTodoBlockMarkedForDeletion = Notification(
            1,
            LocalDateTime.of(currentDate, startTime),
            todoBlockMarkedForDeletion.todoBlockId
        )
        val notificationForTodoBlockNotMarkedForDeletion = Notification(
            2,
            LocalDateTime.of(nextDate, startTime),
            todoBlockNotMarkedForDeletion.todoBlockId
        )
        fakeTodoBlockDao.insertTodoBlock(todoBlockMarkedForDeletion)
        fakeTodoBlockDao.insertTodoBlock(todoBlockNotMarkedForDeletion)
        fakeNotificationDao.insertNotification(notificationForTodoBlockMarkedForDeletion)
        fakeNotificationDao.insertNotification(notificationForTodoBlockNotMarkedForDeletion)

        assertThat(notificationRepository.getNextNotification()).isEqualTo(
            notificationForTodoBlockNotMarkedForDeletion
        )
    }

    @Test
    fun test_updating_notifications_of_todo_block() = runTest {
        val currentDateTime = LocalDateTime.now(ZoneId.of("UTC"))
        val todoBlockId = 1
        val notification1 = Notification(1, currentDateTime, todoBlockId)
        val notification2 = Notification(2, currentDateTime.plusHours(1), todoBlockId)
        fakeNotificationDao.insertNotification(notification1)
        fakeNotificationDao.insertNotification(notification2)

        val newNotifications = listOf(
            Notification(3, currentDateTime.plusHours(2), todoBlockId),
            notification1
        )
        notificationRepository.updateNotificationsOfTodoBlock(todoBlockId, newNotifications)

        val allNotificationsOfTodoBlock =
            notificationRepository.getNotificationsOfTodoBlock(todoBlockId).first()
        assertThat(allNotificationsOfTodoBlock).containsExactlyElementsIn(newNotifications)
    }

    @Test
    fun when_there_is_next_notification_then_return_it() = runTest {
        val todoBlock = TodoBlock(1, null, null, LocalTime.now(), LocalTime.now(), null, false)
        val currentDateTime = LocalDateTime.now(ZoneId.of("UTC"))
        val expiredNotification = Notification(1, currentDateTime.minusHours(1), 1)
        val nextNotification = Notification(2, currentDateTime.plusHours(2), 1)
        val notification = Notification(3, currentDateTime.plusDays(1).plusHours(1), 1)

        fakeNotificationDao.insertNotification(expiredNotification)
        fakeNotificationDao.insertNotification(nextNotification)
        fakeNotificationDao.insertNotification(notification)
        fakeTodoBlockDao.insertTodoBlock(todoBlock)

        assertThat(notificationRepository.getNextNotification()).isEqualTo(nextNotification)
    }

    @Test
    fun when_there_is_no_next_notification_then_return_null() = runTest {
        assertThat(notificationRepository.getNextNotification()).isNull()
    }
}