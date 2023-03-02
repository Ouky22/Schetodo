package com.example.schetodo.data.schedule_block

import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.notification.NotificationRepository
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationshipDao
import com.example.schetodo.data.relationship.TodoBlockTodoRelationshipDao
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoDao
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo_block.TodoBlockDao
import com.example.schetodo.data.todo_category.TodoCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject


class ScheduleBlockRepositoryImpl @Inject constructor(
    private val scheduleBlockDao: ScheduleBlockDao,
    private val todoBlockCategoryRelationshipDao: TodoBlockCategoryRelationshipDao,
    private val todoBlockTodoRelationshipDao: TodoBlockTodoRelationshipDao,
    private val todoBlockDao: TodoBlockDao,
    private val todoDao: TodoDao,
    private val notificationRepository: NotificationRepository
) : ScheduleBlockRepository {

    override fun getScheduleBlocksOnDate(date: LocalDate) =
        scheduleBlockDao.getScheduleBlocksOnDate(date.toEpochDay())

    override fun getScheduleBlockByTodoBlockId(todoBlockId: Int): Flow<ScheduleBlock?> =
        scheduleBlockDao.getScheduleBlockByTodoBlockId(todoBlockId)

    override suspend fun insertOrUpdateScheduleBlock(scheduleBlock: ScheduleBlock) {
        var todoBlockId = todoBlockDao.updateOrInsertTodoBlock(scheduleBlock.todoBlock).toInt()

        // updateOrInsertTodoBlock returns -1 when todoBlock is updated
        val todoBlockUpdated = todoBlockId <= 0
        if (todoBlockUpdated)
            todoBlockId = scheduleBlock.todoBlock.todoBlockId

        connectTodoBlockAndTodos(todoBlockId, scheduleBlock.todos)
        setFlagOfTodosToInProgress(scheduleBlock.todos)
        connectTodoBlockAndTodoCategories(todoBlockId, scheduleBlock.todoCategories)
        setNotificationsOfTodoBlock(todoBlockId, scheduleBlock.notifications)
    }

    private suspend fun connectTodoBlockAndTodos(
        todoBlockId: Int,
        todos: List<Todo>
    ) {
        todoBlockTodoRelationshipDao.disconnectAllTodosFromTodoBlock(todoBlockId)
        todos.forEach {
            todoBlockTodoRelationshipDao.connectTodoBlockAndTodo(todoBlockId, it.todoId)
        }
    }

    private suspend fun connectTodoBlockAndTodoCategories(
        todoBlockId: Int,
        todoCategories: List<TodoCategory>
    ) {
        todoBlockCategoryRelationshipDao.disconnectAllTodoCategoriesFromTodoBlock(todoBlockId)
        todoCategories.forEach {
            todoBlockCategoryRelationshipDao.connectTodoBlockAndTodoCategory(
                todoBlockId, it.categoryId
            )
        }
    }

    private suspend fun setFlagOfTodosToInProgress(todos: List<Todo>) {
        todos.forEach {
            if (it.flag != TodoFlag.RECURRING)
                todoDao.updateTodo(it.copy(flag = TodoFlag.IN_PROGRESS))
        }
    }

    private suspend fun setNotificationsOfTodoBlock(
        todoBlockId: Int,
        notifications: List<Notification>
    ) {
        notificationRepository.setNotificationsOfTodoBlock(
            todoBlockId = todoBlockId,
            notifications = notifications.map { it.copy(todoBlockId = todoBlockId) }
        )
    }
}