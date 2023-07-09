package com.example.schetodo.feature.todos.add_edit_category

sealed class AddEditTodoCategoryEvent {
    data class ChangeTodoCategoryName(val name: String): AddEditTodoCategoryEvent()
    data class ChangeTodoCategoryIcon(val name: String): AddEditTodoCategoryEvent()
    data class ChangeTodoCategoryColor(val color: Long): AddEditTodoCategoryEvent()
    object ShowColorPicker: AddEditTodoCategoryEvent()
    object ShowIconPicker: AddEditTodoCategoryEvent()
    object SaveTodoCategory: AddEditTodoCategoryEvent()
    object MarkTodoCategoryForDeletion: AddEditTodoCategoryEvent()
}