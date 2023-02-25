package com.example.schetodo.ui.feature.schedule.list

import com.example.schetodo.ui.util.UiText
import java.time.LocalTime

data class ScheduleGap(
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val durationHours: UiText = UiText.DynamicString(""),
    override val durationMinutes: UiText = UiText.DynamicString("")
) : ScheduleListItem