package com.example.schetodo.data.todo

import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodo(todo: Todo)
    suspend fun insertOrUpdateTodo(todo: Todo)
    suspend fun deleteTodoById(todoId: Int)
    suspend fun getTodoById(todoId: Int): Flow<Todo?>
    fun getTodosInProgress(): Flow<List<Todo>>
    suspend fun updateTodo(todo: Todo)
    suspend fun setTodoFilterSettings(todoFilterSettings: TodoFilterSettings)
    fun getTodosOfTodoCategory(
        todoCategoryId: Int?,
        todoFilterSettings: TodoFilterSettings
    ): Flow<List<Todo>>

    /**
     * This method uses the user's preferred todos filter settings to only return the desired todos
     */
    fun getTodosOfTodoCategory(todoCategoryId: Int?): Flow<List<Todo>>
    fun getTodoFilterSettings(): Flow<TodoFilterSettings>
    suspend fun markTodoForDeletion(todoId: Int)
    suspend fun unmarkTodoForDeletion(todoId: Int)
    suspend fun markAllTodosOfCategoryForDeletion(todoCategoryId: Int)
    suspend fun unmarkAllTodosOfCategoryForDeletion(todoCategoryId: Int)
}