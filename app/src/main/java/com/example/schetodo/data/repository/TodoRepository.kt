package com.example.schetodo.data.repository

import com.example.schetodo.data.entity.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodo(todo: Todo)
    suspend fun insertOrUpdateTodo(todo: Todo)
    suspend fun deleteTodoById(todoId: Int)
    suspend fun getTodoById(todoId: Int) : Flow<Todo?>
    fun getTodosOfTodoCategory(todoCategoryId: Int?): Flow<List<Todo>>
    fun getTodosInProgress(): Flow<List<Todo>>
}