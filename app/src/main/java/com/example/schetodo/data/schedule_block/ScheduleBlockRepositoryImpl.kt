package com.example.schetodo.data.schedule_block

import com.example.schetodo.data.relationship.TodoBlockCategoryRelationshipDao
import com.example.schetodo.data.relationship.TodoBlockTodoRelationshipDao
import com.example.schetodo.data.todo.TodoDao
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo_block.TodoBlockDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject


class ScheduleBlockRepositoryImpl @Inject constructor(
    private val scheduleBlockDao: ScheduleBlockDao,
    private val todoBlockCategoryRelationshipDao: TodoBlockCategoryRelationshipDao,
    private val todoBlockTodoRelationshipDao: TodoBlockTodoRelationshipDao,
    private val todoBlockDao: TodoBlockDao,
    private val todoDao: TodoDao
) : ScheduleBlockRepository {

    override fun getScheduleBlocksOnDate(date: LocalDate) =
        scheduleBlockDao.getScheduleBlocksOnDate(date.toEpochDay())

    override fun getScheduleBlockByTodoBlockId(todoBlockId: Int): Flow<ScheduleBlock?> =
        scheduleBlockDao.getScheduleBlockByTodoBlockId(todoBlockId)

    override suspend fun insertOrUpdateScheduleBlock(scheduleBlock: ScheduleBlock) {
        val todoBlock = scheduleBlock.todoBlock
        var todoBlockId = todoBlockDao.updateOrInsertTodoBlock(todoBlock).toInt()

        val todoBlockInserted = todoBlockId <= 0
        if (todoBlockInserted)
            todoBlockId = todoBlock.todoBlockId

        todoBlockTodoRelationshipDao.disconnectAllTodosFromTodoBlock(todoBlock.todoBlockId)
        scheduleBlock.todos.forEach {
            todoBlockTodoRelationshipDao.connectTodoBlockAndTodo(todoBlockId, it.todoId)
        }

        todoBlockCategoryRelationshipDao.disconnectAllTodoCategoriesFromTodoBlock(todoBlock.todoBlockId)
        scheduleBlock.todoCategories.forEach {
            todoBlockCategoryRelationshipDao.connectTodoBlockAndTodoCategory(
                todoBlockId, it.categoryId
            )
        }

        scheduleBlock.todos.forEach {
            if (it.flag != TodoFlag.RECURRING)
                todoDao.updateTodo(it.copy(flag = TodoFlag.IN_PROGRESS))
        }
    }
}