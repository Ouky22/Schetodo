package com.example.schetodo.ui.feature.schedule.list

import java.time.LocalDate

sealed class ScheduleEvent {
    object GoToNextDate : ScheduleEvent()
    object GoToPreviousDate : ScheduleEvent()
    object GoToCurrentDate : ScheduleEvent()
    data class GoToAnyDate(val date: LocalDate): ScheduleEvent()
    data class UnmarkTodoBlockForDeletion(val todoBlockId: Int) : ScheduleEvent()
    data class SaveCurrentScheduleAsTemplate(val templateName: String): ScheduleEvent()
}