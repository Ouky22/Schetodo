package com.example.schetodo.ui.feature.schedule.list

import com.example.schetodo.ui.util.UiText
import java.time.LocalTime

interface ScheduleListItem {
    val startTime: LocalTime
    val endTime: LocalTime
    val durationHours: UiText
    val durationMinutes: UiText
}