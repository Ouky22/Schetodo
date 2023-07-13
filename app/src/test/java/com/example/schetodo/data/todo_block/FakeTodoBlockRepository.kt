package com.example.schetodo.data.todo_block

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class FakeTodoBlockRepository : TodoBlockRepository {
    private val todoBlocks = mutableListOf<TodoBlock>()

    override fun getBlockById(todoBlockId: Int): Flow<TodoBlock?> {
        return flow {
            emit(todoBlocks.firstOrNull { it.todoBlockId == todoBlockId })
        }
    }

    override fun getTodoBlocksOnDate(date: LocalDate): Flow<List<TodoBlock>> {
        return flow {
            emit(todoBlocks.filter { it.date == date })
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
        val indexOfTodoBlock = todoBlocks.indexOfFirst { it.todoBlockId == todoBlock.todoBlockId }

        if (indexOfTodoBlock >= 0) {
            val oldTodoBlock = todoBlocks.removeAt(indexOfTodoBlock)
            val updatedTodoBlock = todoBlock.copy(todoBlockId = oldTodoBlock.todoBlockId)
            todoBlocks.add(updatedTodoBlock)
        }
    }

    override suspend fun updateOrInsertTodoBlock(todoBlock: TodoBlock): Long {
        val indexOfTodoBlock = todoBlocks.indexOfFirst { it.todoBlockId == todoBlock.todoBlockId }
        var id = todoBlock.todoBlockId

        if (indexOfTodoBlock >= 0) {
            val oldTodoBlock = todoBlocks.removeAt(indexOfTodoBlock)
            val updatedTodoBlock = todoBlock.copy(todoBlockId = oldTodoBlock.todoBlockId)
            todoBlocks.add(updatedTodoBlock)
            id = oldTodoBlock.todoBlockId
        } else
            todoBlocks.add(todoBlock)

        return id.toLong()
    }

    override suspend fun deleteTodoBlock(todoBlock: TodoBlock) {
        todoBlocks.removeIf { it == todoBlock }
    }

    override suspend fun deleteTodoBlockById(todoBlockId: Int) {
        todoBlocks.removeIf { it.todoBlockId == todoBlockId }
    }

    override suspend fun todoBlockOverlapsWithOtherTodoBlock(
        todoBlock: TodoBlock,
        exceptOfTodoBlockId: Int?
    ): Boolean {
        return false
    }

    override suspend fun markTodoBlockForDeletion(todoBlockId: Int) {
        val indexOfTodoBlockInList = todoBlocks.indexOfFirst { it.todoBlockId == todoBlockId }

        if (indexOfTodoBlockInList == -1)
            return

        val oldTodoBlock = todoBlocks.removeAt(indexOfTodoBlockInList)
        val newTodoBlock = oldTodoBlock.copy(markedForDeletion = true)
        todoBlocks.add(newTodoBlock)
    }

    override suspend fun unmarkTodoBlockForDeletion(todoBlockId: Int) {
        val indexOfTodoBlockInList = todoBlocks.indexOfFirst { it.todoBlockId == todoBlockId }

        if (indexOfTodoBlockInList == -1)
            return

        val oldTodoBlock = todoBlocks.removeAt(indexOfTodoBlockInList)
        val newTodoBlock = oldTodoBlock.copy(markedForDeletion = false)
        todoBlocks.add(newTodoBlock)
    }

    override suspend fun deleteAllTodoBlocksOfScheduleTemplate(templateId: Int) {
        todoBlocks.removeIf { it.templateId == templateId }
    }

    override suspend fun getTodoBlocksThatOverlapWith(
        todoBlock: TodoBlock,
        date: LocalDate
    ): List<TodoBlock> {
        return emptyList()
    }

    override suspend fun templateTodoBlockOverlapsWithTodoBlockFromSameTemplate(todoBlock: TodoBlock): Boolean {
        return false
    }
}