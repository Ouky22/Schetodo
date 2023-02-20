package com.example.schetodo.ui.feature.schedule.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.data.todo.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository
) : ViewModel() {

    private var currentDate = LocalDate.now()

    val currentDateStamp: Long
        get() = currentDate.toEpochDay()

    private val _scheduleState = MutableStateFlow(ScheduleState())
    val scheduleState: StateFlow<ScheduleState>
        get() = _scheduleState.asStateFlow()

    private var collectScheduleBlocksJob: Job? = null


    init {
        loadScheduleBlocksOnCurrentDate()

        updateCurrentDate(LocalDate.now())
    }

    private fun loadScheduleBlocksOnCurrentDate() {
        collectScheduleBlocksJob?.cancel()

        collectScheduleBlocksJob = viewModelScope.launch {
            scheduleBlockRepository.getScheduleBlocksOnDate(currentDate).map { scheduleBlocks ->
                scheduleBlocks.map { scheduleBlock ->
                    convertScheduleBlockToUiScheduleBlock(scheduleBlock)
                }
            }.collect {
                _scheduleState.value = _scheduleState.value.copy(
                    uiScheduleBlocks = it
                )
            }
        }
    }

    private fun convertScheduleBlockToUiScheduleBlock(scheduleBlock: ScheduleBlock) =
        UiScheduleBlock(
            id = scheduleBlock.todoBlock.todoBlockId,
            categories = scheduleBlock.todoCategories.sortedBy { it.name },
            todoDescriptions = scheduleBlock.todos
                .sortedWith(compareByDescending(Todo::priority).thenBy(Todo::description))
                .map { it.description },
            notes = scheduleBlock.todoBlock.notes ?: "",
            startTime = scheduleBlock.todoBlock.startTime.toString(),
            endTime = scheduleBlock.todoBlock.endTime.toString(),
            duration = scheduleBlock.todoBlock.startTime.until(
                scheduleBlock.todoBlock.endTime, ChronoUnit.HOURS
            ).toString()
        )

    private fun updateCurrentDate(date: LocalDate) {
        currentDate = date
        _scheduleState.value = scheduleState.value.copy(
            currentDate = currentDate.format(
                DateTimeFormatter.ofPattern("EEE dd LLL, yyyy", Locale.getDefault())
            )
        )
    }
}