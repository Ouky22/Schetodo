package com.example.schetodo.data.notification

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
internal class NotificationRepositoryImplTest {

    private val fakeNotificationDao = FakeNotificationDao()
    private val notificationRepository = NotificationRepositoryImpl(fakeNotificationDao)

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
        notificationRepository.setNotificationsOfTodoBlock(todoBlockId, newNotifications)

        val allNotificationsOfTodoBlock =
            notificationRepository.getNotificationsOfTodoBlock(todoBlockId).first()
        assertThat(allNotificationsOfTodoBlock).containsExactlyElementsIn(newNotifications)
    }

    @Test
    fun when_there_is_next_notification_then_return_it() = runTest {
        val currentDateTime = LocalDateTime.now(ZoneId.of("UTC"))
        val expiredNotification = Notification(1, currentDateTime.minusHours(1), 1)
        val nextNotification = Notification(2, currentDateTime.plusHours(2), 1)
        val notification = Notification(3, currentDateTime.plusDays(1).plusHours(1), 1)

        fakeNotificationDao.insertNotification(expiredNotification)
        fakeNotificationDao.insertNotification(nextNotification)
        fakeNotificationDao.insertNotification(notification)

        assertThat(notificationRepository.getNextNotification()).isEqualTo(nextNotification)
    }

    @Test
    fun when_there_is_no_next_notification_then_return_null() = runTest {
        assertThat(notificationRepository.getNextNotification()).isNull()
    }
}