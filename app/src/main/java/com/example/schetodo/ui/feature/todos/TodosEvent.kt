package com.example.schetodo.ui.feature.todos

sealed class TodosEvent {
    data class NavigateToNewTodoCategory(val newTodoCategoryId: Int?) : TodosEvent()
    object NavigateToPreviousTodoCategory : TodosEvent()
}