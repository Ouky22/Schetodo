package com.example.schetodo.ui.feature.schedule.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.R
import com.example.schetodo.data.MAX_DATE
import com.example.schetodo.data.MIN_DATE
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.data.schedule_template.ScheduleTemplate
import com.example.schetodo.data.schedule_template.ScheduleTemplateRepository
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.ui.feature.schedule.components.ScheduleGap
import com.example.schetodo.ui.feature.schedule.components.ScheduleListItem
import com.example.schetodo.ui.feature.schedule.components.UiScheduleBlock
import com.example.schetodo.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
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
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.math.abs

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository,
    private val scheduleTemplateRepository: ScheduleTemplateRepository,
    private val todoBlockNotificationScheduler: TodoBlockNotificationScheduler
) : ViewModel() {
    private val _scheduleState = MutableStateFlow(ScheduleState())
    val scheduleState: StateFlow<ScheduleState>
        get() = _scheduleState.asStateFlow()

    private val numberOfSchedulesAroundDate = 4L
    private lateinit var lastDateSchedulesWereLoaded: LocalDate

    init {
        updateCurrentDate(LocalDate.now())
        loadSchedulesForDate(_scheduleState.value.currentDate)
    }

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is GoToNextDate -> goToNextDate()
            is GoToPreviousDate -> goToPreviousDate()
            is GoToCurrentDate -> goToCurrentDate()
            is GoToAnyDate -> goToDate(event.date)
            is UnmarkTodoBlockForDeletion -> onUnmarkTodoBlockForDeletion(event.todoBlockId)
            is SaveCurrentScheduleAsTemplate -> onSaveCurrentScheduleAsTemplate(event.templateName)
        }
    }

    private fun onSaveCurrentScheduleAsTemplate(templateName: String) {
        viewModelScope.launch {
            val templateId =
                scheduleTemplateRepository.insert(ScheduleTemplate(name = templateName))

            scheduleBlockRepository.getScheduleBlocksOnDate(_scheduleState.value.currentDate)
                .first()
                .filter { !it.todoBlock.markedForDeletion }
                .map { scheduleBlock ->
                    val todoBlock = scheduleBlock.todoBlock.copy(
                        todoBlockId = 0,
                        templateId = templateId.toInt(),
                        date = null
                    )
                    val notifications = scheduleBlock.notifications.map {
                        it.copy(notificationId = 0)
                    }
                    scheduleBlock.copy(
                        todoBlock = todoBlock,
                        notifications = notifications
                    )
                }
                .forEach { scheduleBlock ->
                    scheduleBlockRepository.insertOrUpdateScheduleBlock(scheduleBlock)
                }
        }
    }

    private fun onUnmarkTodoBlockForDeletion(todoBlockId: Int) {
        viewModelScope.launch {
            scheduleBlockRepository.unmarkTodoBlockForDeletion(todoBlockId)
            todoBlockNotificationScheduler.scheduleNextNotificationIfExists()
        }
    }

    private fun goToCurrentDate() {
        goToDate(LocalDate.now())
    }

    private fun goToPreviousDate() {
        goToDate(_scheduleState.value.currentDate.minusDays(1))
    }

    private fun goToNextDate() {
        goToDate(_scheduleState.value.currentDate.plusDays(1))
    }

    private fun goToDate(date: LocalDate) {
        updateCurrentDate(date)

        val differenceToLastDateSchedulesWereLoaded = abs(
            ChronoUnit.DAYS.between(
                _scheduleState.value.currentDate,
                lastDateSchedulesWereLoaded
            )
        )
        if (differenceToLastDateSchedulesWereLoaded >= numberOfSchedulesAroundDate / 2)
            loadSchedulesForDate(date)
    }

    private fun loadSchedulesForDate(anchorDate: LocalDate) {
        val maxDayOffset = numberOfSchedulesAroundDate / 2

        viewModelScope.launch {
            for (dayOffset in -maxDayOffset..maxDayOffset) {
                val date = anchorDate.plusDays(dayOffset)
                val dateStamp = date.toEpochDay()

                val scheduleNotAlreadyLoaded = _scheduleState.value.schedules[dateStamp] == null
                if (scheduleNotAlreadyLoaded) {
                    launch {
                        scheduleBlockRepository.getScheduleBlocksOnDate(date)
                            .map { convertToSchedule(it) }
                            .collect { scheduleBlocks ->
                                val schedules = HashMap(
                                    _scheduleState.value.schedules + (dateStamp to scheduleBlocks)
                                )
                                _scheduleState.value =
                                    _scheduleState.value.copy(schedules = schedules)
                            }
                    }
                }
            }
        }

        lastDateSchedulesWereLoaded = _scheduleState.value.currentDate
    }

    private fun convertToSchedule(scheduleBlocks: List<ScheduleBlock>): List<ScheduleListItem> {
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
        _scheduleState.value = scheduleState.value.copy(
            currentDateString = date.format(
                DateTimeFormatter.ofPattern("EEE dd LLL, yyyy", Locale.getDefault())
            ),
            currentDate = date,
            canNavigateToNextDate = date.isBefore(MAX_DATE),
            canNavigateToPreviousDate = date.isAfter(MIN_DATE)
        )
    }
}