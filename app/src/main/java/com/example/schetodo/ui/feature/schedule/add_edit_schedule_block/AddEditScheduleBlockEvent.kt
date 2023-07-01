package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_category.TodoCategory
import java.time.LocalDate
import java.time.LocalTime

sealed class AddEditScheduleBlockEvent {
    data class ChangeTodoBlockNotes(val notes: String) : AddEditScheduleBlockEvent()
    data class ChangeDate(val date: LocalDate) : AddEditScheduleBlockEvent()
    data class ChangeStartTime(val startTime: LocalTime) : AddEditScheduleBlockEvent()
    data class ChangeEndTime(val endTime: LocalTime) : AddEditScheduleBlockEvent()
    data class SelectTodos(val todoIds: List<Int>) : AddEditScheduleBlockEvent()
    data class RemoveSelectedTodo(val todo: Todo) : AddEditScheduleBlockEvent()
    data class SelectTodoCategories(val todoCategoryIds: List<Int>) : AddEditScheduleBlockEvent()
    data class RemoveSelectedTodoCategory(val category: TodoCategory) : AddEditScheduleBlockEvent()
    object MarkScheduleBlockForDeletion : AddEditScheduleBlockEvent()
    object SaveScheduleBlock : AddEditScheduleBlockEvent()
    data class ChangeShowNotificationAtBeginning(
        val showNotification: Boolean
    ) : AddEditScheduleBlockEvent()

    data class ChangeShowNotificationAtEnd(
        val showNotification: Boolean
    ) : AddEditScheduleBlockEvent()
}
