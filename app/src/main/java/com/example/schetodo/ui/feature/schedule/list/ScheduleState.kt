package com.example.schetodo.ui.feature.schedule.list

data class ScheduleState(
    val uiScheduleBlocks: List<UiScheduleBlock> = emptyList(),
    val currentDate: String = ""
)