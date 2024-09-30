package com.example.schetodo.data.notification

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.schetodo.data.NOTIFICATION_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM $NOTIFICATION_TABLE_NAME")
    fun getAllNotifications(): Flow<List<Notification>>

    @Query("SELECT * FROM $NOTIFICATION_TABLE_NAME WHERE todoBlockId = :todoBlockId")
    fun getNotificationsOfTodoBlock(todoBlockId: Int): Flow<List<Notification>>

    @Query("SELECT * FROM $NOTIFICATION_TABLE_NAME WHERE notificationId = :notificationId")
    fun getNotificationById(notificationId: Int): Flow<Notification?>

    @Insert
    suspend fun insertNotification(notification: Notification): Long

    @Insert
    suspend fun insertNotifications(notifications: List<Notification>)

    @Delete
    suspend fun deleteNotification(notification: Notification)

    @Query("DELETE FROM $NOTIFICATION_TABLE_NAME WHERE todoBlockId = :todoBlockId")
    suspend fun deleteAllNotificationsOfTodoBlock(todoBlockId: Int)
}