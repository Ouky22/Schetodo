package com.example.schetodo.ui.feature.todos.check_off_todos

sealed class CheckOffTodosEvent {
    data class MarkTodoForCheckOff(val todoId: Int): CheckOffTodosEvent()
    data class UndoMarkTodoForCheckOff(val todoId: Int): CheckOffTodosEvent()
    object CheckOffMarkedTodos : CheckOffTodosEvent()
}