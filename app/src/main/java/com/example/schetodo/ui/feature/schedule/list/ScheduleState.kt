package com.example.schetodo.ui.feature.schedule.list

data class ScheduleState(
    val scheduleListItems: List<ScheduleListItem> = emptyList(),
    val currentDate: String = ""
)