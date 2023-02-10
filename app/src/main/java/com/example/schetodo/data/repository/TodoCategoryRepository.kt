package com.example.schetodo.data.repository

import com.example.schetodo.data.entity.TodoCategory
import kotlinx.coroutines.flow.Flow

interface TodoCategoryRepository {
    suspend fun insertTodoCategory(todoCategory: TodoCategory): Long
    suspend fun insertOrUpdateTodoCategory(todoCategory: TodoCategory)
    suspend fun deleteTodoCategory(todoCategory: Int)
    fun getChildTodoCategoriesOf(todoCategoryId: Int?): Flow<List<TodoCategory>>
    fun getTodoCategory(todoCategoryId: Int?): Flow<TodoCategory?>
}