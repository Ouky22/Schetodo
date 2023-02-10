package com.example.schetodo.ui.feature.todos.list

import com.example.schetodo.data.entity.Todo
import com.example.schetodo.data.entity.TodoCategory

data class TodosState(
    val currentCategory: TodoCategory? = null, // the todos screen starts with no category selected, so currentCategory is initially null
    val childCategories : List<TodoCategory> = emptyList(),
    val todos: List<Todo> = emptyList(),
    val showAddCategoryOrTodoDialog: Boolean = false
) {
    val currentCategoryIsChildCategory
        get() = currentCategory != null
}