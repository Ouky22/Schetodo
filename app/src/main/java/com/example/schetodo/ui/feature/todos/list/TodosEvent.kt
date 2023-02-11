package com.example.schetodo.ui.feature.todos.list

sealed class TodosEvent {
    data class NavigateToNewTodoCategory(val newTodoCategoryId: Int?) : TodosEvent()
    object NavigateToPreviousTodoCategory : TodosEvent()
    object ClickOnAddCategoryorTodoButton : TodosEvent()
    object NavigateToAddTodoCategoryScreen : TodosEvent()
    object NavigateToAddTodoScreen : TodosEvent()
    object CloseAddCategoryOrTodoDialog : TodosEvent()
}