package com.example.schetodo.ui.feature.todos.list

import com.example.schetodo.data.todo.TodoFilterSettings

sealed class TodosEvent {
    data class NavigateToNewTodoCategory(val newTodoCategoryId: Int?) : TodosEvent()
    object NavigateToPreviousTodoCategory : TodosEvent()
    object ClickOnAddCategoryOrTodoButton : TodosEvent()
    object NavigateToAddTodoCategoryScreen : TodosEvent()
    object NavigateToAddTodoScreen : TodosEvent()
    object CloseAddCategoryOrTodoDialog : TodosEvent()
    data class ChangeTodoFilterSettings(val newFilterSettings: TodoFilterSettings) : TodosEvent()
}