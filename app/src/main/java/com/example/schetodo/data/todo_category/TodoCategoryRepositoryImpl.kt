package com.example.schetodo.data.todo_category

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoCategoryRepositoryImpl @Inject constructor(
    private val todoCategoryDao: TodoCategoryDao
) : TodoCategoryRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            todoCategoryDao.deleteAllTodoCategoriesMarkedForDeletion()
        }
    }

    override suspend fun insertTodoCategory(todoCategory: TodoCategory) =
        todoCategoryDao.insertTodoCategory(todoCategory)

    override suspend fun insertOrUpdateTodoCategory(todoCategory: TodoCategory) =
        todoCategoryDao.insertOrUpdateTodoCategory(todoCategory)

    override suspend fun deleteTodoCategory(todoCategory: Int) {
        todoCategoryDao.deleteTodoCategoryById(todoCategory)
    }

    override suspend fun markTodoCategoryForDeletion(todoCategoryId: Int) {
        todoCategoryDao.markTodoCategoryForDeletion(todoCategoryId)
    }

    override suspend fun unmarkTodoCategoryForDeletion(todoCategoryId: Int) {
        todoCategoryDao.unmarkTodoCategoryForDeletion(todoCategoryId)
    }

    override fun getChildTodoCategoriesOf(todoCategoryId: Int?, withMarkedForDeletion: Boolean): Flow<List<TodoCategory>> {
        return if (todoCategoryId == null)
            todoCategoryDao.getTopLevelTodoCategories()
        else
            todoCategoryDao.getDirectChildTodoCategoriesOf(todoCategoryId, withMarkedForDeletion)
    }

    override fun getTodoCategory(todoCategoryId: Int?): Flow<TodoCategory?> {
        return if (todoCategoryId == null)
            flow { emit(null) }
        else
            todoCategoryDao.getTodoCategoryById(todoCategoryId)
    }
}