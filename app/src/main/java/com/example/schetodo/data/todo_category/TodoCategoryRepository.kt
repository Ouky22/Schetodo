package com.example.schetodo.data.todo_category

import kotlinx.coroutines.flow.Flow

interface TodoCategoryRepository {
    suspend fun insertTodoCategory(todoCategory: TodoCategory): Long
    suspend fun insertOrUpdateTodoCategory(todoCategory: TodoCategory)
    suspend fun deleteTodoCategory(todoCategory: Int)
    fun getChildTodoCategoriesOf(todoCategoryId: Int?): Flow<List<TodoCategory>>
    fun getTodoCategory(todoCategoryId: Int?): Flow<TodoCategory?>
}