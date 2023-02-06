package com.example.schetodo.data.repository

import com.example.schetodo.data.entity.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTodoRepository : TodoRepository {
    private val todos = mutableListOf<Todo>()

    override suspend fun insertTodo(todo: Todo) {
        todos.add(todo)
    }

    override fun getTodosOfTodoCategory(todoCategoryId: Int?): Flow<List<Todo>> {
        return flow {
            val todosOfCategory = todos.filter { it.categoryId == todoCategoryId }
            emit(todosOfCategory)
        }
    }
}