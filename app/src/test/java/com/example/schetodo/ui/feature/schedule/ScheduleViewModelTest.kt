package com.example.schetodo.ui.feature.schedule

import com.example.schetodo.data.notification.FakeNotificationRepository
import com.example.schetodo.data.schedule_block.FakeScheduleBlockRepository
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.feature.schedule.list.ScheduleEvent.*
import com.example.schetodo.ui.feature.schedule.list.ScheduleViewModel
import com.example.schetodo.ui.feature.schedule.list.UiScheduleBlock
import com.example.schetodo.ui.feature.schedule.notification.FakeTodoBlockNotificationScheduler
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
internal class ScheduleViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val fakeScheduleBlockRepository = FakeScheduleBlockRepository()
    private val fakeNotificationRepository = FakeNotificationRepository()
    private val fakeTodoBlockNotificationScheduler =
        FakeTodoBlockNotificationScheduler(fakeNotificationRepository)

    @Test
    fun test_navigating_to_any_date() = runTest {
        val viewModel =
            ScheduleViewModel(fakeScheduleBlockRepository, fakeTodoBlockNotificationScheduler)

        var date = LocalDate.of(1996, 12, 31)
        viewModel.onEvent(GoToAnyDate(date))
        assertThat(viewModel.scheduleState.value.currentDate).isEqualTo(date)

        date = LocalDate.of(2030, 1, 1)
        viewModel.onEvent(GoToAnyDate(date))
        assertThat(viewModel.scheduleState.value.currentDate).isEqualTo(date)
    }

    @Test
    fun test_navigating_to_next_and_previous_date() = runTest {
        val viewModel =
            ScheduleViewModel(fakeScheduleBlockRepository, fakeTodoBlockNotificationScheduler)

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
        val viewModel =
            ScheduleViewModel(fakeScheduleBlockRepository, fakeTodoBlockNotificationScheduler)

        for (currentDayOffset in 1 until 10) {
            viewModel.onEvent(GoToNextDate)
            advanceUntilIdle()

            val stamp = LocalDate.now().plusDays(currentDayOffset.toLong())

            assertThat(viewModel.scheduleState.value.currentDate).isEqualTo(stamp)
        }
    }

    @Test
    fun test_navigating_to_previous_date() = runTest {
        val viewModel =
            ScheduleViewModel(fakeScheduleBlockRepository, fakeTodoBlockNotificationScheduler)

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
        val viewModel =
            ScheduleViewModel(fakeScheduleBlockRepository, fakeTodoBlockNotificationScheduler)

        viewModel.onEvent(GoToCurrentDate)
        advanceUntilIdle()

        assertThat(viewModel.scheduleState.value.currentDate).isEqualTo(LocalDate.now())
    }

    @Test
    fun when_view_model_initializes_then_load_schedule_blocks_of_current_date() = runTest {
        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, null, "")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.DONE, category1.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.DONE, category1.categoryId)
        val categories = listOf(category1, category2)
        val todos = listOf(todo1, todo2)
        val date = LocalDate.now()
        val todoBlock =
            TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
        val scheduleBlock = ScheduleBlock(todoBlock, todos, categories)
        fakeScheduleBlockRepository.insertOrUpdateScheduleBlock(scheduleBlock)

        val viewModel =
            ScheduleViewModel(fakeScheduleBlockRepository, fakeTodoBlockNotificationScheduler)
        advanceUntilIdle()

        val schedule = viewModel.scheduleState.value.schedules[date.toEpochDay()]!!

        val scheduleGap1 = schedule[0]
        assertThat(scheduleGap1.startTime).isEqualTo(LocalTime.of(0, 0))
        assertThat(scheduleGap1.endTime).isEqualTo(todoBlock.startTime)

        val uiScheduleBlock = schedule[1] as UiScheduleBlock
        assertThat(uiScheduleBlock.todoBlockId).isEqualTo(todoBlock.todoBlockId)
        assertThat(uiScheduleBlock.categories).containsExactlyElementsIn(categories)
        assertThat(uiScheduleBlock.todoDescriptions).containsExactlyElementsIn(todos.map { it.description })
        assertThat(uiScheduleBlock.notes).isEqualTo(todoBlock.notes)

        val scheduleGap2 = schedule[2]
        assertThat(scheduleGap2.startTime).isEqualTo(todoBlock.endTime)
        assertThat(scheduleGap2.endTime).isEqualTo(LocalTime.of(23, 59))
    }
}