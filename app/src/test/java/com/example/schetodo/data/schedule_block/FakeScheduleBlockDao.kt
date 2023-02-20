package com.example.schetodo.data.schedule_block

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeScheduleBlockDao : ScheduleBlockDao {
    private val scheduleBlocks = mutableListOf<ScheduleBlock>()

    override fun getScheduleBlocks(): Flow<List<ScheduleBlock>> {
        return flow {
            emit(scheduleBlocks)
        }
    }

    override fun getScheduleBlocksOnDate(dateStampInDays: Long): Flow<List<ScheduleBlock>> {
        return flow {
            emit(scheduleBlocks.filter { it.todoBlock.date?.toEpochDay() == dateStampInDays })
        }
    }

    override fun getScheduleBlockByTodoBlockId(todoBlockId: Int): Flow<ScheduleBlock?> {
        return flow {
            emit(scheduleBlocks.firstOrNull { it.todoBlock.todoBlockId == todoBlockId })
        }
    }

    fun insertScheduleBlock(scheduleBlock: ScheduleBlock) {
        scheduleBlocks.add(scheduleBlock)
    }
}