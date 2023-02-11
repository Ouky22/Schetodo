package com.example.schetodo.data.dao

import com.example.schetodo.data.entity.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeTodoDao : TodoDao {
    private val todos = mutableListOf<Todo>()

    override suspend fun insertTodo(todo: Todo): Long {
        todos.add(todo)
        return todo.todoId.toLong()
    }

    override suspend fun insertOrUpdateTodo(todo: Todo): Long {
        val indexOfTodoInList = todos.indexOfFirst { it.todoId == todo.todoId }
        var id = todo.todoId

        if (indexOfTodoInList >= 0) {
            val oldTodo = todos.removeAt(indexOfTodoInList)
            val updatedTodo = todo.copy(todoId = oldTodo.todoId)
            todos.add(updatedTodo)
            id = todo.todoId
        } else
            todos.add(todo)

        return id.toLong()
    }

    override suspend fun deleteTodoById(todoId: Int) {
        todos.removeIf { it.todoId == todoId }
    }

    override fun getTodoById(todoId: Int): Flow<Todo?> =
        flow {
            emit(todos.find {
                it.todoId == todoId
            })
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