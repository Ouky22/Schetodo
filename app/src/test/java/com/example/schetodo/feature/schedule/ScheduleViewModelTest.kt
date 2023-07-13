package com.example.schetodo.feature.schedule

import com.example.schetodo.data.FakeScheduleTemplateRepository
import com.example.schetodo.data.notification.FakeNotificationRepository
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.schedule_block.FakeScheduleBlockRepository
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.schedule_template.ScheduleTemplate
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.feature.schedule.components.UiScheduleBlock
import com.example.schetodo.feature.schedule.notification.FakeTodoBlockNotificationScheduler
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import com.example.schetodo.feature.schedule.list.ScheduleEvent.*
import com.example.schetodo.feature.schedule.list.ScheduleViewModel
import com.example.schetodo.feature.use_case.ConvertScheduleBlocksToScheduleListItemsUseCase
import com.example.schetodo.feature.use_case.FormatDateUseCase
import com.example.schetodo.feature.use_case.FormatTimeUseCase
import com.example.schetodo.feature.use_case.GeneralUseCases
import com.example.schetodo.util.generalUseCases

@OptIn(ExperimentalCoroutinesApi::class)
internal class ScheduleViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val fakeScheduleBlockRepository = FakeScheduleBlockRepository()
    private val fakeScheduleTemplateRepository = FakeScheduleTemplateRepository()
    private lateinit var viewModel: ScheduleViewModel

    private val testScheduleBlocksDate = LocalDate.now()
    private lateinit var testScheduleBlock1: ScheduleBlock
    private lateinit var testScheduleBlock2: ScheduleBlock

    @Before
    fun init() {
        viewModel = ScheduleViewModel(
            fakeScheduleBlockRepository,
            fakeScheduleTemplateRepository,
            generalUseCases
        )
        createTestScheduleBlock()
    }

    @Test
    fun test_saving_current_schedule_as_template() = runTest {
        fakeScheduleBlockRepository.insertOrUpdateScheduleBlock(testScheduleBlock1)

        val templateName = "template_test"
        viewModel.onEvent(OpenEnterScheduleTemplateNameDialog)
        viewModel.onEvent(ChangeScheduleTemplateName(templateName))
        viewModel.onEvent(SaveCurrentScheduleAsTemplate)
        advanceUntilIdle()

        assertThat(fakeScheduleTemplateRepository.getAll().first()).containsExactly(
            ScheduleTemplate(name = templateName)
        )
        assertThat(fakeScheduleBlockRepository.scheduleBlocks).contains(
            testScheduleBlock1.copy(
                todoBlock = testScheduleBlock1.todoBlock.copy(
                    todoBlockId = 0,
                    templateId = 0,
                    date = null
                )
            )
        )

        assertThat(viewModel.scheduleState.value.showEnterScheduleTemplateNameDialog).isFalse()
    }

    @Test
    fun test_navigating_to_any_date() = runTest {
        var date = LocalDate.of(1996, 12, 31)
        viewModel.onEvent(GoToAnyDate(date))
        assertThat(viewModel.scheduleState.value.currentDate).isEqualTo(date)

        date = LocalDate.of(2030, 1, 1)
        viewModel.onEvent(GoToAnyDate(date))
        assertThat(viewModel.scheduleState.value.currentDate).isEqualTo(date)
    }

    @Test
    fun test_navigating_to_next_and_previous_date() = runTest {
        viewModel.onEvent(GoToNextDate)
        viewModel.onEvent(GoToNextDate)
        viewModel.onEvent(GoToNextDate)
        viewModel.onEvent(GoToPreviousDate)
        viewModel.onEvent(GoToPreviousDate)
        viewModel.onEvent(GoToPreviousDate)
        advanceUntilIdle()

        assertThat(viewModel.scheduleState.value.currentDate).isEqualTo(LocalDate.now())
    }

    @Test
    fun test_navigating_to_next_date() = runTest {
        for (currentDayOffset in 1 until 10) {
            viewModel.onEvent(GoToNextDate)
            advanceUntilIdle()

            assertThat(viewModel.scheduleState.value.currentDate).isEqualTo(
                LocalDate.now().plusDays(currentDayOffset.toLong())
            )
        }
    }

    @Test
    fun test_navigating_to_previous_date() = runTest {
        for (currentDayOffset in 1 until 10) {
            viewModel.onEvent(GoToPreviousDate)
            advanceUntilIdle()

            assertThat(viewModel.scheduleState.value.currentDate).isEqualTo(
                LocalDate.now().minusDays(currentDayOffset.toLong())
            )
        }
    }

    @Test
    fun test_navigating_to_current_date() = runTest {
        viewModel.onEvent(GoToCurrentDate)
        advanceUntilIdle()

        assertThat(viewModel.scheduleState.value.currentDate).isEqualTo(LocalDate.now())
    }

    @Test
    fun when_view_model_initializes_then_load_schedule_blocks_of_current_date() = runTest {
        fakeScheduleBlockRepository.insertOrUpdateScheduleBlock(testScheduleBlock1)

        val viewModel =
            ScheduleViewModel(
                fakeScheduleBlockRepository,
                fakeScheduleTemplateRepository,
                generalUseCases
            )
        advanceUntilIdle()

        val todoBlock = testScheduleBlock1.todoBlock
        val schedule = viewModel.scheduleState.value.schedules[todoBlock.date!!.toEpochDay()]!!

        val scheduleGap1 = schedule[0]
        assertThat(scheduleGap1.startTime).isEqualTo(LocalTime.of(0, 0))
        assertThat(scheduleGap1.endTime).isEqualTo(todoBlock.startTime)

        val uiScheduleBlock = schedule[1] as UiScheduleBlock
        assertThat(uiScheduleBlock.todoBlockId).isEqualTo(todoBlock.todoBlockId)
        assertThat(uiScheduleBlock.categories).containsExactlyElementsIn(
            testScheduleBlock1.todoCategories
        )
        assertThat(uiScheduleBlock.todoDescriptions).containsExactlyElementsIn(
            testScheduleBlock1.todos.map { it.description }
        )
        assertThat(uiScheduleBlock.notes).isEqualTo(todoBlock.notes)

        val scheduleGap2 = schedule[2]
        assertThat(scheduleGap2.startTime).isEqualTo(todoBlock.endTime)
        assertThat(scheduleGap2.endTime).isEqualTo(LocalTime.of(23, 59))
    }

    private fun createTestScheduleBlock() {
        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, null, "")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.DONE, category1.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.DONE, category1.categoryId)
        val todoBlock1 =
            TodoBlock(1, "", testScheduleBlocksDate, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
        val todoBlock2StartTime = LocalTime.of(12, 0)
        val todoBlock2 =
            TodoBlock(2, "", testScheduleBlocksDate, todoBlock2StartTime, LocalTime.of(15, 0), null)
        testScheduleBlock1 =
            ScheduleBlock(todoBlock1, listOf(todo1, todo2), listOf(category1, category2))

        val notificationDateTime = LocalDateTime.of(testScheduleBlocksDate, todoBlock2StartTime)
        val notifications = listOf(Notification(1, notificationDateTime, todoBlock1.todoBlockId))
        testScheduleBlock2 = ScheduleBlock(todoBlock2, listOf(todo1), emptyList(), notifications)
    }
}