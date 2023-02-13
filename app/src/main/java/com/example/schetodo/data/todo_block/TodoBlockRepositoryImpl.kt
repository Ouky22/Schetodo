package com.example.schetodo.data.todo_block

import kotlinx.coroutines.flow.Flow
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
}