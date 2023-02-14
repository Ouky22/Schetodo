package com.example.schetodo.ui.feature.schedule

import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
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

    fun insertScheduleBlock(scheduleBlock: ScheduleBlock) = scheduleBlocks.add(scheduleBlock)
}