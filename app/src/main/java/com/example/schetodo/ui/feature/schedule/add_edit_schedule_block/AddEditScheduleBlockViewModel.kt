package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.ui.navigation.schedule.AddScheduleBlock
import com.example.schetodo.ui.navigation.schedule.EditScheduleBlock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditScheduleBlockViewModel @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(AddEditScheduleBlockScreenState())
        private set

    private lateinit var scheduleBlockDate: LocalDate
    private lateinit var startTime: LocalTime
    private lateinit var endTime: LocalTime

    init {
        val todoBlockId = savedStateHandle.get<Int>(EditScheduleBlock.todoBlockIdArg)
        val viewModelCreatedForEditing = todoBlockId != null && todoBlockId >= 1

        if (viewModelCreatedForEditing) {
            loadScheduleBlock(todoBlockId!!)
        } else {
            val dateStamp = savedStateHandle.get<Long>(AddScheduleBlock.dateStampArg)
                ?: throw Exception("Date stamp argument cannot be null when adding ScheduleBlock")

            updateCurrentDate(dateStamp)

            val startTimeStamp = savedStateHandle.get<Int>(AddScheduleBlock.startTimeStampArg)
            val startTimePassed = startTimeStamp != null && startTimeStamp >= 0
            if (startTimePassed) updateStartTime(startTimeStamp!!)
            else updateStartTime(0)

            val endTimeStamp = savedStateHandle.get<Int>(AddScheduleBlock.endTimeStampArg)
            val endTimePassed = endTimeStamp != null && endTimeStamp >= 0
            if (endTimePassed) updateEndTime(endTimeStamp!!)
            else updateEndTime(0)
        }
    }

    fun onEvent(event: AddEditScheduleBlockEvent) {
        when (event) {
            is AddEditScheduleBlockEvent.ChangeTodoBlockNotes -> updateTodoBlockNotes(event.notes)
            is AddEditScheduleBlockEvent.ChangeStartTime -> updateStartTime(event.startTime)
            is AddEditScheduleBlockEvent.ChangeEndTime -> updateEndTime(event.endTime)
        }
    }

    private fun updateTodoBlockNotes(notes: String) {
        state = state.copy(
            notes = notes
        )
    }

    private fun loadScheduleBlock(todoBlockId: Int) {
        viewModelScope.launch {
            val scheduleBlock =
                scheduleBlockRepository.getScheduleBlockByTodoBlockId(todoBlockId).first()
                    ?: throw Exception("There is no ScheduleBlock with TodoBlock id $todoBlockId")

            updateCurrentDate(
                scheduleBlock.todoBlock.date ?: throw Exception("TodoBlock needs a date")
            )
            updateStartTime(scheduleBlock.todoBlock.startTime.toSecondOfDay())
            updateEndTime(scheduleBlock.todoBlock.endTime.toSecondOfDay())
            state = state.copy(
                todoCategories = scheduleBlock.todoCategories,
                todos = scheduleBlock.todos,
                notes = scheduleBlock.todoBlock.notes ?: "",
                inEditingMode = true
            )
        }
    }

    private fun updateStartTime(startTime: LocalTime) {
        this.startTime = startTime
        state = state.copy(
            startTime = startTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        )
    }

    private fun updateEndTime(endTime: LocalTime) {
        this.endTime = endTime
        state = state.copy(
            endTime = endTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        )
    }

    private fun updateCurrentDate(date: LocalDate) {
        scheduleBlockDate = date
        state = state.copy(
            date = scheduleBlockDate.format(
                DateTimeFormatter.ofPattern("EEE dd LLL, yyyy", Locale.getDefault())
            )
        )
    }

    private fun updateStartTime(startTimeStamp: Int) =
        updateStartTime(LocalTime.ofSecondOfDay(startTimeStamp.toLong()).withSecond(0).withNano(0))

    private fun updateEndTime(endTimeStamp: Int) =
        updateEndTime(LocalTime.ofSecondOfDay(endTimeStamp.toLong()).withSecond(0).withNano(0))

    private fun updateCurrentDate(dateStamp: Long) =
        updateCurrentDate(LocalDate.ofEpochDay(dateStamp))
}