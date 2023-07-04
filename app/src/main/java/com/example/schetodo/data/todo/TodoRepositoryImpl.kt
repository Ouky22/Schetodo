package com.example.schetodo.data.todo

import com.example.schetodo.data.user_preferences.UserPreferencesRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : TodoRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            todoDao.deleteAllTodosMarkedForDeletion()
        }
    }

    override suspend fun insertTodo(todo: Todo) {
        todoDao.insertTodo(todo)
    }

    override suspend fun insertOrUpdateTodo(todo: Todo) {
        todoDao.insertOrUpdateTodo(todo)
    }

    override suspend fun deleteTodoById(todoId: Int) {
        todoDao.deleteTodoById(todoId)
    }

    override suspend fun markTodoForDeletion(todoId: Int) {
        todoDao.markTodoForDeletion(todoId)
    }

    override suspend fun unmarkTodoForDeletion(todoId: Int) {
        todoDao.unmarkTodoForDeletion(todoId)
    }

    override suspend fun markAllTodosOfCategoryForDeletion(todoCategoryId: Int) {
        todoDao.markAllTodosOfCategoryForDeletion(todoCategoryId)
    }

    override suspend fun unmarkAllTodosOfCategoryForDeletion(todoCategoryId: Int) {
        todoDao.unmarkAllTodosOfCategoryForDeletion(todoCategoryId)
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
        userPreferencesRepository.setTodoFilterSettings(todoFilterSettings)
    }

    override fun getTodoFilterSettings() =
        userPreferencesRepository.todoFilterSettingsPreferences

    override fun getTodosInProgress(): Flow<List<Todo>> =
        todoDao.getAllTodosWithFlag(TodoFlag.IN_PROGRESS)

    override suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo)
    }

    private fun todoNotFilteredOut(todo: Todo, todoFilterSettings: TodoFilterSettings) =
        !todo.markedForDeletion
                && (todo.flag == TodoFlag.DONE && todoFilterSettings.showDoneTodos
                || todo.flag == TodoFlag.UNDONE && todoFilterSettings.showUndoneTodos
                || todo.flag == TodoFlag.IN_PROGRESS && todoFilterSettings.showInProgressTodos
                || todo.flag == TodoFlag.RECURRING && todoFilterSettings.showRecurringTodos
                )
}

data class TodoFilterSettings(
    val showRecurringTodos: Boolean = true,
    val showUndoneTodos: Boolean = true,
    val showInProgressTodos: Boolean = true,
    val showDoneTodos: Boolean = true
)