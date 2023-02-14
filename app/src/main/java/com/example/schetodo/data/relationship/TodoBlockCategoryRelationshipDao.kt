package com.example.schetodo.data.relationship

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoBlockCategoryRelationshipDao {
    @Query("INSERT INTO TodoBlockCategoryRelationship VALUES (:todoBlockId, :todoCategoryId)")
    fun connectTodoBlockAndTodoCategory(todoBlockId: Int, todoCategoryId: Int)

    @Query("DELETE FROM TodoBlockCategoryRelationship WHERE todoBlockId = :todoBlockId")
    fun disconnectAllTodoCategoriesFromTodoBlock(todoBlockId: Int)

    @Query("SELECT * FROM TodoBlockCategoryRelationship")
    fun getAllTodoBlockCategoryRelationships() : Flow<List<TodoBlockCategoryRelationship>>
}