package com.example.schetodo.feature.schedule.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.MAX_DATE
import com.example.schetodo.data.MIN_DATE
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.data.schedule_template.ScheduleTemplate
import com.example.schetodo.data.schedule_template.ScheduleTemplateRepository
import com.example.schetodo.feature.schedule.notification.TodoBlockNotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlinx.coroutines.flow.*
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.math.abs
import com.example.schetodo.feature.schedule.list.ScheduleEvent.*
import com.example.schetodo.feature.use_case.GeneralUseCases


@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository,
    private val scheduleTemplateRepository: ScheduleTemplateRepository,
    private val todoBlockNotificationScheduler: TodoBlockNotificationScheduler,
    private val generalUseCases: GeneralUseCases
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
                            .map { generalUseCases.convertScheduleBlocksToScheduleListItems(it) }
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

    private fun updateCurrentDate(date: LocalDate) {
        _scheduleState.value = scheduleState.value.copy(
            currentDateString = generalUseCases.formatDate(date),
            currentDate = date,
            canNavigateToNextDate = date.isBefore(MAX_DATE),
            canNavigateToPreviousDate = date.isAfter(MIN_DATE)
        )
    }
}