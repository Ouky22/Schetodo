package com.example.schetodo.data.repository

import com.example.schetodo.data.entity.TodoCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTodoCategoryRepository : TodoCategoryRepository {
    private val todoCategories = mutableListOf<TodoCategory>()

    override fun insertTodoCategory(todoCategory: TodoCategory) {
        todoCategories.add(todoCategory)
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