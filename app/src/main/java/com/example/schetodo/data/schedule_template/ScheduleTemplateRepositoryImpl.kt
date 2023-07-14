package com.example.schetodo.data.schedule_template

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleTemplateRepositoryImpl @Inject constructor(
    private val scheduleTemplateDao: ScheduleTemplateDao
) : ScheduleTemplateRepository {

    override suspend fun insert(scheduleTemplate: ScheduleTemplate) =
        scheduleTemplateDao.insert(scheduleTemplate)

    override suspend fun insertOrUpdate(scheduleTemplate: ScheduleTemplate) =
        scheduleTemplateDao.insertOrUpdate(scheduleTemplate)

    override fun getAll(): Flow<List<ScheduleTemplate>> = scheduleTemplateDao.getAll()

    override fun getById(templateId: Int): Flow<ScheduleTemplate?> =
        scheduleTemplateDao.getById(templateId)

    override suspend fun deleteById(templateId: Int) = scheduleTemplateDao.deleteById(templateId)

    override suspend fun markForDeletion(templateId: Int) =
        scheduleTemplateDao.markForDeletion(templateId)

    override suspend fun unmarkForDeletion(templateId: Int) =
        scheduleTemplateDao.unmarkForDeletion(templateId)
}