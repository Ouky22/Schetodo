package com.example.schetodo.data

import com.example.schetodo.data.schedule_template.ScheduleTemplate
import com.example.schetodo.data.schedule_template.ScheduleTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeScheduleTemplateRepository : ScheduleTemplateRepository {
    private var scheduleTemplates = mutableListOf<ScheduleTemplate>()

    override suspend fun insert(scheduleTemplate: ScheduleTemplate): Long {
        scheduleTemplates.add(scheduleTemplate)
        return scheduleTemplate.templateId.toLong()
    }

    override suspend fun insertOrUpdate(scheduleTemplate: ScheduleTemplate): Long {
        val indexOfScheduleTemplate =
            scheduleTemplates.indexOfFirst { it.templateId == scheduleTemplate.templateId }

        val notInList = indexOfScheduleTemplate == -1
        return if (notInList) {
            insert(scheduleTemplate)
        } else {
            val oldScheduleTemplate = scheduleTemplates.removeAt(indexOfScheduleTemplate)
            val newScheduleTemplate =
                scheduleTemplate.copy(templateId = oldScheduleTemplate.templateId)
            insert(newScheduleTemplate)
        }
    }

    override fun getAll(): Flow<List<ScheduleTemplate>> {
        return flow {
            emit(scheduleTemplates)
        }
    }

    override fun getById(templateId: Int): Flow<ScheduleTemplate?> {
        return flow {
            emit(scheduleTemplates.firstOrNull { it.templateId == templateId })
        }
    }

    override suspend fun deleteById(templateId: Int) {
        scheduleTemplates.removeIf { templateId == it.templateId }
    }

    override suspend fun markForDeletion(templateId: Int) {
        scheduleTemplates = scheduleTemplates.map { scheduleTemplate ->
            if (scheduleTemplate.templateId == templateId)
                scheduleTemplate.copy(markedForDeletion = true)
            else
                scheduleTemplate
        }.toMutableList()
    }

    override suspend fun unmarkForDeletion(templateId: Int) {
        scheduleTemplates = scheduleTemplates.map { scheduleTemplate ->
            if (scheduleTemplate.templateId == templateId)
                scheduleTemplate.copy(markedForDeletion = false)
            else
                scheduleTemplate
        }.toMutableList()
    }
}