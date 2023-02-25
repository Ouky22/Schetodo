package com.example.schetodo.ui.feature.schedule.list

import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.util.UiText
import java.time.LocalTime

data class UiScheduleBlock(
    val todoBlockId: Int = 0,
    override val startTime: LocalTime = LocalTime.of(0, 0),
    override val endTime: LocalTime = LocalTime.of(0, 0),
    val startTimeText: String = "",
    val endTimeText: String = "",
    override val durationHours: UiText = UiText.DynamicString(""),
    override val durationMinutes: UiText = UiText.DynamicString(""),
    val categories: List<TodoCategory> = emptyList(),
    val todoDescriptions: List<String> = emptyList(),
    val notes: String = "",
    val isCurrentScheduleBlock: Boolean = false
) : ScheduleListItem