package com.example.schetodo.feature.schedule_template.list

open class ScheduleTemplatesEvent {
    data class UndoDeletionOfScheduleTemplate(val templateId: Int) : ScheduleTemplatesEvent()
}