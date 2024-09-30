package com.example.schetodo.data.todo_block

import androidx.room.*
import com.example.schetodo.data.TODO_BLOCK_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoBlockDao {
    @Query("SELECT * FROM $TODO_BLOCK_TABLE_NAME WHERE todoBlockId = :todoBlockId ")
    fun getTodoBlockById(todoBlockId: Int): Flow<TodoBlock?>

    @Query("SELECT * FROM $TODO_BLOCK_TABLE_NAME WHERE date = :dateStampInDays AND markedForDeletion = 0")
    fun getTodoBlocksOnDate(dateStampInDays: Long): Flow<List<TodoBlock>>

    @Query("SELECT * FROM $TODO_BLOCK_TABLE_NAME WHERE markedForDeletion = 0")
    fun getAllTodoBlocks(): Flow<List<TodoBlock>>

    @Insert
    suspend fun insertTodoBlock(todoBlock: TodoBlock): Long

    @Update
    suspend fun updateTodoBlock(todoBlock: TodoBlock)

    @Upsert
    suspend fun updateOrInsertTodoBlock(todoBlock: TodoBlock): Long

    @Delete
    suspend fun deleteTodoBlock(todoBlock: TodoBlock)

    @Query("DELETE FROM $TODO_BLOCK_TABLE_NAME WHERE todoBlockId = :todoBlockId")
    suspend fun deleteTodoBlockById(todoBlockId: Int)

    @Query("UPDATE $TODO_BLOCK_TABLE_NAME SET markedForDeletion = 1 WHERE todoBlockId = :todoBlockId")
    suspend fun markTodoBlockForDeletion(todoBlockId: Int)

    @Query("UPDATE $TODO_BLOCK_TABLE_NAME SET markedForDeletion = 1 WHERE date = :dateStampInDays")
    suspend fun markTodoBlocksOnDateForDeletion(dateStampInDays: Long)

    @Query("UPDATE $TODO_BLOCK_TABLE_NAME SET markedForDeletion = 1 WHERE templateId = :templateId")
    suspend fun markTodoBlocksOfScheduleTemplateForDeletion(templateId: Int)

    @Query("UPDATE $TODO_BLOCK_TABLE_NAME SET markedForDeletion = 0 WHERE templateId = :templateId")
    suspend fun unmarkTodoBlocksOfScheduleTemplateForDeletion(templateId: Int)

    @Query("UPDATE $TODO_BLOCK_TABLE_NAME SET markedForDeletion = 0 WHERE date = :dateStampInDays")
    suspend fun unmarkTodoBlocksOnDateForDeletion(dateStampInDays: Long)

    @Query("UPDATE $TODO_BLOCK_TABLE_NAME SET markedForDeletion = 0 WHERE todoBlockId = :todoBlockId")
    suspend fun unmarkTodoBlockForDeletion(todoBlockId: Int)

    @Query("DELETE FROM $TODO_BLOCK_TABLE_NAME WHERE markedForDeletion = 1")
    suspend fun deleteAllTodoBlocksMarkedForDeletion()

    @Query("DELETE FROM $TODO_BLOCK_TABLE_NAME WHERE templateId = :templateId")
    suspend fun deleteAllTodoBlocksOfScheduleTemplate(templateId: Int)
}
