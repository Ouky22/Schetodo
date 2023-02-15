package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

sealed class AddEditScheduleBlockEvent {
    data class ChangeTodoBlockNotes(val notes: String) : AddEditScheduleBlockEvent()
}
