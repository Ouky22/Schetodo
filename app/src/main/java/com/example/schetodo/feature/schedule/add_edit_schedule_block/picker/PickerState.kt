package com.example.schetodo.feature.schedule.add_edit_schedule_block.picker

import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_category.TodoCategory

data class PickerState<T>(
    val currentCategory: TodoCategory? = null, // the todos screen starts with no category selected, so currentCategory is initially null
    val childCategories: List<TodoCategory> = emptyList(),
    val todos: List<Todo> = emptyList(),
    val selectedItems: List<T> = emptyList(),
    val showTopBarBackButton: Boolean = false,

) {
    val currentCategoryIsChildCategory: Boolean
        get() = currentCategory != null
}