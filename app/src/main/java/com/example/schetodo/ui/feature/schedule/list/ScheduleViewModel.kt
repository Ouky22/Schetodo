package com.example.schetodo.ui.feature.schedule.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
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

        _scheduleState.value = _scheduleState.value.copy(currentDate = currentDate.toString())
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
            categories = scheduleBlock.todoCategories,
            todoDescriptions = scheduleBlock.todos.map { it.description },
            notes = scheduleBlock.todoBlock.notes ?: "",
            startTime = scheduleBlock.todoBlock.startTime.toString(),
            endTime = scheduleBlock.todoBlock.endTime.toString(),
            duration = scheduleBlock.todoBlock.startTime.until(
                scheduleBlock.todoBlock.endTime, ChronoUnit.HOURS
            ).toString()
        )
}