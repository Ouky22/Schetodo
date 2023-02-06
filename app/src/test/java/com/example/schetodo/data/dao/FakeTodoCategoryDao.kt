package com.example.schetodo.data.dao

import com.example.schetodo.data.entity.TodoCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeTodoCategoryDao : TodoCategoryDao {
    private val todoCategories = mutableListOf<TodoCategory>()

    override suspend fun insertTodoCategory(todoCategory: TodoCategory): Long {
        todoCategories.add(todoCategory)
        return todoCategory.categoryId.toLong()
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
}