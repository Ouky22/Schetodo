package com.example.schetodo.data.repository

import com.example.schetodo.data.entity.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodo(todo: Todo)
    fun getTodosOfTodoCategory(todoCategoryId: Int?): Flow<List<Todo>>
}