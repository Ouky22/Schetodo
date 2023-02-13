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
        val indexOfCategoryInList =
            todoCategories.indexOfFirst { it.categoryId == todoCategory.categoryId }

        if (indexOfCategoryInList >= 0) {
            val oldCategory = todoCategories.removeAt(indexOfCategoryInList)
            val updatedTodoCategory = todoCategory.copy(categoryId = oldCategory.categoryId)
            todoCategories.add(updatedTodoCategory)
        } else
            todoCategories.add(todoCategory.copy(categoryId = todoCategories.size))
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

    override fun getDirectChildTodoCategoriesOf(todoCategoryId: Int): Flow<List<TodoCategory>> {
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
}