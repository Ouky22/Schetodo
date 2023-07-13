package com.example.schetodo.feature.schedule.add_edit_schedule_block

import androidx.lifecycle.SavedStateHandle
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.schedule_block.FakeScheduleBlockRepository
import com.example.schetodo.data.todo.FakeTodoRepository
import com.example.schetodo.data.todo_block.FakeTodoBlockRepository
import com.example.schetodo.data.todo_category.FakeTodoCategoryRepository
import com.example.schetodo.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockEvent.*
import com.example.schetodo.ui.navigation.schedule.AddScheduleBlock
import com.example.schetodo.ui.navigation.schedule.EditScheduleBlock
import com.example.schetodo.util.MainDispatcherRule
import com.example.schetodo.util.generalUseCases
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class AddEditScheduleBlockViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val fakeScheduleBlockRepository = FakeScheduleBlockRepository()
    private val fakeTodoRepository = FakeTodoRepository()
    private val fakeTodoCategoryRepository = FakeTodoCategoryRepository()
    private val fakeTodoBlockRepository = FakeTodoBlockRepository()

    @Before
    fun init() {
        Locale.setDefault(Locale.US)
    }

    @Test
    fun `test saving notifications of added schedule block for template`() = runTest {
        val viewModel = createAddEditScheduleBlockViewModel(SavedStateHandle())
        advanceUntilIdle()

        val endTime = LocalTime.of(15, 0)
        viewModel.onEvent(ChangeStartTime(LocalTime.of(12, 0)))
        viewModel.onEvent(ChangeEndTime(endTime))
        viewModel.onEvent(ChangeTodoBlockNotes("test"))
        viewModel.onEvent(ChangeShowNotificationAtBeginning(showNotification = true))
        viewModel.onEvent(ChangeShowNotificationAtBeginning(showNotification = false))
        viewModel.onEvent(ChangeShowNotificationAtEnd(showNotification = true))
        viewModel.onEvent(SaveScheduleBlock)
        advanceUntilIdle()

        assertThat(viewModel.state.showNotificationAtEnd).isTrue()
        assertThat(viewModel.state.showNotificationAtBeginning).isFalse()
        val addedScheduleBlock = fakeScheduleBlockRepository.scheduleBlocks[0]
        assertThat(addedScheduleBlock.notifications.size).isEqualTo(1)
        val notification = addedScheduleBlock.notifications.first()
        assertThat(notification.dateTime).isEqualTo(LocalDateTime.of(LocalDate.now(), endTime))
    }

    @Test
    fun `test saving notifications of edited schedule block for template`() = runTest {
        val scheduleBlock = createTestScheduleBlock(date = null)
        fakeScheduleBlockRepository.insertOrUpdateScheduleBlock(scheduleBlock)
        val savedStateHandle = SavedStateHandle(
            mapOf(EditScheduleBlock.todoBlockIdArg to scheduleBlock.todoBlock.todoBlockId)
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)
        advanceUntilIdle()

        val startTime = LocalTime.of(12, 0)
        viewModel.onEvent(ChangeStartTime(startTime))
        viewModel.onEvent(ChangeEndTime(LocalTime.of(15, 0)))
        viewModel.onEvent(ChangeTodoBlockNotes("test"))
        viewModel.onEvent(ChangeShowNotificationAtBeginning(showNotification = true))
        viewModel.onEvent(ChangeShowNotificationAtEnd(showNotification = true))
        viewModel.onEvent(ChangeShowNotificationAtEnd(showNotification = false))
        viewModel.onEvent(SaveScheduleBlock)
        advanceUntilIdle()

        assertThat(viewModel.state.showNotificationAtEnd).isFalse()
        assertThat(viewModel.state.showNotificationAtBeginning).isTrue()
        val editedScheduleBlock = fakeScheduleBlockRepository.scheduleBlocks[0]
        assertThat(editedScheduleBlock.notifications.size).isEqualTo(1)
        val notification = editedScheduleBlock.notifications.first()
        assertThat(notification.dateTime).isEqualTo(LocalDateTime.of(LocalDate.now(), startTime))
    }

    @Test
    fun `when saving schedule block then also save selected notifications`() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)
        advanceUntilIdle()

        val startTime = LocalTime.of(12, 0)
        viewModel.onEvent(ChangeStartTime(startTime))
        viewModel.onEvent(ChangeEndTime(LocalTime.of(15, 0)))
        viewModel.onEvent(ChangeTodoBlockNotes("test"))
        viewModel.onEvent(ChangeShowNotificationAtBeginning(showNotification = true))
        viewModel.onEvent(ChangeShowNotificationAtEnd(showNotification = true))
        viewModel.onEvent(ChangeShowNotificationAtEnd(showNotification = false))
        viewModel.onEvent(SaveScheduleBlock)
        advanceUntilIdle()

        val addedScheduleBlock =
            fakeScheduleBlockRepository.getScheduleBlocksOnDate(date).first()[0]
        assertThat(addedScheduleBlock.notifications.size).isEqualTo(1)
        val notification = addedScheduleBlock.notifications.first()
        assertThat(notification.dateTime).isEqualTo(LocalDateTime.of(date, startTime))
    }

    @Test
    fun `when saving and categories, date and time are valid then save`() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val category = TodoCategory(1, "", 0, null, "")
        fakeTodoCategoryRepository.insertTodoCategory(category)
        advanceUntilIdle()
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val startTime = LocalTime.of(9, 0)
        val endTime = LocalTime.of(10, 0)
        viewModel.onEvent(ChangeStartTime(startTime))
        viewModel.onEvent(ChangeEndTime(endTime))
        viewModel.onEvent(SelectTodoCategories(listOf(category.categoryId)))
        advanceUntilIdle()
        viewModel.onEvent(SaveScheduleBlock)
        advanceUntilIdle()

        assertThat(viewModel.closeAddEditScheduleBlockScreen.value).isTrue()
    }

    @Test
    fun `when saving and todos, date and time are valid then save`() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val todo = Todo(1, "", TodoPriority.LOW, TodoFlag.DONE, 1)
        fakeTodoRepository.insertTodo(todo)
        advanceUntilIdle()
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val startTime = LocalTime.of(9, 0)
        val endTime = LocalTime.of(10, 0)
        viewModel.onEvent(ChangeStartTime(startTime))
        viewModel.onEvent(ChangeEndTime(endTime))
        viewModel.onEvent(SelectTodos(listOf(todo.todoId)))
        advanceUntilIdle()
        viewModel.onEvent(SaveScheduleBlock)
        advanceUntilIdle()

        assertThat(viewModel.closeAddEditScheduleBlockScreen.value).isTrue()
    }

    @Test
    fun `when saving and notes, date and time are valid then save`() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val startTime = LocalTime.of(9, 0)
        val endTime = LocalTime.of(10, 0)
        viewModel.onEvent(ChangeStartTime(startTime))
        viewModel.onEvent(ChangeEndTime(endTime))
        viewModel.onEvent(ChangeTodoBlockNotes("test"))
        viewModel.onEvent(SaveScheduleBlock)
        advanceUntilIdle()

        assertThat(viewModel.closeAddEditScheduleBlockScreen.value).isTrue()
    }

    @Test
    fun when_saving_and_notes_todos_and_todo_category_not_set_then_do_not_save() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        viewModel.onEvent(ChangeTodoBlockNotes("   \n"))
        viewModel.onEvent(SaveScheduleBlock)
        advanceUntilIdle()

        assertThat(viewModel.closeAddEditScheduleBlockScreen.value).isFalse()
    }

    @Test
    fun when_saving_and_start_time_equals_end_time_then_do_not_save() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(10, 0)
        viewModel.onEvent(ChangeStartTime(startTime))
        viewModel.onEvent(ChangeEndTime(endTime))
        viewModel.onEvent(SaveScheduleBlock)
        advanceUntilIdle()

        assertThat(viewModel.closeAddEditScheduleBlockScreen.value).isFalse()
    }

    @Test
    fun when_saving_and_start_time_bigger_than_end_time_then_do_not_save() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(9, 59)
        viewModel.onEvent(ChangeStartTime(startTime))
        viewModel.onEvent(ChangeEndTime(endTime))
        viewModel.onEvent(SaveScheduleBlock)

        assertThat(viewModel.closeAddEditScheduleBlockScreen.value).isFalse()
    }

    @Test
    fun when_selecting_todos_then_the_corresponding_todo_categories_are_also_added() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, category1.categoryId, "")
        val todo1 = Todo(1, "t1", TodoPriority.HIGH, TodoFlag.DONE, category1.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.HIGH, TodoFlag.DONE, category2.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category1)
        fakeTodoCategoryRepository.insertTodoCategory(category2)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)

        viewModel.onEvent(SelectTodos(listOf(todo1.todoId, todo2.todoId)))
        advanceUntilIdle()

        assertThat(viewModel.state.todoCategories).containsExactly(category1, category2)
    }

    @Test
    fun test_removing_selected_todo() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val todo1 = Todo(1, "t1", TodoPriority.HIGH, TodoFlag.DONE, 1)
        val todo2 = Todo(2, "t2", TodoPriority.HIGH, TodoFlag.DONE, 1)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)

        viewModel.onEvent(SelectTodos(listOf(todo1.todoId, todo2.todoId)))
        advanceUntilIdle()
        viewModel.onEvent(RemoveSelectedTodo(todo1))

        assertThat(viewModel.state.todos).containsExactly(todo2)
    }

    @Test
    fun when_selecting_todo_multiple_times_then_it_occurs_at_maximum_once() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val todo1 = Todo(1, "t1", TodoPriority.HIGH, TodoFlag.DONE, 1)
        val todo2 = Todo(2, "t2", TodoPriority.HIGH, TodoFlag.DONE, 1)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)

        viewModel.onEvent(SelectTodos(listOf(todo1.todoId, todo2.todoId)))
        viewModel.onEvent(SelectTodos(listOf(todo1.todoId)))

        advanceUntilIdle()

        assertThat(viewModel.state.todos).containsExactly(todo1, todo2)
    }

    @Test
    fun test_update_date_event() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to 0L)
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val newDate = LocalDate.of(2023, 12, 31)
        viewModel.onEvent(ChangeDate(newDate))

        assertThat(viewModel.state.date).isEqualTo("Sun 31 Dec, 2023")
    }

    @Test
    fun test_update_end_time_event() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to 0L)
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val newEndTime = LocalTime.of(13, 45)
        viewModel.onEvent(ChangeEndTime(newEndTime))

        assertThat(viewModel.state.endTime).isEqualTo("1:45 PM")
    }

    @Test
    fun test_update_start_time_event() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to 0L)
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        val newStartTime = LocalTime.of(13, 45)
        viewModel.onEvent(ChangeStartTime(newStartTime))

        assertThat(viewModel.state.startTime).isEqualTo("1:45 PM")
    }

    @Test
    fun when_creating_view_model_for_editing_and_valid_id_passed_then_load_schedule_block_data() =
        runTest {
            val scheduleBlock = createTestScheduleBlock()
            fakeScheduleBlockRepository.insertOrUpdateScheduleBlock(scheduleBlock)
            val savedStateHandle = SavedStateHandle(
                mapOf(EditScheduleBlock.todoBlockIdArg to scheduleBlock.todoBlock.todoBlockId)
            )
            val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)
            advanceUntilIdle()

            val state = viewModel.state
            assertThat(state.date).isEqualTo("Wed 15 Feb, 2023")
            assertThat(state.startTime).isEqualTo("1:45 PM")
            assertThat(state.endTime).isEqualTo("3:00 PM")
            assertThat(state.todoCategories).containsExactlyElementsIn(scheduleBlock.todoCategories)
            assertThat(state.todos).containsExactlyElementsIn(scheduleBlock.todos)
            assertThat(state.notes).isEqualTo(scheduleBlock.todoBlock.notes)
            assertThat(state.inEditingMode).isTrue()
            assertThat(state.showNotificationAtBeginning).isTrue()
            assertThat(state.showNotificationAtEnd).isFalse()
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
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

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
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

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
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)

        assertThat(viewModel.state.startTime).isEqualTo("1:45 PM")
        assertThat(viewModel.state.inEditingMode).isFalse()
    }

    @Test
    fun when_creating_view_model_then_load_the_passed_date() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = createAddEditScheduleBlockViewModel(savedStateHandle)
        assertThat(viewModel.state.date).isEqualTo("Wed 15 Feb, 2023")
    }

    @Test
    fun when_creating_view_model_and_no_valid_date_is_passed_then_throw_exception() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to -1)
        )
        assertThrows(Exception::class.java) {
            createAddEditScheduleBlockViewModel(savedStateHandle)
        }
    }

    private fun createAddEditScheduleBlockViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) =
        AddEditScheduleBlockViewModel(
            fakeScheduleBlockRepository,
            fakeTodoRepository,
            fakeTodoCategoryRepository,
            fakeTodoBlockRepository,
            generalUseCases,
            savedStateHandle
        )

    private fun createTestScheduleBlock(
        date: LocalDate? = LocalDate.of(2023, 2, 15)
    ): ScheduleBlock {
        val startTime = LocalTime.of(13, 45)
        val endTime = LocalTime.of(15, 0)
        val todoBlock = TodoBlock(1, "test", date, startTime, endTime, null)
        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, null, "")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.DONE, category1.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.HIGH, TodoFlag.UNDONE, category2.categoryId)
        val notification = Notification(
            1,
            LocalDateTime.of(date ?: LocalDate.now(), startTime),
            todoBlock.todoBlockId
        )
        return ScheduleBlock(
            todoBlock, listOf(todo1, todo2), listOf(category1, category2), listOf(notification)
        )
    }
}