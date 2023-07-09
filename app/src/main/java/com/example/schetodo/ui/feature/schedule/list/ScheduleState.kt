package com.example.schetodo.ui.feature.schedule.list

import com.example.schetodo.data.MAX_DATE
import java.time.LocalDate

data class ScheduleState(
    val schedules: Map<Long, List<ScheduleListItem>> = emptyMap(),
    val currentDateString: String = "",
    val currentDate: LocalDate = LocalDate.now(),
    val maxDate: LocalDate = MAX_DATE,
    val canNavigateToNextDate: Boolean = false,
    val canNavigateToPreviousDate: Boolean = false
)