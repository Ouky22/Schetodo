package com.example.schetodo.data.todo_category

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeTodoCategoryDao : TodoCategoryDao {
    private val todoCategories = mutableListOf<TodoCategory>()

    override suspend fun insertTodoCategory(todoCategory: TodoCategory): Long {
        todoCategories.add(todoCategory)
        return todoCategory.categoryId.toLong()
    }

    override suspend fun insertOrUpdateTodoCategory(todoCategory: TodoCategory) {
        todoCategories.removeIf { todoCategory.categoryId == it.categoryId }
        todoCategories.add(todoCategory)
    }

    override fun getTodoCategoryById(todoCategoryId: Int): Flow<TodoCategory?> {
        return flow {
            emit(todoCategories.firstOrNull { it.categoryId == todoCategoryId })
        }
    }

    override fun getTopLevelTodoCategories(): Flow<List<TodoCategory>> {
        return flow {
            emit(todoCategories.filter {
                it.parentTodoCategoryId == null
            })
        }
    }

    override fun getDirectChildTodoCategoriesOf(todoCategoryId: Int, withMarkedForDeletion: Boolean): Flow<List<TodoCategory>> {
        return flow {
            emit(todoCategories.filter {
                it.parentTodoCategoryId == todoCategoryId
            })
        }
    }

    override suspend fun deleteTodoCategory(todoCategory: TodoCategory) {
        todoCategories.remove(todoCategory)
    }

    override suspend fun deleteTodoCategoryById(todoCategoryId: Int) {
        todoCategories.removeIf { it.categoryId == todoCategoryId }
    }

    override suspend fun markTodoCategoryForDeletion(todoCategoryId: Int) {
        val indexOfCategoryInList = todoCategories.indexOfFirst { it.categoryId == todoCategoryId }

        if (indexOfCategoryInList == -1)
            return

        val oldCategory = todoCategories.removeAt(indexOfCategoryInList)
        val newCategory = oldCategory.copy(markedForDeletion = true)
        todoCategories.add(newCategory)
    }

    override suspend fun unmarkTodoCategoryForDeletion(todoCategoryId: Int) {
        val indexOfCategoryInList = todoCategories.indexOfFirst { it.categoryId == todoCategoryId }

        if (indexOfCategoryInList == -1)
            return

        val oldCategory = todoCategories.removeAt(indexOfCategoryInList)
        val newCategory = oldCategory.copy(markedForDeletion = false)
        todoCategories.add(newCategory)
    }

    override suspend fun deleteAllTodoCategoriesMarkedForDeletion() {
        todoCategories.removeIf { it.markedForDeletion }
    }
}