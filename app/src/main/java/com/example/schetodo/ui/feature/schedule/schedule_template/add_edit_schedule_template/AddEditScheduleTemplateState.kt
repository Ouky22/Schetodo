package com.example.schetodo.ui.feature.schedule.schedule_template.add_edit_schedule_template

import com.example.schetodo.ui.feature.schedule.components.ScheduleListItem

data class AddEditScheduleTemplateState(
    val scheduleItems: List<ScheduleListItem> = emptyList(),
    val scheduleTemplateName: String = ""
)