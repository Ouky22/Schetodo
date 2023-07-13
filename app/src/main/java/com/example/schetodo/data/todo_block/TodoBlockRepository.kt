package com.example.schetodo.data.todo_block

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TodoBlockRepository {
    fun getBlockById(todoBlockId: Int): Flow<TodoBlock?>
    fun getTodoBlocksOnDate(date: LocalDate): Flow<List<TodoBlock>>
    fun getAllTodoBlocks(): Flow<List<TodoBlock>>
    suspend fun insertTodoBlock(todoBlock: TodoBlock): Long
    suspend fun updateTodoBlock(todoBlock: TodoBlock)
    suspend fun updateOrInsertTodoBlock(todoBlock: TodoBlock): Long
    suspend fun deleteTodoBlock(todoBlock: TodoBlock)
    suspend fun deleteTodoBlockById(todoBlockId: Int)
    suspend fun todoBlockOverlapsWithOtherTodoBlock(todoBlock: TodoBlock): Boolean
    suspend fun markTodoBlockForDeletion(todoBlockId: Int)
    suspend fun unmarkTodoBlockForDeletion(todoBlockId: Int)
    suspend fun deleteAllTodoBlocksOfScheduleTemplate(templateId: Int)
    suspend fun getTodoBlocksThatOverlapWith(todoBlock: TodoBlock, date: LocalDate): List<TodoBlock>
    suspend fun templateTodoBlockOverlapsWithTodoBlockFromSameTemplate(todoBlock: TodoBlock): Boolean
}