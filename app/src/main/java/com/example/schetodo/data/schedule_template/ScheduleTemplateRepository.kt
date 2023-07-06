package com.example.schetodo.data.schedule_template

import kotlinx.coroutines.flow.Flow

interface ScheduleTemplateRepository {
    suspend fun insert(scheduleTemplate: ScheduleTemplate): Long
    suspend fun insertOrUpdate(scheduleTemplate: ScheduleTemplate): Long
    fun getAll(): Flow<List<ScheduleTemplate>>
    fun getById(templateId: Int): Flow<ScheduleTemplate>
    suspend fun deleteById(templateId: Int)
}