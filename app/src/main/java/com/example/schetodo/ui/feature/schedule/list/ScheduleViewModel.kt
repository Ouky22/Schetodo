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
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.schetodo.ui.feature.schedule.list.ScheduleEvent.*
import com.example.schetodo.ui.feature.schedule.notification.TodoBlockNotificationScheduler
import kotlinx.coroutines.flow.*
import java.time.LocalTime
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository,
    private val todoBlockNotificationScheduler: TodoBlockNotificationScheduler
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
            is GoToCurrentDate -> goToCurrentDate()
            is UnmarkTodoBlockForDeletion -> onUnmarkTodoBlockForDeletion(event.todoBlockId)
        }
    }

    private fun onUnmarkTodoBlockForDeletion(todoBlockId: Int) {
        viewModelScope.launch {
            scheduleBlockRepository.unmarkTodoBlockForDeletion(todoBlockId)
            todoBlockNotificationScheduler.scheduleNextNotificationIfExists()
        }
    }

    private fun goToCurrentDate() {
        updateCurrentDate(LocalDate.now())
        loadScheduleBlocksOnCurrentDate()
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
            scheduleBlockRepository.getScheduleBlocksOnDate(currentDate).collect { scheduleBlocks ->
                updateScheduleListItems(scheduleBlocks)
            }
        }
    }

    private fun updateScheduleListItems(scheduleBlocks: List<ScheduleBlock>) {
        val scheduleListItems = convertToScheduleListItems(scheduleBlocks)

        _scheduleState.value = _scheduleState.value.copy(
            scheduleListItems = scheduleListItems
        )
    }

    private fun convertToScheduleListItems(scheduleBlocks: List<ScheduleBlock>): List<ScheduleListItem> {
        var previousEndTime = LocalTime.of(0, 0)
        val scheduleListItems = mutableListOf<ScheduleListItem>()

        for (scheduleBlock in scheduleBlocks.sortedBy { it.todoBlock.startTime }) {
            val gapDuration = Duration.between(previousEndTime, scheduleBlock.todoBlock.startTime)
            if (gapDuration.toMinutes() > 0)
                scheduleListItems.add(
                    ScheduleGap(
                        startTime = previousEndTime,
                        endTime = scheduleBlock.todoBlock.startTime,
                        durationHours = getDurationHoursUiText(gapDuration),
                        durationMinutes = getDurationMinutesUiText(gapDuration)
                    )
                )

            val uiScheduleBlock = convertScheduleBlockToUiScheduleBlock(scheduleBlock)
            scheduleListItems.add(uiScheduleBlock)
            previousEndTime = scheduleBlock.todoBlock.endTime
        }

        if (scheduleBlocks.isNotEmpty()) {
            val scheduleMaxTime = LocalTime.of(23, 59)
            val gap = Duration.between(previousEndTime, scheduleMaxTime)
            if (gap.toMinutes() > 0)
                scheduleListItems.add(
                    ScheduleGap(
                        startTime = previousEndTime,
                        endTime = scheduleMaxTime,
                        durationHours = getDurationHoursUiText(gap),
                        durationMinutes = getDurationMinutesUiText(gap)
                    )
                )
        }

        return scheduleListItems
    }

    private fun convertScheduleBlockToUiScheduleBlock(scheduleBlock: ScheduleBlock): UiScheduleBlock {
        val todoBlock = scheduleBlock.todoBlock
        val duration = Duration.between(todoBlock.startTime, todoBlock.endTime)

        return UiScheduleBlock(
            todoBlockId = todoBlock.todoBlockId,
            categories = scheduleBlock.todoCategories.sortedBy { it.name },
            todoDescriptions = scheduleBlock.todos
                .sortedWith(compareByDescending(Todo::priority).thenBy(Todo::description))
                .map { it.description },
            notes = todoBlock.notes ?: "",
            startTime = todoBlock.startTime,
            endTime = todoBlock.endTime,
            startTimeText = formatTime(todoBlock.startTime),
            endTimeText = formatTime(todoBlock.endTime),
            durationHours = getDurationHoursUiText(duration),
            durationMinutes = getDurationMinutesUiText(duration),
            isCurrentScheduleBlock = isCurrentScheduleBlock(todoBlock)
        )
    }

    private fun isCurrentScheduleBlock(todoBlockOfScheduleBlock: TodoBlock) =
        LocalDate.now() == todoBlockOfScheduleBlock.date &&
                LocalTime.now().isAfter(todoBlockOfScheduleBlock.startTime) &&
                LocalTime.now().isBefore(todoBlockOfScheduleBlock.endTime)

    private fun formatTime(time: LocalTime): String {
        val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        return formatter.format(time)
    }

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