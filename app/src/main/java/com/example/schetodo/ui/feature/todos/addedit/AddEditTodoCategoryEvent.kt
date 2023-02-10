package com.example.schetodo.ui.feature.todos.addedit

sealed class AddEditTodoCategoryEvent {
    data class ChangeTodoCategoryName(val name: String): AddEditTodoCategoryEvent()
    data class ChangeTodoCategoryIcon(val name: String): AddEditTodoCategoryEvent()
    data class ChangeTodoCategoryColor(val color: Long): AddEditTodoCategoryEvent()
    object ShowColorPicker: AddEditTodoCategoryEvent()
    object ShowIconPicker: AddEditTodoCategoryEvent()
    object SaveTodoCategory: AddEditTodoCategoryEvent()
    object DeleteTodoCategory: AddEditTodoCategoryEvent()
}