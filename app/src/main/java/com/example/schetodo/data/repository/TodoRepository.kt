package com.example.schetodo.data.repository

import com.example.schetodo.data.entity.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodo(todo: Todo)
    suspend fun getTodoById(todoId: Int) : Flow<Todo?>
    fun getTodosOfTodoCategory(todoCategoryId: Int?): Flow<List<Todo>>
}