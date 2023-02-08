package com.example.schetodo.ui.feature.todos.addedit

sealed class AddEditTodoCategoryEvent {
    data class ChangeTodoCategoryName(val name: String): AddEditTodoCategoryEvent()
}