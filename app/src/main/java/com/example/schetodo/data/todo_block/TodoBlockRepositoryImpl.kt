package com.example.schetodo.data.todo_block

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class TodoBlockRepositoryImpl @Inject constructor(
    private val todoBlockDao: TodoBlockDao
) : TodoBlockRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            todoBlockDao.deleteAllTodoBlocksMarkedForDeletion()
        }
    }

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

    override suspend fun markTodoBlockForDeletion(todoBlockId: Int) =
        todoBlockDao.markTodoBlockForDeletion(todoBlockId)

    override suspend fun markTodoBlocksOnDateForDeletion(date: LocalDate) {
        deleteAllTodoBlocksMarkedForDeletion()
        todoBlockDao.markTodoBlocksOnDateForDeletion(date.toEpochDay())
    }

    override suspend fun markTodoBlocksOfScheduleTemplateForDeletion(templateId: Int) =
        todoBlockDao.markTodoBlocksOfScheduleTemplateForDeletion(templateId)

    override suspend fun unmarkTodoBlocksOfScheduleTemplateForDeletion(templateId: Int) =
        todoBlockDao.unmarkTodoBlocksOfScheduleTemplateForDeletion(templateId)

    override suspend fun unmarkTodoBlocksOnDateForDeletion(date: LocalDate) =
        todoBlockDao.unmarkTodoBlocksOnDateForDeletion(date.toEpochDay())

    override suspend fun unmarkTodoBlockForDeletion(todoBlockId: Int) =
        todoBlockDao.unmarkTodoBlockForDeletion(todoBlockId)

    override suspend fun deleteAllTodoBlocksMarkedForDeletion() =
        todoBlockDao.deleteAllTodoBlocksMarkedForDeletion()

    override suspend fun deleteTodoBlock(todoBlock: TodoBlock) =
        todoBlockDao.deleteTodoBlock(todoBlock)

    override suspend fun deleteTodoBlockById(todoBlockId: Int) =
        todoBlockDao.deleteTodoBlockById(todoBlockId)

    override suspend fun deleteAllTodoBlocksOfScheduleTemplate(templateId: Int) =
        todoBlockDao.deleteAllTodoBlocksOfScheduleTemplate(templateId)

    override suspend fun todoBlockOverlapsWithOtherTodoBlock(todoBlock: TodoBlock): Boolean {
        val date = todoBlock.date ?: return false

        for (otherTodoBlock in getTodoBlocksOnDate(date).first()) {
            val overlapsWithTodoBlock =
                if (otherTodoBlock.todoBlockId == todoBlock.todoBlockId) false
                else todoBlocksOverlap(todoBlock, otherTodoBlock)

            if (overlapsWithTodoBlock)
                return true
        }

        return false
    }

    override suspend fun templateTodoBlockOverlapsWithTodoBlockFromSameTemplate(todoBlock: TodoBlock): Boolean {
        if (todoBlock.templateId == null) throw Exception("TodoBlock is not from a schedule template.")

        val otherTodoBlocksOfTodoBlockTemplate = todoBlockDao.getAllTodoBlocks().first().filter {
            it.templateId == todoBlock.templateId && it.todoBlockId != todoBlock.todoBlockId
        }
        for (otherTodoBlock in otherTodoBlocksOfTodoBlockTemplate)
            if (todoBlocksOverlap(todoBlock, otherTodoBlock))
                return true

        return false
    }

    override suspend fun getTodoBlocksThatOverlapWith(
        todoBlock: TodoBlock,
        date: LocalDate
    ): List<TodoBlock> {
        val overlappingTodoBlocks = mutableListOf<TodoBlock>()

        for (otherTodoBlock in getTodoBlocksOnDate(date).first())
            if (todoBlocksOverlap(todoBlock, otherTodoBlock))
                overlappingTodoBlocks.add(otherTodoBlock)

        return overlappingTodoBlocks
    }

    private fun todoBlocksOverlap(todoBlock1: TodoBlock, todoBlock2: TodoBlock): Boolean =
        todoBlock1.startTime.isBefore(todoBlock2.endTime)
                && todoBlock2.startTime.isBefore(todoBlock1.endTime)
}