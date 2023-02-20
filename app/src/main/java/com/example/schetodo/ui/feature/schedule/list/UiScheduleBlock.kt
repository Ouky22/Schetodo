package com.example.schetodo.ui.feature.schedule.list

import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.util.UiText

data class UiScheduleBlock(
    val id: Int = 0,
    val categories: List<TodoCategory> = emptyList(),
    val todoDescriptions: List<String> = emptyList(),
    val notes: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val durationHours: UiText = UiText.DynamicString(""),
    val durationMinutes: UiText = UiText.DynamicString("")
)