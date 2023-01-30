package com.example.schetodo.data.dao

import com.example.schetodo.data.entity.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeTodoDao : TodoDao {
    private val todos = mutableListOf<Todo>()

    override fun insertTodo(todo: Todo): Long {
        todos.add(todo)
        return todo.todoId.toLong()
    }

    override fun getAllTodos(): Flow<List<Todo>> {
        return flow { emit(todos) }
    }

    override fun getAllTodosOfTodoCategory(todoCategoryId: Int): Flow<List<Todo>> {
        return flow {
            emit(todos.filter {
                it.categoryId == todoCategoryId
            })
        }
    }
}