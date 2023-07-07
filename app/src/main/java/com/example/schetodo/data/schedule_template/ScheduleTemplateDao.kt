package com.example.schetodo.data.schedule_template

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleTemplateDao {

    @Insert
    suspend fun insert(scheduleTemplate: ScheduleTemplate): Long

    @Upsert
    suspend fun insertOrUpdate(scheduleTemplate: ScheduleTemplate): Long

    @Query("SELECT * FROM ScheduleTemplate")
    fun getAll(): Flow<List<ScheduleTemplate>>

    @Query("SELECT * FROM ScheduleTemplate WHERE templateId = :templateId")
    fun getById(templateId: Int): Flow<ScheduleTemplate?>

    @Query("DELETE FROM ScheduleTemplate WHERE templateId = :templateId")
    suspend fun deleteById(templateId: Int)
}