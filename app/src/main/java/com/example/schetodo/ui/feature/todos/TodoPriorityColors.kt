package com.example.schetodo.ui.feature.todos

import androidx.compose.ui.graphics.Color
import com.example.schetodo.data.todo.TodoPriority

val lowTodoPriorityColor = Color(0xFF93BFCF)
val mediumTodoPriorityColor = Color(0xFF6D8B74)
val highTodoPriorityColor = Color(0xFFF2D388)
val veryHighTodoPriorityColor = Color(0xFFE97777)

fun getTodoPriorityColorOf(todoPriority: TodoPriority) = when (todoPriority) {
    TodoPriority.LOW -> lowTodoPriorityColor
    TodoPriority.MEDIUM -> mediumTodoPriorityColor
    TodoPriority.HIGH -> highTodoPriorityColor
    TodoPriority.VERY_HIGH -> veryHighTodoPriorityColor
}