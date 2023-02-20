package com.example.schetodo.data.todo_block

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class TodoBlockRepositoryImpl @Inject constructor(
    private val todoBlockDao: TodoBlockDao
) : TodoBlockRepository {
    override fun getBlockById(todoBlockId: Int) = todoBlockDao.getTodoBlockById(todoBlockId)

    override fun getTodoBlocksOnDate(date: LocalDate): Flow<List<TodoBlock>> {
        val dateStamp = date.toEpochDay()
        return todoBlockDao.getTodoBlocksOnDate(dateStamp)
    }

    override fun getAllTodoBlocks() = todoBlockDao.getAllTodoBlocks()

    override suspend fun insertTodoBlock(todoBlock: TodoBlock) =
        todoBlockDao.insertTodoBlock(todoBlock)

    override suspend fun updateTodoBlock(todoBlock: TodoBlock) =
        todoBlockDao.updateTodoBlock(todoBlock)

    override suspend fun updateOrInsertTodoBlock(todoBlock: TodoBlock) =
        todoBlockDao.updateOrInsertTodoBlock(todoBlock)

    override suspend fun deleteTodoBlock(todoBlock: TodoBlock) =
        todoBlockDao.deleteTodoBlock(todoBlock)

    override suspend fun deleteTodoBlockById(todoBlockId: Int) =
        todoBlockDao.deleteTodoBlockById(todoBlockId)

    override suspend fun todoBlockOverlapsWithOtherTodoBlock(
        todoBlock: TodoBlock, exceptOfTodoBlockId: Int?
    ): Boolean {
        val date = todoBlock.date ?: return false
        val startTime = todoBlock.startTime
        val endTime = todoBlock.endTime

        val allTodoBlocksOnDate = getTodoBlocksOnDate(date).first()

        if (allTodoBlocksOnDate.isEmpty())
            return false

        for (otherTodoBlock in allTodoBlocksOnDate) {
            val overlapsWithTodoBlock =
                if (otherTodoBlock.todoBlockId == exceptOfTodoBlockId) false
                else startTime.isBefore(otherTodoBlock.endTime) and
                        otherTodoBlock.startTime.isBefore(endTime)

            if (overlapsWithTodoBlock)
                return true
        }

        return false
    }
}