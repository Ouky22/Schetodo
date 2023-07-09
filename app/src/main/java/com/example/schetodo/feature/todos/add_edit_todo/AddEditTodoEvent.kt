package com.example.schetodo.feature.todos.add_edit_todo

import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority

sealed class AddEditTodoEvent {
    data class ChangeTodoDescription(val todoDescription: String) : AddEditTodoEvent()
    data class ChangeTodoPriority(val todoPriority: TodoPriority) : AddEditTodoEvent()
    data class ChangeTodoFlag(val todoFlag: TodoFlag) : AddEditTodoEvent()
    object SaveTodo : AddEditTodoEvent()
    object MarkTodoForDeletion: AddEditTodoEvent()
    object CloseScreen: AddEditTodoEvent()
}