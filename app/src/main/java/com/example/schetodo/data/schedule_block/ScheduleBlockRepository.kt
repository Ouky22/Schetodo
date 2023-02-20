package com.example.schetodo.data.schedule_block

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

interface ScheduleBlockRepository {
    fun getScheduleBlocksOnDate(date: LocalDate): Flow<List<ScheduleBlock>>
    fun getScheduleBlockByTodoBlockId(todoBlockId: Int): Flow<ScheduleBlock?>
    suspend fun scheduleBlockOverlapsWithOtherScheduleBlock(scheduleBlock: ScheduleBlock) : Boolean
}