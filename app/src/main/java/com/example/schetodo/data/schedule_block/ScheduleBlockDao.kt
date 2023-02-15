package com.example.schetodo.data.schedule_block

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleBlockDao {
    @Transaction
    @Query("SELECT * FROM TodoBlock")
    fun getScheduleBlocks(): Flow<List<ScheduleBlock>>

    @Transaction
    @Query("SELECT * FROM TodoBlock WHERE date = :dateStampInDays")
    fun getScheduleBlocksOnDate(dateStampInDays: Long): Flow<List<ScheduleBlock>>

    @Transaction
    @Query("SELECT * FROM TodoBlock WHERE todoBlockId = :todoBlockId")
    fun getScheduleBlockByTodoBlockId(todoBlockId: Int): Flow<ScheduleBlock?>
}