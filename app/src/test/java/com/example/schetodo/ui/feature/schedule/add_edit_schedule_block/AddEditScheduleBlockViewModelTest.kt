package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import androidx.lifecycle.SavedStateHandle
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.schedule_block.FakeScheduleBlockRepository
import com.example.schetodo.data.todo.FakeTodoRepository
import com.example.schetodo.ui.navigation.schedule.AddScheduleBlock
import com.example.schetodo.ui.navigation.schedule.EditScheduleBlock
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class AddEditScheduleBlockViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val fakeScheduleBlockRepository = FakeScheduleBlockRepository()
    private val fakeTodoRepository = FakeTodoRepository()

    @Before
    fun init() {
        Locale.setDefault(Locale.US)
    }

    @Test
    fun when_selecting_element_multiple_times_then_it_occurs_at_maximum_once() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = AddEditScheduleBlockViewModel(
            fakeScheduleBlockRepository,
            fakeTodoRepository,
            savedStateHandle
        )

        val todo1 = Todo(1, "t1", TodoPriority.HIGH, TodoFlag.DONE, 1)
        val todo2 = Todo(2, "t2", TodoPriority.HIGH, TodoFlag.DONE, 1)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)

        viewModel.onEvent(AddEditScheduleBlockEvent.TodosSelected(listOf(todo1.todoId, todo2.todoId)))
        viewModel.onEvent(AddEditScheduleBlockEvent.TodosSelected(listOf(todo1.todoId)))

        advanceUntilIdle()

        assertThat(viewModel.state.todos).containsExactly(todo1, todo2)
    }

    @Test
    fun test_update_date_event() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to 0L)
        )
        val viewModel = AddEditScheduleBlockViewModel(
            fakeScheduleBlockRepository,
            fakeTodoRepository,
            savedStateHandle
        )

        val newDate = LocalDate.of(2023, 12, 31)
        viewModel.onEvent(AddEditScheduleBlockEvent.ChangeDate(newDate))

        assertThat(viewModel.state.date).isEqualTo("Sun 31 Dec, 2023")
    }

    @Test
    fun test_update_end_time_event() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to 0L)
        )
        val viewModel = AddEditScheduleBlockViewModel(
            fakeScheduleBlockRepository,
            fakeTodoRepository,
            savedStateHandle
        )

        val newEndTime = LocalTime.of(13, 45)
        viewModel.onEvent(AddEditScheduleBlockEvent.ChangeEndTime(newEndTime))

        assertThat(viewModel.state.endTime).isEqualTo("1:45 PM")
    }

    @Test
    fun test_update_start_time_event() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to 0L)
        )
        val viewModel = AddEditScheduleBlockViewModel(
            fakeScheduleBlockRepository,
            fakeTodoRepository,
            savedStateHandle
        )

        val newStartTime = LocalTime.of(13, 45)
        viewModel.onEvent(AddEditScheduleBlockEvent.ChangeStartTime(newStartTime))

        assertThat(viewModel.state.startTime).isEqualTo("1:45 PM")
    }

    @Test
    fun when_creating_view_model_for_editing_and_valid_id_passed_then_load_schedule_block_data() =
        runTest {
            val scheduleBlock = createTestScheduleBlock()
            fakeScheduleBlockRepository.insertScheduleBlock(scheduleBlock)
            val savedStateHandle = SavedStateHandle(
                mapOf(EditScheduleBlock.todoBlockIdArg to scheduleBlock.todoBlock.todoBlockId)
            )
            val viewModel = AddEditScheduleBlockViewModel(
                fakeScheduleBlockRepository, fakeTodoRepository, savedStateHandle
            )
            advanceUntilIdle()

            val state = viewModel.state
            assertThat(state.date).isEqualTo("Wed 15 Feb, 2023")
            assertThat(state.startTime).isEqualTo("1:45 PM")
            assertThat(state.endTime).isEqualTo("3:00 PM")
            assertThat(state.todoCategories).containsExactlyElementsIn(scheduleBlock.todoCategories)
            assertThat(state.todos).containsExactlyElementsIn(scheduleBlock.todos)
            assertThat(state.notes).isEqualTo(scheduleBlock.todoBlock.notes)
            assertThat(state.inEditingMode).isTrue()
        }

    @Test
    fun when_creating_view_model_for_adding_and_start_and_time_passed_then_load_times() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val startTime = LocalTime.of(13, 45)
        val endTime = LocalTime.of(15, 0)
        val savedStateHandle = SavedStateHandle(
            mapOf(
                AddScheduleBlock.dateStampArg to date.toEpochDay(),
                AddScheduleBlock.startTimeStampArg to startTime.toSecondOfDay(),
                AddScheduleBlock.endTimeStampArg to endTime.toSecondOfDay()
            )
        )
        val viewModel = AddEditScheduleBlockViewModel(
            fakeScheduleBlockRepository,
            fakeTodoRepository,
            savedStateHandle
        )

        assertThat(viewModel.state.startTime).isEqualTo("1:45 PM")
        assertThat(viewModel.state.endTime).isEqualTo("3:00 PM")
        assertThat(viewModel.state.inEditingMode).isFalse()
    }

    @Test
    fun when_creating_view_model_for_adding_and_end_time_passed_then_load_end_time() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val endTime = LocalTime.of(13, 45)
        val savedStateHandle = SavedStateHandle(
            mapOf(
                AddScheduleBlock.dateStampArg to date.toEpochDay(),
                AddScheduleBlock.endTimeStampArg to endTime.toSecondOfDay()
            )
        )
        val viewModel = AddEditScheduleBlockViewModel(
            fakeScheduleBlockRepository,
            fakeTodoRepository,
            savedStateHandle
        )

        assertThat(viewModel.state.endTime).isEqualTo("1:45 PM")
        assertThat(viewModel.state.inEditingMode).isFalse()
    }

    @Test
    fun when_creating_view_model_for_adding_and_start_time_passed_then_load_start_time() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val startTime = LocalTime.of(13, 45)
        val savedStateHandle = SavedStateHandle(
            mapOf(
                AddScheduleBlock.dateStampArg to date.toEpochDay(),
                AddScheduleBlock.startTimeStampArg to startTime.toSecondOfDay()
            )
        )
        val viewModel = AddEditScheduleBlockViewModel(
            fakeScheduleBlockRepository,
            fakeTodoRepository,
            savedStateHandle
        )

        assertThat(viewModel.state.startTime).isEqualTo("1:45 PM")
        assertThat(viewModel.state.inEditingMode).isFalse()
    }

    @Test
    fun when_creating_view_model_then_load_the_passed_date() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = AddEditScheduleBlockViewModel(
            fakeScheduleBlockRepository,
            fakeTodoRepository,
            savedStateHandle
        )
        assertThat(viewModel.state.date).isEqualTo("Wed 15 Feb, 2023")
    }

    @Test
    fun when_creating_view_model_and_no_valid_date_is_passed_then_throw_exception() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to -1)
        )
        assertThrows(Exception::class.java) {
            AddEditScheduleBlockViewModel(
                fakeScheduleBlockRepository,
                fakeTodoRepository,
                savedStateHandle
            )
        }
    }

    @Test
    fun when_creating_view_model_and_no_date_is_passed_then_throw_exception() = runTest {
        assertThrows(Exception::class.java) {
            AddEditScheduleBlockViewModel(
                fakeScheduleBlockRepository,
                fakeTodoRepository,
                SavedStateHandle()
            )
        }
    }

    private fun createTestScheduleBlock(): ScheduleBlock {
        val date = LocalDate.of(2023, 2, 15)
        val startTime = LocalTime.of(13, 45)
        val endTime = LocalTime.of(15, 0)
        val todoBlock = TodoBlock(1, "test", date, startTime, endTime, null)
        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, null, "")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.DONE, category1.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.HIGH, TodoFlag.UNDONE, category2.categoryId)
        return ScheduleBlock(
            todoBlock, listOf(todo1, todo2), listOf(category1, category2)
        )
    }
}