package com.example.schetodo.data.schedule_block

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject


class ScheduleBlockRepositoryImpl @Inject constructor(
    private val todoScheduleBlockDao: ScheduleBlockDao
) : ScheduleBlockRepository {

    override fun getScheduleBlocksOnDate(date: LocalDate) =
        todoScheduleBlockDao.getScheduleBlocksOnDate(date.toEpochDay())

    override fun getScheduleBlockByTodoBlockId(todoBlockId: Int): Flow<ScheduleBlock?> =
        todoScheduleBlockDao.getScheduleBlockByTodoBlockId(todoBlockId)
}