package com.example.schetodo.data.schedule_block

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject


class ScheduleBlockRepositoryImpl @Inject constructor(
    private val todoScheduleBlockDao: ScheduleBlockDao
) : ScheduleBlockRepository {

    override fun getScheduleBlocksOnDate(date: LocalDate) =
        todoScheduleBlockDao.getScheduleBlocksOnDate(date.toEpochDay())

    override fun getScheduleBlockByTodoBlockId(todoBlockId: Int): Flow<ScheduleBlock?> =
        todoScheduleBlockDao.getScheduleBlockByTodoBlockId(todoBlockId)

    override suspend fun scheduleBlockOverlapsWithOtherScheduleBlock(scheduleBlock: ScheduleBlock): Boolean {
        val date = scheduleBlock.todoBlock.date ?: return false
        val startTime = scheduleBlock.todoBlock.startTime
        val endTime = scheduleBlock.todoBlock.endTime

        val allScheduleBlocksOnDate =
            todoScheduleBlockDao.getScheduleBlocksOnDate(date.toEpochDay()).first()

        if (allScheduleBlocksOnDate.isEmpty())
            return false

        for (todoBlock in allScheduleBlocksOnDate.map { it.todoBlock }) {
            val overlapsWithTodoBlock =
                startTime.isBefore(todoBlock.endTime) && todoBlock.startTime.isBefore(endTime)

            if (overlapsWithTodoBlock)
                return true
        }

        return false
    }
}