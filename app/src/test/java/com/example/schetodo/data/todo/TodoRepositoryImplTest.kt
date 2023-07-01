package com.example.schetodo.data.todo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import app.cash.turbine.test
import com.example.schetodo.data.user_preferences.FakeUserPreferencesRepository
import com.example.schetodo.data.user_preferences.UserPreferencesRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import org.junit.Test

@ExperimentalCoroutinesApi
internal class TodoRepositoryImplTest {

    private lateinit var fakeTodoDao: TodoDao
    private lateinit var todoRepositoryImpl: TodoRepositoryImpl
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun init() {
        fakeTodoDao = FakeTodoDao()
        userPreferencesRepository = FakeUserPreferencesRepository()
        todoRepositoryImpl = TodoRepositoryImpl(fakeTodoDao, userPreferencesRepository)
    }

    @Test
    fun when_getting_todos_then_todos_marked_for_deletion_are_not_returned() = runTest {
        val todo1 = Todo(1, "t1", TodoPriority.HIGH, TodoFlag.UNDONE, 1)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.IN_PROGRESS, 1)
        val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.RECURRING, 1)
        fakeTodoDao.insertTodo(todo1)
        fakeTodoDao.insertTodo(todo2)
        fakeTodoDao.insertTodo(todo3)

        fakeTodoDao.markTodoForDeletion(todo1.todoId)

        assertThat(todoRepositoryImpl.getTodosOfTodoCategory(1).first())
            .containsExactly(todo2, todo3)
    }

    @Test
    fun when_initializing_repository_then_all_todos_marked_for_deletion_are_deleted() = runTest {
        val todo1 = Todo(1, "t1", TodoPriority.HIGH, TodoFlag.UNDONE, 1)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.IN_PROGRESS, 1)
        val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.RECURRING, 1)
        fakeTodoDao.insertTodo(todo1)
        fakeTodoDao.insertTodo(todo2)
        fakeTodoDao.insertTodo(todo3)

        fakeTodoDao.markTodoForDeletion(todo1.todoId)
        fakeTodoDao.markTodoForDeletion(todo2.todoId)
        val todoRepository = TodoRepositoryImpl(fakeTodoDao, userPreferencesRepository)

        assertThat(todoRepository.getTodosOfTodoCategory(1).first()).containsExactly(todo3)
    }

    @Test
    fun when_filter_is_set_to_only_show_recurring_todos_then_return_recurring_todos_only() =
        runTest {
            val todo1 = Todo(1, "t1", TodoPriority.HIGH, TodoFlag.UNDONE, 1)
            val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.IN_PROGRESS, 1)
            val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.RECURRING, 1)
            fakeTodoDao.insertTodo(todo1)
            fakeTodoDao.insertTodo(todo2)
            fakeTodoDao.insertTodo(todo3)

            val todoFilterSettings = TodoFilterSettings(
                showDoneTodos = false,
                showInProgressTodos = false,
                showUndoneTodos = false,
                showRecurringTodos = true
            )

            val todos = todoRepositoryImpl.getTodosOfTodoCategory(1, todoFilterSettings).first()
            assertThat(todos).containsExactly(todo3)
        }

    @Test
    fun when_null_as_todo_category_id_provided_then_return_flow_with_empty_list() = runTest {
        val todos = todoRepositoryImpl.getTodosOfTodoCategory(null).first()
        assertThat(todos).isEmpty()
    }

    @Test
    fun when_todo_category_id_not_exists_then_return_flow_of_empty_list() = runTest {
        val todo1 = Todo(1, "", TodoPriority.HIGH, TodoFlag.UNDONE, 1)
        val todo2 = Todo(2, "", TodoPriority.LOW, TodoFlag.IN_PROGRESS, 1)
        fakeTodoDao.insertTodo(todo1)
        fakeTodoDao.insertTodo(todo2)

        val todos = todoRepositoryImpl.getTodosOfTodoCategory(2).first()
        assertThat(todos).isEmpty()
    }

    @Test
    fun when_todo_category_id_exist_then_return_flow_of_list_of_its_todos() = runTest {
        val todoCategoryId = 1
        val todo1 = Todo(1, "", TodoPriority.HIGH, TodoFlag.UNDONE, todoCategoryId)
        val todo2 = Todo(2, "", TodoPriority.LOW, TodoFlag.IN_PROGRESS, todoCategoryId)
        val todo3 = Todo(3, "", TodoPriority.MEDIUM, TodoFlag.RECURRING, 2)

        fakeTodoDao.insertTodo(todo1)
        fakeTodoDao.insertTodo(todo2)
        fakeTodoDao.insertTodo(todo3)

        val todos = todoRepositoryImpl.getTodosOfTodoCategory(todoCategoryId).first()
        assertThat(todos).contains(todo1)
        assertThat(todos).contains(todo2)
        assertThat(todos.size).isEqualTo(2)
    }
}