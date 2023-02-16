package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import java.time.LocalTime

sealed class AddEditScheduleBlockEvent {
    data class ChangeTodoBlockNotes(val notes: String) : AddEditScheduleBlockEvent()
    data class ChangeStartTime(val startTime: LocalTime) : AddEditScheduleBlockEvent()
    data class ChangeEndTime(val endTime: LocalTime) : AddEditScheduleBlockEvent()
}
