package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.schetodo.ui.navigation.schedule.AddScheduleBlock
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditScheduleBlockViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(AddEditScheduleBlockScreenState())
        private set

    private lateinit var scheduleBlockDate: LocalDate
    private lateinit var startTime: LocalTime
    private lateinit var endTime: LocalTime

    init {
        val dateStamp = savedStateHandle.get<Long>(AddScheduleBlock.dateStampArg)
            ?: throw Exception("Date stamp argument cannot be null")
        updateCurrentDate(dateStamp)

        val startTimeStamp = savedStateHandle.get<Int>(AddScheduleBlock.startTimeStampArg)
        if (startTimeStamp != null && startTimeStamp >= 0)
            updateStartTime(startTimeStamp)
        else
            updateStartTime(0)

        val endTimeStamp = savedStateHandle.get<Int>(AddScheduleBlock.endTimeStampArg)
        if (endTimeStamp != null && endTimeStamp >= 0)
            updateEndTime(endTimeStamp)
        else
            updateEndTime(0)
    }

    private fun updateStartTime(startTimeStamp: Int) {
        startTime = LocalTime.ofSecondOfDay(startTimeStamp.toLong()).withSecond(0).withNano(0)
        state = state.copy(
            startTime = startTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        )
    }

    private fun updateEndTime(endTimeStamp: Int) {
        endTime = LocalTime.ofSecondOfDay(endTimeStamp.toLong()).withSecond(0).withNano(0)
        state = state.copy(
            endTime = endTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        )
    }

    private fun updateCurrentDate(dateStamp: Long) {
        scheduleBlockDate = LocalDate.ofEpochDay(dateStamp)
        state = state.copy(
            date = scheduleBlockDate.format(
                DateTimeFormatter.ofPattern("EEE dd LLL, yyyy", Locale.getDefault())
            )
        )
    }
}