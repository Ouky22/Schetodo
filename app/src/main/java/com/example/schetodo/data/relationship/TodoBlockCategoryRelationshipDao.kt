package com.example.schetodo.data.relationship

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoBlockCategoryRelationshipDao {
    @Transaction
    @Query("INSERT INTO TodoBlockCategoryRelationship VALUES (:todoBlockId, :todoCategoryId)")
    suspend fun connectTodoBlockAndTodoCategory(todoBlockId: Int, todoCategoryId: Int)

    @Transaction
    @Query("DELETE FROM TodoBlockCategoryRelationship WHERE todoBlockId = :todoBlockId")
    suspend fun disconnectAllTodoCategoriesFromTodoBlock(todoBlockId: Int)

    @Query("SELECT * FROM TodoBlockCategoryRelationship")
    fun getAllTodoBlockCategoryRelationships(): Flow<List<TodoBlockCategoryRelationship>>
}