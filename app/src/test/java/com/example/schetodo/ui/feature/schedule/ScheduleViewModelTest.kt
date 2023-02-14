package com.example.schetodo.ui.feature.schedule

import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.feature.schedule.list.ScheduleViewModel
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

    @Test
    fun when_view_model_initializes_then_load_schedule_blocks_of_current_date() = runTest {
        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, null, "")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.DONE, category1.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.DONE, category1.categoryId)
        val categories = listOf(category1, category2)
        val todos = listOf(todo1, todo2)
        val todoBlock = TodoBlock(1, "", LocalDate.now(), LocalTime.now(), LocalTime.now(), null)
        val scheduleBlock = ScheduleBlock(todoBlock, todos, categories)
        fakeScheduleBlockRepository.insertScheduleBlock(scheduleBlock)

        val viewModel = ScheduleViewModel(fakeScheduleBlockRepository)
        advanceUntilIdle()

        val uiScheduleBlocks = viewModel.scheduleState.value.uiScheduleBlocks
        assertThat(uiScheduleBlocks.size).isEqualTo(1)

        val uiScheduleBlock = uiScheduleBlocks.first()
        assertThat(uiScheduleBlock.id).isEqualTo(todoBlock.todoBlockId)
        assertThat(uiScheduleBlock.categories).containsAtLeastElementsIn(categories)
        assertThat(uiScheduleBlock.todoDescriptions).containsAtLeastElementsIn(todos.map { it.description })
        assertThat(uiScheduleBlock.notes).isEqualTo(todoBlock.notes)
        // TODO check start time, end time and duration string
    }
}