package com.example.schetodo.data.repository

import com.example.schetodo.data.dao.TodoCategoryDao
import com.example.schetodo.data.entity.TodoCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoCategoryRepository @Inject constructor(
    private val todoCategoryDao: TodoCategoryDao
) {
    fun getChildTodoCategoriesOf(todoCategoryId: Int?): Flow<List<TodoCategory>> {
        return if (todoCategoryId == null)
            todoCategoryDao.getTopLevelTodoCategories()
        else
            todoCategoryDao.getDirectChildTodoCategoriesOf(todoCategoryId)
    }

    fun getTodoCategory(todoCategoryId: Int?): Flow<TodoCategory?> {
        return if (todoCategoryId == null)
            flow { emit(null) }
        else
            todoCategoryDao.getTodoCategoryById(todoCategoryId)
    }
}