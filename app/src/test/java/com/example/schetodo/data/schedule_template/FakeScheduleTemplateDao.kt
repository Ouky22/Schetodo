package com.example.schetodo.data.schedule_template

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeScheduleTemplateDao : ScheduleTemplateDao {
    var templates = mutableListOf<ScheduleTemplate>()

    override suspend fun insert(scheduleTemplate: ScheduleTemplate): Long {
        templates.add(scheduleTemplate)
        return scheduleTemplate.templateId.toLong()
    }

    override suspend fun insertOrUpdate(scheduleTemplate: ScheduleTemplate): Long {
        templates.removeIf { it.templateId == scheduleTemplate.templateId }
        templates.add(scheduleTemplate)
        return scheduleTemplate.templateId.toLong()
    }

    override fun getAll(): Flow<List<ScheduleTemplate>> {
        return flow {
            emit(templates)
        }
    }

    override fun getById(templateId: Int): Flow<ScheduleTemplate?> {
        return flow {
            emit(templates.firstOrNull { it.templateId == templateId })
        }
    }

    override suspend fun deleteById(templateId: Int) {
        templates.removeIf { it.templateId == templateId }
    }

    override suspend fun markForDeletion(templateId: Int) {
        templates = templates.map { template ->
            if (template.templateId == templateId)
                template.copy(markedForDeletion = true)
            else
                template
        }.toMutableList()
    }

    override suspend fun unmarkForDeletion(templateId: Int) {
        templates = templates.map { template ->
            if (template.templateId == templateId)
                template.copy(markedForDeletion = false)
            else
                template
        }.toMutableList()
    }

    override suspend fun deleteAllMarkedForDeletion() {
        templates.removeIf { it.markedForDeletion }
    }
}