package com.example.schetodo.data.relationship

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTodoBlockCategoryRelationshipDao : TodoBlockCategoryRelationshipDao {
    private val todoBlockCategoryRelationships = mutableListOf<TodoBlockCategoryRelationship>()

    override suspend fun connectTodoBlockAndTodoCategory(todoBlockId: Int, todoCategoryId: Int) {
        todoBlockCategoryRelationships.add(TodoBlockCategoryRelationship(todoBlockId, todoCategoryId))
    }

    override suspend fun disconnectAllTodoCategoriesFromTodoBlock(todoBlockId: Int) {
        todoBlockCategoryRelationships.removeIf { it.todoBlockId == todoBlockId }
    }

    override fun getAllTodoBlockCategoryRelationships(): Flow<List<TodoBlockCategoryRelationship>> {
        return flow {
            emit(todoBlockCategoryRelationships)
        }
    }
}