package com.example.schetodo.data.schedule_block

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.schetodo.data.TODO_BLOCK_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleBlockDao {
    @Transaction
    @Query("SELECT * FROM $TODO_BLOCK_TABLE_NAME WHERE markedForDeletion = 0")
    fun getScheduleBlocks(): Flow<List<ScheduleBlock>>

    @Transaction
    @Query("SELECT * FROM $TODO_BLOCK_TABLE_NAME WHERE date = :dateStampInDays AND markedForDeletion = 0")
    fun getScheduleBlocksOnDate(dateStampInDays: Long): Flow<List<ScheduleBlock>>

    @Transaction
    @Query("SELECT * FROM $TODO_BLOCK_TABLE_NAME WHERE todoBlockId = :todoBlockId")
    fun getScheduleBlockByTodoBlockId(todoBlockId: Int): Flow<ScheduleBlock?>

    @Transaction
    @Query("SELECT * FROM $TODO_BLOCK_TABLE_NAME WHERE templateId = :scheduleTemplateId AND markedForDeletion = 0")
    fun getScheduleBlocksOfScheduleTemplate(scheduleTemplateId: Int): Flow<List<ScheduleBlock>>
}