package com.example.schetodo.data.relationship

import androidx.room.*
import com.example.schetodo.data.TODO_BLOCK_TODO_RELATIONSHIP_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoBlockTodoRelationshipDao {
    @Query("INSERT INTO $TODO_BLOCK_TODO_RELATIONSHIP_TABLE_NAME VALUES (:todoBlockId, :todoId)")
    suspend fun connectTodoBlockAndTodo(todoBlockId: Int, todoId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun connectTodoBlocksAndTodos(relationships: List<TodoBlockTodoRelationship>)

    @Query("DELETE FROM $TODO_BLOCK_TODO_RELATIONSHIP_TABLE_NAME WHERE todoBlockId = :todoBlockId")
    suspend fun disconnectAllTodosFromTodoBlock(todoBlockId: Int)

    @Query("SELECT * FROM $TODO_BLOCK_TODO_RELATIONSHIP_TABLE_NAME")
    fun getAllTodoBlockTodoRelationships(): Flow<List<TodoBlockTodoRelationship>>
}