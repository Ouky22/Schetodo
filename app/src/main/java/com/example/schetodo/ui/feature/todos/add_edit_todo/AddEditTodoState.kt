package com.example.schetodo.ui.feature.todos.add_edit_todo

import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority

data class AddEditTodoState(
    val parentTodoCategoryName: String = "",
    val parentTodoCategoryIconName: String = "",
    val parentTodoCategoryColor: Long = 0,
    val todoDescription: String = "",
    val todoFlag: TodoFlag = TodoFlag.UNDONE,
    val todoPriority: TodoPriority = TodoPriority.MEDIUM,
    val inEditingMode: Boolean = false,
    val showInvalidDescriptionError: Boolean = false
)