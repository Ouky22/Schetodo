package com.example.schetodo.ui.feature.schedule.list

sealed class ScheduleEvent {
    object GoToNextDate : ScheduleEvent()
    object GoToPreviousDate: ScheduleEvent()
}