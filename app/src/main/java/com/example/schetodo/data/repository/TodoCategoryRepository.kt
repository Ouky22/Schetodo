package com.example.schetodo.data.repository

import com.example.schetodo.data.entity.TodoCategory
import kotlinx.coroutines.flow.Flow

interface TodoCategoryRepository {
    fun insertTodoCategory(todoCategory: TodoCategory)
    fun getChildTodoCategoriesOf(todoCategoryId: Int?): Flow<List<TodoCategory>>
    fun getTodoCategory(todoCategoryId: Int?): Flow<TodoCategory?>
}