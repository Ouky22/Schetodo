package com.example.schetodo.data.todo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow


class FakeTodoDao : TodoDao {
    private val todos = mutableListOf<Todo>()

    override suspend fun insertTodo(todo: Todo): Long {
        todos.add(todo)
        return todo.todoId.toLong()
    }

    override suspend fun insertOrUpdateTodo(todo: Todo): Long {
        todos.removeIf { it.todoId == todo.todoId }
        todos.add(todo)
        return todo.todoId.toLong()
    }

    override suspend fun updateTodo(todo: Todo) {
        val indexOfTodoInList = todos.indexOfFirst { it.todoId == todo.todoId }
        if (indexOfTodoInList >= 0) {
            todos.removeAt(indexOfTodoInList)
            todos.add(todo)
        }
    }

    override suspend fun deleteTodoById(todoId: Int) {
        todos.removeIf { it.todoId == todoId }
    }

    override suspend fun markTodoForDeletion(todoId: Int) {
        val indexOfTodoInList = todos.indexOfFirst { it.todoId == todoId }

        if (indexOfTodoInList == -1)
            return

        val oldTodo = todos.removeAt(indexOfTodoInList)
        val newTodo = oldTodo.copy(markedForDeletion = true)
        todos.add(newTodo)
    }

    override suspend fun unmarkTodoForDeletion(todoId: Int) {
        val indexOfTodoInList = todos.indexOfFirst { it.todoId == todoId }

        if (indexOfTodoInList == -1)
            return

        val oldTodo = todos.removeAt(indexOfTodoInList)
        val newTodo = oldTodo.copy(markedForDeletion = false)
        todos.add(newTodo)
    }

    override suspend fun markAllTodosOfCategoryForDeletion(todoCategoryId: Int) {
        getAllTodosOfTodoCategory(todoCategoryId).first().forEach { todo ->
            updateTodo(todo.copy(markedForDeletion = true))
        }
    }

    override suspend fun unmarkAllTodosOfCategoryForDeletion(todoCategoryId: Int) {
        getAllTodosOfTodoCategory(todoCategoryId).first().forEach { todo ->
            updateTodo(todo.copy(markedForDeletion = false))
        }
    }

    override suspend fun deleteAllTodosMarkedForDeletion() {
        todos.removeIf { it.markedForDeletion }
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

    override fun getAllTodosWithFlag(todoFlag: TodoFlag): Flow<List<Todo>> {
        return flow {
            val todos = todos.filter { it.flag == todoFlag }
            emit(todos)
        }
    }
}