package com.example.schetodo.ui.feature.schedule.list

data class ScheduleState(
    val schedules: Array<List<ScheduleListItem>> =  Array(3) { emptyList() },
    val currentDate: String = ""
)