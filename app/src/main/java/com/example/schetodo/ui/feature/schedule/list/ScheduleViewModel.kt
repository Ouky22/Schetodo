package com.example.schetodo.ui.feature.schedule.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.R
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.schetodo.ui.feature.schedule.list.ScheduleEvent.*
import java.time.LocalTime
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

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is GoToNextDate -> goToNextDate()
            is GoToPreviousDate -> goToPreviousDate()
        }
    }

    private fun goToPreviousDate() {
        updateCurrentDate(currentDate.minusDays(1))
        loadScheduleBlocksOnCurrentDate()
    }

    private fun goToNextDate() {
        updateCurrentDate(currentDate.plusDays(1))
        loadScheduleBlocksOnCurrentDate()
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

    private fun convertScheduleBlockToUiScheduleBlock(scheduleBlock: ScheduleBlock): UiScheduleBlock {
        val todoBlock = scheduleBlock.todoBlock
        val duration = Duration.between(todoBlock.startTime, todoBlock.endTime)

        return UiScheduleBlock(
            id = todoBlock.todoBlockId,
            categories = scheduleBlock.todoCategories.sortedBy { it.name },
            todoDescriptions = scheduleBlock.todos
                .sortedWith(compareByDescending(Todo::priority).thenBy(Todo::description))
                .map { it.description },
            notes = todoBlock.notes ?: "",
            startTime = todoBlock.startTime.toString(),
            endTime = todoBlock.endTime.toString(),
            durationHours = getDurationHoursUiText(duration),
            durationMinutes = getDurationMinutesUiText(duration),
            isCurrentScheduleBlock = isCurrentScheduleBlock(todoBlock)
        )
    }

    private fun isCurrentScheduleBlock(todoBlockOfScheduleBlock: TodoBlock) =
        LocalDate.now() == todoBlockOfScheduleBlock.date &&
                LocalTime.now().isAfter(todoBlockOfScheduleBlock.startTime) &&
                LocalTime.now().isBefore(todoBlockOfScheduleBlock.endTime)

    private fun getDurationHoursUiText(duration: Duration): UiText {
        val durationHours = duration.toHours().toInt()
        return if (durationHours >= 1)
            UiText.StringResource(R.string.hour, durationHours)
        else
            UiText.DynamicString("")
    }

    private fun getDurationMinutesUiText(duration: Duration): UiText {
        val durationMinutes = (duration.toMinutes() % 60).toInt()
        return if (durationMinutes >= 1)
            UiText.StringResource(R.string.minute, durationMinutes)
        else
            UiText.DynamicString("")
    }

    private fun updateCurrentDate(date: LocalDate) {
        currentDate = date
        _scheduleState.value = scheduleState.value.copy(
            currentDate = currentDate.format(
                DateTimeFormatter.ofPattern("EEE dd LLL, yyyy", Locale.getDefault())
            )
        )
    }
}