package com.example.schetodo.ui.feature.schedule.list

import com.example.schetodo.ui.util.UiText
import java.time.LocalTime

interface ScheduleListItem {
    val endTime: LocalTime
    val startTime: LocalTime
    val durationHours: UiText
    val durationMinutes: UiText
}