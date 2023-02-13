package com.example.schetodo.data.todo_category

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoCategoryRepositoryImpl @Inject constructor(
    private val todoCategoryDao: TodoCategoryDao
) : TodoCategoryRepository {

    override suspend fun insertTodoCategory(todoCategory: TodoCategory) =
        todoCategoryDao.insertTodoCategory(todoCategory)

    override suspend fun insertOrUpdateTodoCategory(todoCategory: TodoCategory) =
        todoCategoryDao.insertOrUpdateTodoCategory(todoCategory)

    override suspend fun deleteTodoCategory(todoCategory: Int) {
        todoCategoryDao.deleteTodoCategoryById(todoCategory)
    }

    override fun getChildTodoCategoriesOf(todoCategoryId: Int?): Flow<List<TodoCategory>> {
        return if (todoCategoryId == null)
            todoCategoryDao.getTopLevelTodoCategories()
        else
            todoCategoryDao.getDirectChildTodoCategoriesOf(todoCategoryId)
    }

    override fun getTodoCategory(todoCategoryId: Int?): Flow<TodoCategory?> {
        return if (todoCategoryId == null)
            flow { emit(null) }
        else
            todoCategoryDao.getTodoCategoryById(todoCategoryId)
    }
}