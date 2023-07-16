package com.example.schetodo.data.relationship

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoBlockTodoRelationshipDao {
    @Query("INSERT INTO TodoBlockTodoRelationship VALUES (:todoBlockId, :todoId)")
    suspend fun connectTodoBlockAndTodo(todoBlockId: Int, todoId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun connectTodoBlocksAndTodos(relationships: List<TodoBlockTodoRelationship>)

    @Query("DELETE FROM TodoBlockTodoRelationship WHERE todoBlockId = :todoBlockId")
    suspend fun disconnectAllTodosFromTodoBlock(todoBlockId: Int)

    @Query("SELECT * FROM TodoBlockTodoRelationship")
    fun getAllTodoBlockTodoRelationships(): Flow<List<TodoBlockTodoRelationship>>
}