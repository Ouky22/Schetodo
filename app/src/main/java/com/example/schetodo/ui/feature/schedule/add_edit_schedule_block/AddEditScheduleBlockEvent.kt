package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import com.example.schetodo.data.todo.Todo
import java.time.LocalDate
import java.time.LocalTime

sealed class AddEditScheduleBlockEvent {
    data class ChangeTodoBlockNotes(val notes: String) : AddEditScheduleBlockEvent()
    data class ChangeDate(val date: LocalDate) : AddEditScheduleBlockEvent()
    data class ChangeStartTime(val startTime: LocalTime) : AddEditScheduleBlockEvent()
    data class ChangeEndTime(val endTime: LocalTime) : AddEditScheduleBlockEvent()
    data class SelectTodos(val todoIds: List<Int>) : AddEditScheduleBlockEvent()
    data class RemoveSelectedTodo(val todo: Todo) : AddEditScheduleBlockEvent()
}
