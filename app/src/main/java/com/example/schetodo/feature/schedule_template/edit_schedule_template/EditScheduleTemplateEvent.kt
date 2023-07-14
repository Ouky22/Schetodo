package com.example.schetodo.feature.schedule_template.edit_schedule_template

import java.time.LocalDate

open class EditScheduleTemplateEvent {
    object DeleteScheduleTemplate : EditScheduleTemplateEvent()
    data class UndoDeleteScheduleBlock(val todoBlockId: Int) : EditScheduleTemplateEvent()
    data class SelectScheduleTemplateApplyDate(val date: LocalDate): EditScheduleTemplateEvent()
    object ApplyScheduleTemplateToDate : EditScheduleTemplateEvent()
}