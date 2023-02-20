package com.example.schetodo.data.relationship

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoBlockTodoRelationshipDao {
    @Transaction
    @Query("INSERT INTO TodoBlockTodoRelationship VALUES (:todoBlockId, :todoId)")
    suspend fun connectTodoBlockAndTodo(todoBlockId: Int, todoId: Int)

    @Transaction
    @Query("DELETE FROM TodoBlockTodoRelationship WHERE todoBlockId = :todoBlockId")
    suspend fun disconnectAllTodosFromTodoBlock(todoBlockId: Int)

    @Query("SELECT * FROM TodoBlockTodoRelationship")
    fun getAllTodoBlockTodoRelationships(): Flow<List<TodoBlockTodoRelationship>>
}