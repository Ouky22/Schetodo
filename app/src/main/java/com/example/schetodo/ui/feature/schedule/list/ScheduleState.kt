package com.example.schetodo.ui.feature.schedule.list

import java.time.LocalDate

data class ScheduleState(
    val schedules: Map<Long, List<ScheduleListItem>> = emptyMap(),
    val currentDateString: String = "",
    val currentDate: LocalDate = LocalDate.now()
)