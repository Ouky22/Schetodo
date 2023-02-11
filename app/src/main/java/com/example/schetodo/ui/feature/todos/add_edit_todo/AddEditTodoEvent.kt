package com.example.schetodo.ui.feature.todos.add_edit_todo

import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority

sealed class AddEditTodoEvent {
    data class ChangeTodoDescription(val todoDescription: String) : AddEditTodoEvent()
    data class ChangeTodoPriority(val todoPriority: TodoPriority) : AddEditTodoEvent()
    data class ChangeTodoFlag(val todoFlag: TodoFlag) : AddEditTodoEvent()
    object SaveTodo : AddEditTodoEvent()
    object CloseScreen: AddEditTodoEvent()
}