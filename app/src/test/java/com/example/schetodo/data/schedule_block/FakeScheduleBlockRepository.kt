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

    override suspend fun insertOrUpdateScheduleBlock(scheduleBlock: ScheduleBlock) {
        val indexOfScheduleBlock = scheduleBlocks.indexOfFirst {
            it.todoBlock.todoBlockId == scheduleBlock.todoBlock.todoBlockId
        }

        if (indexOfScheduleBlock >= 0) {
            val oldTodoBlock = scheduleBlock.todoBlock
            val updatedScheduleBlock =
                scheduleBlock.copy(todoBlock = oldTodoBlock.copy(todoBlockId = oldTodoBlock.todoBlockId))
            scheduleBlocks.add(updatedScheduleBlock)
        } else
            scheduleBlocks.add(scheduleBlock)
    }
}