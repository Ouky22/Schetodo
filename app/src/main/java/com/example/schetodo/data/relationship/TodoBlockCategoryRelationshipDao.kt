package com.example.schetodo.data.relationship

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoBlockCategoryRelationshipDao {
    @Query("INSERT INTO TodoBlockCategoryRelationship VALUES (:todoBlockId, :todoCategoryId)")
    suspend fun connectTodoBlockAndTodoCategory(todoBlockId: Int, todoCategoryId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun connectTodoBlocksAndTodoCategories(relationships: List<TodoBlockCategoryRelationship>)

    @Query("DELETE FROM TodoBlockCategoryRelationship WHERE todoBlockId = :todoBlockId")
    suspend fun disconnectAllTodoCategoriesFromTodoBlock(todoBlockId: Int)

    @Query("SELECT * FROM TodoBlockCategoryRelationship")
    fun getAllTodoBlockCategoryRelationships(): Flow<List<TodoBlockCategoryRelationship>>
}