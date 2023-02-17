package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.todo_picker

import com.example.schetodo.data.todo.Todo

class TodoPickerViewModel {
}

data class TodoWithSelector(
    val todo: Todo,
    val selected: Boolean
)