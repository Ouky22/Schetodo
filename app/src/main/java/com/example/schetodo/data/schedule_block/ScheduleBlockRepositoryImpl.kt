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
import com.example.schetodo.data.user_preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


class ScheduleBlockRepositoryImpl @Inject constructor(
    private val scheduleBlockDao: ScheduleBlockDao,
    private val todoBlockCategoryRelationshipDao: TodoBlockCategoryRelationshipDao,
    private val todoBlockTodoRelationshipDao: TodoBlockTodoRelationshipDao,
    private val todoBlockDao: TodoBlockDao,
    private val todoDao: TodoDao,
    private val notificationRepository: NotificationRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ScheduleBlockRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            todoBlockDao.deleteAllTodoBlocksMarkedForDeletion()
        }
    }

    override suspend fun unmarkTodoBlockForDeletion(todoBlockId: Int) {
        todoBlockDao.unmarkTodoBlockForDeletion(todoBlockId)
    }

    override fun getScheduleBlocksOnDate(date: LocalDate): Flow<List<ScheduleBlock>> =
        scheduleBlockDao.getScheduleBlocksOnDate(date.toEpochDay())
            .map { scheduleBlocks -> removeTodosMarkedForDeletion(scheduleBlocks) }
            .map { scheduleBlocks -> removeTodoCategoriesMarkedForDeletion(scheduleBlocks) }

    override fun getScheduleBlockByTodoBlockId(todoBlockId: Int): Flow<ScheduleBlock?> =
        scheduleBlockDao.getScheduleBlockByTodoBlockId(todoBlockId)
            .map { scheduleBlock -> scheduleBlock?.let { removeTodosMarkedForDeletion(it) } }
            .map { scheduleBlock -> scheduleBlock?.let { removeTodoCategoriesMarkedForDeletion(it) } }

    override fun getScheduleBlocksOfScheduleTemplate(templateId: Int): Flow<List<ScheduleBlock>> =
        scheduleBlockDao.getScheduleBlocksOfScheduleTemplate(templateId)
            .map { scheduleBlocks -> removeTodosMarkedForDeletion(scheduleBlocks) }
            .map { scheduleBlocks -> removeTodoCategoriesMarkedForDeletion(scheduleBlocks) }

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
        setNotificationPreferences(scheduleBlock)
    }

    override val showScheduleBlockNotificationAtBeginning =
        userPreferencesRepository.showScheduleBlockNotificationAtBeginning

    override val showScheduleBlockNotificationAtEnd =
        userPreferencesRepository.showScheduleBlockNotificationAtEnd

    private fun removeTodosMarkedForDeletion(scheduleBlocks: List<ScheduleBlock>): List<ScheduleBlock> =
        scheduleBlocks.map { scheduleBlock ->
            removeTodosMarkedForDeletion(scheduleBlock)
        }

    private fun removeTodoCategoriesMarkedForDeletion(scheduleBlocks: List<ScheduleBlock>): List<ScheduleBlock> =
        scheduleBlocks.map { scheduleBlock ->
            removeTodoCategoriesMarkedForDeletion(scheduleBlock)
        }

    private fun removeTodosMarkedForDeletion(scheduleBlock: ScheduleBlock): ScheduleBlock =
        scheduleBlock.copy(
            todos = scheduleBlock.todos.filter { !it.markedForDeletion }
        )

    private fun removeTodoCategoriesMarkedForDeletion(scheduleBlock: ScheduleBlock): ScheduleBlock =
        scheduleBlock.copy(
            todoCategories = scheduleBlock.todoCategories.filter { !it.markedForDeletion }
        )

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

    private suspend fun setNotificationPreferences(scheduleBlock: ScheduleBlock) {
        val showNotificationAtBeginning = scheduleBlock.notifications.any {
            it.dateTime.toLocalTime() == scheduleBlock.todoBlock.startTime
        }
        userPreferencesRepository.setShowScheduleBlockNotificationAtBeginning(
            showNotificationAtBeginning
        )

        val showNotificationAtEnd = scheduleBlock.notifications.any {
            it.dateTime.toLocalTime() == scheduleBlock.todoBlock.endTime
        }
        userPreferencesRepository.setShowScheduleBlockNotificationAtEnd(showNotificationAtEnd)
    }
}