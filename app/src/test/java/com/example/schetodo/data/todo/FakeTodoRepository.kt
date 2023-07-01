package com.example.schetodo.data.todo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTodoRepository : TodoRepository {
    private val todos = mutableListOf<Todo>()

    override suspend fun insertTodo(todo: Todo) {
        todos.add(todo)
    }

    override suspend fun insertOrUpdateTodo(todo: Todo) {
        val indexOfTodoInList = todos.indexOfFirst { it.todoId == todo.todoId }

        if (indexOfTodoInList >= 0) {
            val oldTodo = todos.removeAt(indexOfTodoInList)
            val updatedTodo = todo.copy(todoId = oldTodo.todoId)
            todos.add(updatedTodo)
        } else
            todos.add(todo)
    }

    override suspend fun deleteTodoById(todoId: Int) {
        todos.removeIf { it.todoId == todoId }
    }

    override suspend fun getTodoById(todoId: Int): Flow<Todo?> =
        flow {
            val todo = todos.find { it.todoId == todoId }
            emit(todo)
        }

    override fun getTodosOfTodoCategory(
        todoCategoryId: Int?, todoFilterSettings: TodoFilterSettings
    ): Flow<List<Todo>> {
        return flow {
            val todosOfCategory = todos.filter { it.categoryId == todoCategoryId }
            emit(todosOfCategory)
        }
    }

    override fun getTodosOfTodoCategory(todoCategoryId: Int?): Flow<List<Todo>> =
        getTodosOfTodoCategory(todoCategoryId, TodoFilterSettings())

    override fun getTodoFilterSettings(): Flow<TodoFilterSettings> =
        flow {
            emit(TodoFilterSettings())
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

    override fun getTodosInProgress(): Flow<List<Todo>> {
        return flow {
            val todos = todos.filter { it.flag == TodoFlag.IN_PROGRESS }
            emit(todos)
        }
    }

    override suspend fun updateTodo(todo: Todo) {
        val indexOfTodoInList = todos.indexOfFirst { it.todoId == todo.todoId }
        if (indexOfTodoInList >= 0) {
            val oldTodo = todos.removeAt(indexOfTodoInList)
            val updatedTodo = todo.copy(todoId = oldTodo.todoId)
            todos.add(updatedTodo)
        }
    }

    override suspend fun setTodoFilterSettings(todoFilterSettings: TodoFilterSettings) {}
}