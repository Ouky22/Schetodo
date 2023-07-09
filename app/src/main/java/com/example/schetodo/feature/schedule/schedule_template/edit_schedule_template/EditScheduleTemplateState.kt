package com.example.schetodo.feature.schedule.schedule_template.edit_schedule_template

import com.example.schetodo.feature.schedule.components.ScheduleListItem


data class EditScheduleTemplateState(
    val scheduleItems: List<ScheduleListItem> = emptyList(),
    val scheduleTemplateName: String = ""
)