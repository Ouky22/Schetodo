package com.example.schetodo.data.schedule_template

import com.example.schetodo.di.CoroutineScopeModule.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleTemplateRepositoryImpl @Inject constructor(
    private val scheduleTemplateDao: ScheduleTemplateDao,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope
) : ScheduleTemplateRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            scheduleTemplateDao.deleteAllMarkedForDeletion()
        }
    }

    override suspend fun insert(scheduleTemplate: ScheduleTemplate) =
        withContext(applicationCoroutineScope.coroutineContext) {
            scheduleTemplateDao.insert(scheduleTemplate)
        }

    override suspend fun insertOrUpdate(scheduleTemplate: ScheduleTemplate) =
        withContext(applicationCoroutineScope.coroutineContext) {
            scheduleTemplateDao.insertOrUpdate(scheduleTemplate)
        }

    override fun getAll(): Flow<List<ScheduleTemplate>> = scheduleTemplateDao.getAll()

    override fun getById(templateId: Int): Flow<ScheduleTemplate?> =
        scheduleTemplateDao.getById(templateId)

    override suspend fun deleteById(templateId: Int) {
        applicationCoroutineScope.launch {
            scheduleTemplateDao.deleteById(templateId)
        }.join()
    }

    override suspend fun markForDeletion(templateId: Int) {
        applicationCoroutineScope.launch {
            scheduleTemplateDao.markForDeletion(templateId)
        }.join()
    }

    override suspend fun unmarkForDeletion(templateId: Int) {
        applicationCoroutineScope.launch {
            scheduleTemplateDao.unmarkForDeletion(templateId)
        }.join()
    }
}