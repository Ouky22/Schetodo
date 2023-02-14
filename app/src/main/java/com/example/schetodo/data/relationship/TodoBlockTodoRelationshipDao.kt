package com.example.schetodo.data.relationship

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoBlockTodoRelationshipDao {
    @Query("INSERT INTO TodoBlockTodoRelationship VALUES (:todoBlockId, :todoId)")
    fun connectTodoBlockAndTodo(todoBlockId: Int, todoId: Int)

    @Query("DELETE FROM TodoBlockTodoRelationship WHERE todoBlockId = :todoBlockId")
    fun disconnectAllTodosFromTodoBlock(todoBlockId: Int)

    @Query("SELECT * FROM TodoBlockTodoRelationship")
    fun getAllTodoBlockTodoRelationships() : Flow<List<TodoBlockTodoRelationship>>
}