package com.example.schetodo.feature.schedule.list

import java.time.LocalDate

sealed class ScheduleEvent {
    object GoToNextDate : ScheduleEvent()
    object GoToPreviousDate : ScheduleEvent()
    object GoToCurrentDate : ScheduleEvent()
    data class GoToAnyDate(val date: LocalDate) : ScheduleEvent()
    data class UnmarkTodoBlockForDeletion(val todoBlockId: Int) : ScheduleEvent()
    object MarkAllTodoBlocksForDeletion : ScheduleEvent()
    object UndoMarkAllTodoBlocksForDeletion : ScheduleEvent()
    object SaveCurrentScheduleAsTemplate : ScheduleEvent()
    data class ChangeScheduleTemplateName(val templateName: String) : ScheduleEvent()
    object OpenEnterScheduleTemplateNameDialog : ScheduleEvent()
    object CloseEnterScheduleTemplateNameDialog : ScheduleEvent()
}