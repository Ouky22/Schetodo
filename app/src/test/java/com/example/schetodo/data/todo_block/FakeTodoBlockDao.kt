package com.example.schetodo.data.todo_block

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTodoBlockDao : TodoBlockDao {
    private var todoBlocks = mutableListOf<TodoBlock>()

    override fun getTodoBlockById(todoBlockId: Int): Flow<TodoBlock?> {
        return flow {
            emit(todoBlocks.firstOrNull { it.todoBlockId == todoBlockId })
        }
    }

    override fun getTodoBlocksOnDate(dateStampInDays: Long): Flow<List<TodoBlock>> {
        return flow {
            emit(todoBlocks.filter { it.date?.toEpochDay() == dateStampInDays })
        }
    }

    override fun getAllTodoBlocks(): Flow<List<TodoBlock>> {
        return flow {
            emit(todoBlocks)
        }
    }

    override suspend fun insertTodoBlock(todoBlock: TodoBlock): Long {
        todoBlocks.add(todoBlock)
        return todoBlock.todoBlockId.toLong()
    }

    override suspend fun updateTodoBlock(todoBlock: TodoBlock) {
        val indexOfTodoBlockInList =
            todoBlocks.indexOfFirst { it.todoBlockId == todoBlock.todoBlockId }

        if (indexOfTodoBlockInList >= 0) {
            todoBlocks.removeAt(indexOfTodoBlockInList)
            todoBlocks.add(todoBlock)
        }
    }

    override suspend fun updateOrInsertTodoBlock(todoBlock: TodoBlock): Long {
        todoBlocks.removeIf { it.todoBlockId == todoBlock.todoBlockId }
        todoBlocks.add(todoBlock)
        return todoBlock.todoBlockId.toLong()
    }

    override suspend fun deleteTodoBlock(todoBlock: TodoBlock) {
        todoBlocks.removeIf { it.todoBlockId == todoBlock.todoBlockId }
    }

    override suspend fun deleteTodoBlockById(todoBlockId: Int) {
        todoBlocks.removeIf { it.todoBlockId == todoBlockId }
    }

    override suspend fun markTodoBlockForDeletion(todoBlockId: Int) {
        val indexOfTodoBlockInList = todoBlocks.indexOfFirst { it.todoBlockId == todoBlockId }

        if (indexOfTodoBlockInList == -1)
            return

        val oldTodoBlock = todoBlocks.removeAt(indexOfTodoBlockInList)
        val newTodoBlock = oldTodoBlock.copy(markedForDeletion = true)
        todoBlocks.add(newTodoBlock)
    }

    override suspend fun markTodoBlocksOnDateForDeletion(dateStampInDays: Long) {
        todoBlocks = todoBlocks.map { todoBlock ->
            if (todoBlock.date?.toEpochDay() == dateStampInDays)
                todoBlock.copy(markedForDeletion = true)
            else
                todoBlock
        }.toMutableList()
    }

    override suspend fun markTodoBlocksOfScheduleTemplateForDeletion(templateId: Int) {
        todoBlocks = todoBlocks.map { todoBlock ->
            if (todoBlock.templateId == templateId)
                todoBlock.copy(markedForDeletion = true)
            else
                todoBlock
        }.toMutableList()
    }

    override suspend fun unmarkTodoBlocksOfScheduleTemplateForDeletion(templateId: Int) {
        todoBlocks = todoBlocks.map { todoBlock ->
            if (todoBlock.templateId == templateId)
                todoBlock.copy(markedForDeletion = false)
            else
                todoBlock
        }.toMutableList()
    }

    override suspend fun unmarkTodoBlocksOnDateForDeletion(dateStampInDays: Long) {
        todoBlocks = todoBlocks.map { todoBlock ->
            if (todoBlock.date?.toEpochDay() == dateStampInDays)
                todoBlock.copy(markedForDeletion = false)
            else
                todoBlock
        }.toMutableList()
    }

    override suspend fun unmarkTodoBlockForDeletion(todoBlockId: Int) {
        val indexOfTodoBlockInList = todoBlocks.indexOfFirst { it.todoBlockId == todoBlockId }

        if (indexOfTodoBlockInList == -1)
            return

        val oldTodoBlock = todoBlocks.removeAt(indexOfTodoBlockInList)
        val newTodoBlock = oldTodoBlock.copy(markedForDeletion = false)
        todoBlocks.add(newTodoBlock)
    }

    override suspend fun deleteAllTodoBlocksMarkedForDeletion() {
        todoBlocks.removeIf { it.markedForDeletion }
    }

    override suspend fun deleteAllTodoBlocksOfScheduleTemplate(templateId: Int) {
        todoBlocks.removeIf { it.templateId == templateId }
    }
}