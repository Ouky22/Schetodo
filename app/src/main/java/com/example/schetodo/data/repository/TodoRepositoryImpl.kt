package com.example.schetodo.data.repository

import com.example.schetodo.data.dao.TodoDao
import com.example.schetodo.data.entity.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
) : TodoRepository {

    override suspend fun insertTodo(todo: Todo) {
        todoDao.insertTodo(todo)
    }

    override suspend fun getTodoById(todoId: Int): Flow<Todo?> =
        todoDao.getTodoById(todoId)

    override fun getTodosOfTodoCategory(todoCategoryId: Int?): Flow<List<Todo>> {
        // all todos must have a TodoCategory, that's why an empty list is returned if no todoCategoryId is passed
        return if (todoCategoryId == null)
            flow { emit(emptyList()) }
        else
            todoDao.getAllTodosOfTodoCategory(todoCategoryId)
    }
}