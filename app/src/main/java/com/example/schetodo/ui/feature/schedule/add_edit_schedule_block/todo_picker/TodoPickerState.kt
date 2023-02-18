package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.todo_picker

import com.example.schetodo.data.todo_category.TodoCategory

data class TodoPickerState(
    val currentCategory: TodoCategory? = null, // the todos screen starts with no category selected, so currentCategory is initially null
    val childCategories: List<TodoCategory> = emptyList(),
    val todos: List<TodoWithSelector> = emptyList(),
    val showTopBarBackButton: Boolean = false
)
