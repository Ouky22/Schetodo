package com.example.schetodo.data.todo

import com.example.schetodo.data.user_preferences.UserPreferencesRepository
import com.example.schetodo.di.CoroutineScopeModule.ApplicationCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao,
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope
) : TodoRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.deleteAllTodosMarkedForDeletion()
        }
    }

    override suspend fun insertTodo(todo: Todo) {
        applicationCoroutineScope.launch {
            todoDao.insertTodo(todo)
        }.join()
    }

    override suspend fun insertOrUpdateTodo(todo: Todo) {
        applicationCoroutineScope.launch {
            todoDao.insertOrUpdateTodo(todo)
        }.join()
    }

    override suspend fun deleteTodoById(todoId: Int) {
        applicationCoroutineScope.launch {
            todoDao.deleteTodoById(todoId)
        }.join()
    }

    override suspend fun markTodoForDeletion(todoId: Int) {
        applicationCoroutineScope.launch {
            todoDao.markTodoForDeletion(todoId)
        }.join()
    }

    override suspend fun unmarkTodoForDeletion(todoId: Int) {
        applicationCoroutineScope.launch {
            todoDao.unmarkTodoForDeletion(todoId)
        }.join()
    }

    override suspend fun markAllTodosOfCategoryForDeletion(todoCategoryId: Int) {
        applicationCoroutineScope.launch {
            todoDao.markAllTodosOfCategoryForDeletion(todoCategoryId)
        }.join()
    }

    override suspend fun unmarkAllTodosOfCategoryForDeletion(todoCategoryId: Int) {
        applicationCoroutineScope.launch {
            todoDao.unmarkAllTodosOfCategoryForDeletion(todoCategoryId)
        }.join()
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
                    todos.filter { todo ->
                        todoNotFilteredOut(todo, todoFilterSettings)
                    }
                }
    }

    override fun getTodosOfTodoCategory(todoCategoryId: Int?): Flow<List<Todo>> {
        // all todos must have a TodoCategory, that's why an empty list is returned if no todoCategoryId is passed
        return if (todoCategoryId == null)
            flow { emit(emptyList()) }
        else
            combine(
                todoDao.getAllTodosOfTodoCategory(todoCategoryId),
                userPreferencesRepository.todoFilterSettingsPreferences
            ) { todos: List<Todo>, todoFilterSettings: TodoFilterSettings ->
                todos.filter { todo ->
                    todoNotFilteredOut(todo, todoFilterSettings)
                }
            }
    }

    override suspend fun setTodoFilterSettings(todoFilterSettings: TodoFilterSettings) {
        applicationCoroutineScope.launch {
            userPreferencesRepository.setTodoFilterSettings(todoFilterSettings)
        }.join()
    }

    override fun getTodoFilterSettings() =
        userPreferencesRepository.todoFilterSettingsPreferences

    override fun getTodosInProgress(): Flow<List<Todo>> =
        todoDao.getAllTodosWithFlag(TodoFlag.IN_PROGRESS)

    override suspend fun updateTodo(todo: Todo) {
        applicationCoroutineScope.launch {
            todoDao.updateTodo(todo)
        }.join()
    }

    private fun todoNotFilteredOut(todo: Todo, todoFilterSettings: TodoFilterSettings) =
        todo.flag == TodoFlag.DONE && todoFilterSettings.showDoneTodos
                || todo.flag == TodoFlag.UNDONE && todoFilterSettings.showUndoneTodos
                || todo.flag == TodoFlag.IN_PROGRESS && todoFilterSettings.showInProgressTodos
                || todo.flag == TodoFlag.RECURRING && todoFilterSettings.showRecurringTodos
}

data class TodoFilterSettings(
    val showRecurringTodos: Boolean = true,
    val showUndoneTodos: Boolean = true,
    val showInProgressTodos: Boolean = true,
    val showDoneTodos: Boolean = true
)