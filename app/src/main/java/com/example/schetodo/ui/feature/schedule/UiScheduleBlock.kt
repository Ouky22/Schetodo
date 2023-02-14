package com.example.schetodo.ui.feature.schedule

import com.example.schetodo.data.todo_category.TodoCategory

data class UiScheduleBlock(
    val id: Int = 0,
    val categories: List<TodoCategory> = emptyList(),
    val todoDescriptions: List<String> = emptyList(),
    val notes: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val duration: String = ""
)