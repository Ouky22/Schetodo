package com.example.schetodo.data.relationship

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTodoBlockTodoRelationshipDao : TodoBlockTodoRelationshipDao {
    private val todoBlockTodoRelationships = mutableListOf<TodoBlockTodoRelationship>()
    override suspend fun connectTodoBlockAndTodo(todoBlockId: Int, todoId: Int) {
        todoBlockTodoRelationships.add(TodoBlockTodoRelationship(todoBlockId, todoId))
    }

    override suspend fun disconnectAllTodosFromTodoBlock(todoBlockId: Int) {
        todoBlockTodoRelationships.removeIf { it.todoBlockId == todoBlockId }
    }

    override fun getAllTodoBlockTodoRelationships(): Flow<List<TodoBlockTodoRelationship>> {
        return flow {
            emit(todoBlockTodoRelationships)
        }
    }
}