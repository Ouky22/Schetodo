package com.example.schetodo.ui.feature.schedule

data class ScheduleState(
    val uiScheduleBlocks: List<UiScheduleBlock> = emptyList(),
    val currentDate: String = ""
)