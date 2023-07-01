package com.example.schetodo.ui.feature.schedule.list

sealed class ScheduleEvent {
    object GoToNextDate : ScheduleEvent()
    object GoToPreviousDate : ScheduleEvent()
    object GoToCurrentDate : ScheduleEvent()
    data class UnmarkTodoBlockForDeletion(val todoBlockId: Int) : ScheduleEvent()
}