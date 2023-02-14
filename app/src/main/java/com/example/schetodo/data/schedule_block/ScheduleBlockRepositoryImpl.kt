package com.example.schetodo.data.schedule_block

import com.example.schetodo.data.todo.TodoDao
import com.example.schetodo.data.todo_block.TodoBlockDao
import com.example.schetodo.data.todo_category.TodoCategoryDao
import java.time.LocalDate
import javax.inject.Inject


class ScheduleBlockRepositoryImpl @Inject constructor(
    private val todoBlockDao: TodoBlockDao,
    private val todoDao: TodoDao,
    private val todoCategoryDao: TodoCategoryDao,
    private val todoScheduleBlockDao: ScheduleBlockDao
) : ScheduleBlockRepository {

    override fun getScheduleBlocksOnDate(date: LocalDate) =
        todoScheduleBlockDao.getScheduleBlocksOnDate(date.toEpochDay())
}