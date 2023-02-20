package com.example.schetodo.data.schedule_block

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class FakeScheduleBlockRepository : ScheduleBlockRepository {

    private val scheduleBlocks = mutableListOf<ScheduleBlock>()

    override fun getScheduleBlocksOnDate(date: LocalDate): Flow<List<ScheduleBlock>> {
        return flow {
            emit(scheduleBlocks.filter { it.todoBlock.date == date })
        }
    }

    override fun getScheduleBlockByTodoBlockId(todoBlockId: Int): Flow<ScheduleBlock?> {
        return flow {
            emit(scheduleBlocks.firstOrNull { it.todoBlock.todoBlockId == todoBlockId })
        }
    }

    override suspend fun scheduleBlockOverlapsWithOtherScheduleBlock(scheduleBlock: ScheduleBlock): Boolean {
        TODO("Not yet implemented")
    }

    fun insertScheduleBlock(scheduleBlock: ScheduleBlock) = scheduleBlocks.add(scheduleBlock)
}