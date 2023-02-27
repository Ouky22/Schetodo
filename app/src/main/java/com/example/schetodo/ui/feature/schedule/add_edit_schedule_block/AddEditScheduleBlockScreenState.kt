package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_category.TodoCategory

data class AddEditScheduleBlockScreenState(
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val todoCategories: List<TodoCategory> = emptyList(),
    val todos: List<Todo> = emptyList(),
    val notes: String = "",
    val inEditingMode: Boolean = false,
    val showNotificationAtBeginning: Boolean = false,
    val showNotificationAtEnd: Boolean = false,
)