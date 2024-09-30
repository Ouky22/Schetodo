package com.example.schetodo.data.schedule_template

import androidx.room.*
import com.example.schetodo.data.SCHEDULE_TEMPLATE_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleTemplateDao {

    @Insert
    suspend fun insert(scheduleTemplate: ScheduleTemplate): Long

    @Upsert
    suspend fun insertOrUpdate(scheduleTemplate: ScheduleTemplate): Long

    @Query("SELECT * FROM $SCHEDULE_TEMPLATE_TABLE_NAME WHERE markedForDeletion = 0")
    fun getAll(): Flow<List<ScheduleTemplate>>

    @Query("SELECT * FROM $SCHEDULE_TEMPLATE_TABLE_NAME WHERE templateId = :templateId")
    fun getById(templateId: Int): Flow<ScheduleTemplate?>

    @Query("DELETE FROM $SCHEDULE_TEMPLATE_TABLE_NAME WHERE templateId = :templateId")
    suspend fun deleteById(templateId: Int)

    @Query("UPDATE $SCHEDULE_TEMPLATE_TABLE_NAME SET markedForDeletion = 1 WHERE templateId = :templateId")
    suspend fun markForDeletion(templateId: Int)

    @Query("UPDATE $SCHEDULE_TEMPLATE_TABLE_NAME SET markedForDeletion = 0 WHERE templateId = :templateId")
    suspend fun unmarkForDeletion(templateId: Int)

    @Query("DELETE FROM $SCHEDULE_TEMPLATE_TABLE_NAME WHERE markedForDeletion = 1")
    suspend fun deleteAllMarkedForDeletion()
}