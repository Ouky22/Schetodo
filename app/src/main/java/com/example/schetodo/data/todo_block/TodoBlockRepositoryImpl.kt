package com.example.schetodo.data.todo_block

import com.example.schetodo.di.CoroutineScopeModule.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class TodoBlockRepositoryImpl @Inject constructor(
    private val todoBlockDao: TodoBlockDao,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope
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
        withContext(applicationCoroutineScope.coroutineContext) {
            todoBlockDao.insertTodoBlock(todoBlock)
        }

    override suspend fun updateTodoBlock(todoBlock: TodoBlock) =
        withContext(applicationCoroutineScope.coroutineContext) {
            todoBlockDao.updateTodoBlock(todoBlock)
        }

    override suspend fun updateOrInsertTodoBlock(todoBlock: TodoBlock) =
        withContext(applicationCoroutineScope.coroutineContext) {
            todoBlockDao.updateOrInsertTodoBlock(todoBlock)
        }

    override suspend fun markTodoBlockForDeletion(todoBlockId: Int) {
        applicationCoroutineScope.launch {
            deleteAllTodoBlocksMarkedForDeletion()
            todoBlockDao.markTodoBlockForDeletion(todoBlockId)
        }.join()
    }

    override suspend fun markTodoBlocksOnDateForDeletion(date: LocalDate) {
        applicationCoroutineScope.launch {
            deleteAllTodoBlocksMarkedForDeletion()
            todoBlockDao.markTodoBlocksOnDateForDeletion(date.toEpochDay())
        }.join()
    }

    override suspend fun markTodoBlocksOfScheduleTemplateForDeletion(templateId: Int) {
        applicationCoroutineScope.launch {
            deleteAllTodoBlocksMarkedForDeletion()
            todoBlockDao.markTodoBlocksOfScheduleTemplateForDeletion(templateId)
        }.join()
    }

    override suspend fun unmarkTodoBlockForDeletion(todoBlockId: Int) {
        applicationCoroutineScope.launch {
            val todoBlock = todoBlockDao.getTodoBlockById(todoBlockId).first() ?: return@launch
            val todoBlockIsFromTemplate = todoBlock.templateId != null

            val doesNotOverlapWithOtherTodoBlock =
                if (todoBlockIsFromTemplate)
                    !templateTodoBlockOverlapsWithTodoBlockFromSameTemplate(todoBlock)
                else
                    !todoBlockOverlapsWithOtherTodoBlock(todoBlock)

            if (doesNotOverlapWithOtherTodoBlock)
                todoBlockDao.unmarkTodoBlockForDeletion(todoBlockId)
        }.join()
    }

    override suspend fun unmarkTodoBlocksOfScheduleTemplateForDeletion(templateId: Int) {
        applicationCoroutineScope.launch {
            todoBlockDao.unmarkTodoBlocksOfScheduleTemplateForDeletion(templateId)
        }.join()
    }

    override suspend fun unmarkTodoBlocksOnDateForDeletion(date: LocalDate) {
        applicationCoroutineScope.launch {
            todoBlockDao.unmarkTodoBlocksOnDateForDeletion(date.toEpochDay())
        }.join()
    }

    override suspend fun deleteAllTodoBlocksMarkedForDeletion() {
        applicationCoroutineScope.launch {
            todoBlockDao.deleteAllTodoBlocksMarkedForDeletion()
        }.join()
    }

    override suspend fun deleteTodoBlock(todoBlock: TodoBlock) {
        applicationCoroutineScope.launch {
            todoBlockDao.deleteTodoBlock(todoBlock)
        }.join()
    }

    override suspend fun deleteTodoBlockById(todoBlockId: Int) =
        applicationCoroutineScope.launch {
            todoBlockDao.deleteTodoBlockById(todoBlockId)
        }.join()

    override suspend fun deleteAllTodoBlocksOfScheduleTemplate(templateId: Int) {
        applicationCoroutineScope.launch {
            todoBlockDao.deleteAllTodoBlocksOfScheduleTemplate(templateId)
        }.join()
    }

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