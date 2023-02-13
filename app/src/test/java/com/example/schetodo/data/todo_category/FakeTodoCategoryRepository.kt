package com.example.schetodo.data.todo_category

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTodoCategoryRepository : TodoCategoryRepository {
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

    override suspend fun deleteTodoCategory(todoCategory: Int) {
        todoCategories.removeIf { it.categoryId == todoCategory }
    }

    override fun getChildTodoCategoriesOf(todoCategoryId: Int?): Flow<List<TodoCategory>> {
        return flow {
            val childCategories = todoCategories.filter { todoCategory ->
                todoCategory.parentTodoCategoryId == todoCategoryId
            }
            emit(childCategories)
        }
    }

    override fun getTodoCategory(todoCategoryId: Int?): Flow<TodoCategory?> {
        return flow {
            emit(todoCategories.firstOrNull { it.categoryId == todoCategoryId })
        }
    }
}