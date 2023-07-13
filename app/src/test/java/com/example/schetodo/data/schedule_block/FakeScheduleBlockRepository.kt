package com.example.schetodo.data.schedule_block

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class FakeScheduleBlockRepository : ScheduleBlockRepository {

    val scheduleBlocks = mutableListOf<ScheduleBlock>()

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
        scheduleBlocks.removeIf { it.todoBlock.todoBlockId == scheduleBlock.todoBlock.todoBlockId }
        scheduleBlocks.add(scheduleBlock)
    }

    override val showScheduleBlockNotificationAtEnd: Flow<Boolean>
        get() = flow {
            emit(false)
        }

    override val showScheduleBlockNotificationAtBeginning: Flow<Boolean>
        get() = flow {
            emit(false)
        }

    override suspend fun unmarkTodoBlockForDeletion(todoBlockId: Int) {
        val indexOfTodoBlockInList = scheduleBlocks.indexOfFirst {
            it.todoBlock.todoBlockId == todoBlockId
        }

        if (indexOfTodoBlockInList == -1)
            return

        val oldScheduleBlock = scheduleBlocks.removeAt(indexOfTodoBlockInList)
        val newScheduleBlock = oldScheduleBlock.copy(
            todoBlock = oldScheduleBlock.todoBlock.copy(markedForDeletion = false)
        )
        scheduleBlocks.add(newScheduleBlock)
    }

    override fun getScheduleBlocksOfScheduleTemplate(templateId: Int): Flow<List<ScheduleBlock>> {
        return flow {
            emit(scheduleBlocks.filter { it.todoBlock.templateId == templateId })
        }
    }
}