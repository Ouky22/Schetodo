package com.example.schetodo.data.todo_category

import com.example.schetodo.di.CoroutineScopeModule.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoCategoryRepositoryImpl @Inject constructor(
    private val todoCategoryDao: TodoCategoryDao,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope
) : TodoCategoryRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            todoCategoryDao.deleteAllTodoCategoriesMarkedForDeletion()
        }
    }

    override suspend fun insertTodoCategory(todoCategory: TodoCategory) =
        withContext(applicationCoroutineScope.coroutineContext) {
            todoCategoryDao.insertTodoCategory(todoCategory)
        }

    override suspend fun insertOrUpdateTodoCategory(todoCategory: TodoCategory) {
        applicationCoroutineScope.launch {
            todoCategoryDao.insertOrUpdateTodoCategory(todoCategory)
        }.join()
    }

    override suspend fun deleteTodoCategory(todoCategory: Int) {
        applicationCoroutineScope.launch {
            todoCategoryDao.deleteTodoCategoryById(todoCategory)
        }.join()
    }

    override suspend fun markTodoCategoryForDeletion(todoCategoryId: Int) {
        applicationCoroutineScope.launch {
            todoCategoryDao.markTodoCategoryForDeletion(todoCategoryId)
        }.join()
    }

    override suspend fun unmarkTodoCategoryForDeletion(todoCategoryId: Int) {
        applicationCoroutineScope.launch {
            todoCategoryDao.unmarkTodoCategoryForDeletion(todoCategoryId)
        }.join()
    }

    override fun getChildTodoCategoriesOf(
        todoCategoryId: Int?,
        withMarkedForDeletion: Boolean
    ): Flow<List<TodoCategory>> {
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