package com.example.schetodo.data.todo_block

import androidx.room.*
import com.example.schetodo.data.schedule_block.ScheduleBlock
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoBlockDao {
    @Query("SELECT * FROM TodoBlock WHERE todoBlockId = :todoBlockId ")
    fun getTodoBlockById(todoBlockId: Int): Flow<TodoBlock?>

    @Query("SELECT * FROM TodoBlock WHERE date = :dateStampInDays AND markedForDeletion = 0")
    fun getTodoBlocksOnDate(dateStampInDays: Long): Flow<List<TodoBlock>>

    @Query("SELECT * FROM TodoBlock WHERE markedForDeletion = 0")
    fun getAllTodoBlocks(): Flow<List<TodoBlock>>

    @Insert
    suspend fun insertTodoBlock(todoBlock: TodoBlock): Long

    @Update
    suspend fun updateTodoBlock(todoBlock: TodoBlock)

    @Upsert
    suspend fun updateOrInsertTodoBlock(todoBlock: TodoBlock): Long

    @Delete
    suspend fun deleteTodoBlock(todoBlock: TodoBlock)

    @Query("DELETE FROM TodoBlock WHERE todoBlockId = :todoBlockId")
    suspend fun deleteTodoBlockById(todoBlockId: Int)

    @Query("UPDATE TodoBlock SET markedForDeletion = 1 WHERE todoBlockId = :todoBlockId")
    suspend fun markTodoBlockForDeletion(todoBlockId: Int)

    @Query("UPDATE TodoBlock SET markedForDeletion = 0 WHERE todoBlockId = :todoBlockId")
    suspend fun unmarkTodoBlockForDeletion(todoBlockId: Int)

    @Query("DELETE FROM TodoBlock WHERE markedForDeletion = 1")
    suspend fun deleteAllTodoBlocksMarkedForDeletion()
}
