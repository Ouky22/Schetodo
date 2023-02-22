package com.example.schetodo.ui.feature.todos.list

import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFilterSettings
import com.example.schetodo.data.todo_category.TodoCategory

data class TodosState(
    val currentCategory: TodoCategory? = null, // the todos screen starts with no category selected, so currentCategory is initially null
    val childCategories: List<TodoCategory> = emptyList(),
    val todos: List<Todo> = emptyList(),
    val showAddCategoryOrTodoDialog: Boolean = false,
    val checkOffTodosButtonActivated: Boolean = false,
    val todoFilterSettings: TodoFilterSettings = TodoFilterSettings()
) {
    val currentCategoryIsChildCategory
        get() = currentCategory != null
}