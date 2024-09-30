package com.example.schetodo.data.relationship

import androidx.room.*
import com.example.schetodo.data.TODO_BLOCK_CATEGORY_RELATIONSHIP_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoBlockCategoryRelationshipDao {
    @Query("INSERT INTO $TODO_BLOCK_CATEGORY_RELATIONSHIP_TABLE_NAME VALUES (:todoBlockId, :todoCategoryId)")
    suspend fun connectTodoBlockAndTodoCategory(todoBlockId: Int, todoCategoryId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun connectTodoBlocksAndTodoCategories(relationships: List<TodoBlockCategoryRelationship>)

    @Query("DELETE FROM $TODO_BLOCK_CATEGORY_RELATIONSHIP_TABLE_NAME WHERE todoBlockId = :todoBlockId")
    suspend fun disconnectAllTodoCategoriesFromTodoBlock(todoBlockId: Int)

    @Query("SELECT * FROM $TODO_BLOCK_CATEGORY_RELATIONSHIP_TABLE_NAME")
    fun getAllTodoBlockCategoryRelationships(): Flow<List<TodoBlockCategoryRelationship>>
}