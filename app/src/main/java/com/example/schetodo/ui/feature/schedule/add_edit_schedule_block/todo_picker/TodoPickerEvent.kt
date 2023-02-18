package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.todo_picker

import com.example.schetodo.data.todo.Todo

sealed class TodoPickerEvent {
    data class NavigateToNewTodoCategory(val newTodoCategoryId: Int?) : TodoPickerEvent()
    object NavigateToPreviousTodoCategory : TodoPickerEvent()
    data class MarkTodoForSelection(val todo: Todo) : TodoPickerEvent()
    data class UndoMarkTodoForSelection(val todo: Todo) : TodoPickerEvent()
}