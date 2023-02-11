package com.example.schetodo.ui.feature.todos.check_off_todos

import com.example.schetodo.data.entity.Todo
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.data.repository.FakeTodoCategoryRepository
import com.example.schetodo.data.repository.FakeTodoRepository
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class CheckOffTodosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val fakeTodoRepository = FakeTodoRepository()
    private val fakeTodoCategoryRepository = FakeTodoCategoryRepository()


    @Test
    fun when_todos_are_checked_off_then_their_flag_is_done() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "icon")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)
        val viewModel = CheckOffTodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        viewModel.onEvent(CheckOffTodosEvent.MarkTodoForCheckOff(todo1.todoId))
        viewModel.onEvent(CheckOffTodosEvent.UndoMarkTodoForCheckOff(todo1.todoId))
        viewModel.onEvent(CheckOffTodosEvent.MarkTodoForCheckOff(todo1.todoId))
        viewModel.onEvent(CheckOffTodosEvent.UndoMarkTodoForCheckOff(todo2.todoId))
        viewModel.onEvent(CheckOffTodosEvent.CheckOffMarkedTodos)

        val doneTodos = fakeTodoRepository.getTodosOfTodoCategory(1).first().filter {
            it.flag == TodoFlag.DONE
        }
        assertThat(doneTodos.size).isEqualTo(1)
        assertThat(doneTodos).contains(todo1.copy(flag = TodoFlag.DONE))
    }

    @Test
    fun when_view_model_initializes_then_it_loads_all_todos_in_progress() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "icon")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.DONE, category.categoryId)
        val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.UNDONE, category.categoryId)
        val todo4 = Todo(3, "t4", TodoPriority.LOW, TodoFlag.RECURRING, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)
        fakeTodoRepository.insertTodo(todo3)
        fakeTodoRepository.insertTodo(todo4)
        val viewModel = CheckOffTodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        val todoCategoryTodoPairs = viewModel.todosInProgress.first()
        assertThat(todoCategoryTodoPairs.size).isEqualTo(category.categoryId)
        assertThat(todoCategoryTodoPairs.map { it.todo }).contains(todo1)
    }
}