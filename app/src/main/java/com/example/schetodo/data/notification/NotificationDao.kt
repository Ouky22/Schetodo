package com.example.schetodo.data.notification

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM Notification")
    fun getAllNotifications(): Flow<List<Notification>>

    @Query("SELECT * FROM Notification WHERE todoBlockId = :todoBlockId")
    fun getNotificationsOfTodoBlock(todoBlockId: Int): Flow<List<Notification>>

    @Query("SELECT * FROM Notification WHERE notificationId = :notificationId")
    fun getNotificationById(notificationId: Int): Flow<Notification?>

    @Insert
    suspend fun insertNotification(notification: Notification): Long

    @Delete
    suspend fun deleteNotification(notification: Notification)

    @Query("DELETE FROM Notification WHERE todoBlockId = :todoBlockId")
    suspend fun deleteAllNotificationsOfTodoBlock(todoBlockId: Int)
}