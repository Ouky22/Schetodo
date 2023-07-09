package com.example.schetodo.feature.todos.check_off_todos

import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.FakeTodoCategoryRepository
import com.example.schetodo.data.todo.FakeTodoRepository
import com.example.schetodo.feature.todos.check_off_todos.CheckOffTodosEvent.*
import com.example.schetodo.feature.todos.check_off_todos.CheckOffTodosViewModel
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
    fun test_undoing_check_off_todos() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "icon")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)
        fakeTodoRepository.insertTodo(todo3)
        val viewModel = CheckOffTodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        viewModel.onEvent(MarkTodoForCheckOff(todo1.todoId))
        viewModel.onEvent(CheckOffMarkedTodos)
        viewModel.onEvent(CheckOffTodo(todo2.todoId))
        viewModel.onEvent(UndoCheckOffTodos) // should only affect last check off of todo2

        val todos = fakeTodoRepository.getTodosOfTodoCategory(category.categoryId).first()
        assertThat(todos.size).isEqualTo(3)
        assertThat(todos).contains(todo1.copy(flag = TodoFlag.DONE))
        assertThat(todos).contains(todo2)
        assertThat(todos).contains(todo3)
    }

    @Test
    fun test_undoing_mark_todo_as_undone() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "icon")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)
        val viewModel = CheckOffTodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        viewModel.onEvent(MarkTodoAsUndone(todo1.todoId))
        viewModel.onEvent(MarkTodoAsUndone(todo2.todoId))
        viewModel.onEvent(UndoMarkTodoAsUndone) // should only affect last undo of todo2

        val todos = fakeTodoRepository.getTodosOfTodoCategory(category.categoryId).first()
        assertThat(todos.size).isEqualTo(2)
        assertThat(todos).contains(todo1.copy(flag = TodoFlag.UNDONE))
        assertThat(todos).contains(todo2)
    }

    @Test
    fun when_todo_is_checked_off_then_is_is_marked_as_done() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "icon")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)
        fakeTodoRepository.insertTodo(todo3)
        val viewModel = CheckOffTodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        viewModel.onEvent(CheckOffTodo(todo1.todoId))
        viewModel.onEvent(CheckOffTodo(todo2.todoId))

        val todos = fakeTodoRepository.getTodosOfTodoCategory(category.categoryId).first()
        assertThat(todos.size).isEqualTo(3)
        assertThat(todos).contains(todo1.copy(flag = TodoFlag.DONE))
        assertThat(todos).contains(todo2.copy(flag = TodoFlag.DONE))
        assertThat(todos).contains(todo3)
    }

    @Test
    fun test_mark_todo_as_undone() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "icon")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)
        val viewModel = CheckOffTodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        viewModel.onEvent(MarkTodoAsUndone(todo1.todoId))

        val todos = fakeTodoRepository.getTodosOfTodoCategory(category.categoryId).first()
        assertThat(todos.size).isEqualTo(2)
        assertThat(todos).contains(todo1.copy(flag = TodoFlag.UNDONE))
        assertThat(todos).contains(todo2)
    }

    @Test
    fun when_todos_are_marked_and_checked_off_then_they_are_marked_as_done() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "icon")
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)
        val viewModel = CheckOffTodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        viewModel.onEvent(MarkTodoForCheckOff(todo1.todoId))
        viewModel.onEvent(UndoMarkTodoForCheckOff(todo1.todoId))
        viewModel.onEvent(MarkTodoForCheckOff(todo1.todoId))
        viewModel.onEvent(UndoMarkTodoForCheckOff(todo2.todoId))
        viewModel.onEvent(CheckOffMarkedTodos)

        val doneTodos = fakeTodoRepository.getTodosOfTodoCategory(category.categoryId).first()
            .filter { it.flag == TodoFlag.DONE }
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