package com.example.schetodo.data.todo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
) : TodoRepository {

    override suspend fun insertTodo(todo: Todo) {
        todoDao.insertTodo(todo)
    }

    override suspend fun insertOrUpdateTodo(todo: Todo) {
        todoDao.insertOrUpdateTodo(todo)
    }

    override suspend fun deleteTodoById(todoId: Int) {
        todoDao.deleteTodoById(todoId)
    }

    override suspend fun getTodoById(todoId: Int): Flow<Todo?> =
        todoDao.getTodoById(todoId)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getTodosOfTodoCategory(
        todoCategoryId: Int?,
        todoFilterSettings: TodoFilterSettings
    ): Flow<List<Todo>> {
        // all todos must have a TodoCategory, that's why an empty list is returned if no todoCategoryId is passed
        return if (todoCategoryId == null)
            flow { emit(emptyList()) }
        else
            todoDao.getAllTodosOfTodoCategory(todoCategoryId)
                .mapLatest { todos ->
                    todos.filter {
                        it.flag == TodoFlag.DONE && todoFilterSettings.showDoneTodos
                                || it.flag == TodoFlag.UNDONE && todoFilterSettings.showUndoneTodos
                                || it.flag == TodoFlag.IN_PROGRESS && todoFilterSettings.showInProgressTodos
                                || it.flag == TodoFlag.RECURRING && todoFilterSettings.showRecurringTodos
                    }
                }
    }

    override fun getTodosInProgress(): Flow<List<Todo>> =
        todoDao.getAllTodosWithFlag(TodoFlag.IN_PROGRESS)

    override suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo)
    }
}

data class TodoFilterSettings(
    val showRecurringTodos: Boolean = true,
    val showUndoneTodos: Boolean = true,
    val showInProgressTodos: Boolean = true,
    val showDoneTodos: Boolean = false
)