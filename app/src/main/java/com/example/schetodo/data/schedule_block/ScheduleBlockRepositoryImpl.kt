package com.example.schetodo.data.schedule_block

import android.util.Log
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.notification.NotificationRepository
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationship
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationshipDao
import com.example.schetodo.data.relationship.TodoBlockTodoRelationship
import com.example.schetodo.data.relationship.TodoBlockTodoRelationshipDao
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoDao
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo_block.TodoBlockRepository
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.user_preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


class ScheduleBlockRepositoryImpl @Inject constructor(
    private val scheduleBlockDao: ScheduleBlockDao,
    private val todoBlockCategoryRelationshipDao: TodoBlockCategoryRelationshipDao,
    private val todoBlockTodoRelationshipDao: TodoBlockTodoRelationshipDao,
    private val todoBlockRepository: TodoBlockRepository,
    private val todoDao: TodoDao,
    private val notificationRepository: NotificationRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ScheduleBlockRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            todoBlockRepository.deleteAllTodoBlocksMarkedForDeletion()
        }
    }

    override suspend fun unmarkTodoBlockForDeletion(todoBlockId: Int) {
        todoBlockRepository.unmarkTodoBlockForDeletion(todoBlockId)
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
        var todoBlockId =
            todoBlockRepository.updateOrInsertTodoBlock(scheduleBlock.todoBlock).toInt()

        // updateOrInsertTodoBlock returns -1 when todoBlock is updated
        val todoBlockUpdated = todoBlockId <= 0
        if (todoBlockUpdated)
            todoBlockId = scheduleBlock.todoBlock.todoBlockId

        val otherStuffStart = System.currentTimeMillis()

        var start = System.currentTimeMillis()
        connectTodoBlockAndTodos(todoBlockId, scheduleBlock.todos, todoBlockUpdated)
        var time = System.currentTimeMillis() - start
        Log.d("testing", "$time ms for connecting todo block and todo")

        start = System.currentTimeMillis()
        setFlagOfTodosToInProgress(scheduleBlock.todos)
        time = System.currentTimeMillis() - start
        Log.d("testing", "$time ms for setting flag of todos")

        start = System.currentTimeMillis()
        connectTodoBlockAndTodoCategories(
            todoBlockId, scheduleBlock.todoCategories, todoBlockUpdated
        )
        time = System.currentTimeMillis() - start
        Log.d("testing", "$time ms for connecting todo block and categories")

        start = System.currentTimeMillis()
        setNotificationsOfTodoBlock(todoBlockId, scheduleBlock.notifications, todoBlockUpdated)
        time = System.currentTimeMillis() - start
        Log.d("testing", "$time ms for setting notifications")

        start = System.currentTimeMillis()
        setNotificationPreferences(scheduleBlock)
        time = System.currentTimeMillis() - start
        Log.d("testing", "$time ms for setting notification preferences")

        val otherStuffEnd = System.currentTimeMillis() - otherStuffStart
        Log.d("testing", "$otherStuffEnd ms for inserting other stuff")
        Log.d("testing", "---------------------------------------------")
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
        todos: List<Todo>,
        todoBlockUpdated: Boolean
    ) {
        if (todoBlockUpdated)
            todoBlockTodoRelationshipDao.disconnectAllTodosFromTodoBlock(todoBlockId)

        if (todos.isNotEmpty())
            todoBlockTodoRelationshipDao.connectTodoBlocksAndTodos(todos.map { todo ->
                TodoBlockTodoRelationship(todoBlockId, todo.todoId)
            })
    }

    private suspend fun connectTodoBlockAndTodoCategories(
        todoBlockId: Int,
        todoCategories: List<TodoCategory>,
        todoBlockUpdated: Boolean
    ) {
        if (todoBlockUpdated)
            todoBlockCategoryRelationshipDao.disconnectAllTodoCategoriesFromTodoBlock(todoBlockId)

        if (todoCategories.isNotEmpty())
            todoBlockCategoryRelationshipDao.connectTodoBlocksAndTodoCategories(
                todoCategories.map { category ->
                    TodoBlockCategoryRelationship(todoBlockId, category.categoryId)
                }
            )
    }

    private suspend fun setFlagOfTodosToInProgress(todos: List<Todo>) {
        if (todos.isNotEmpty())
            todoDao.updateTodos(
                todos.filter { it.flag != TodoFlag.RECURRING }
                    .map { it.copy(flag = TodoFlag.IN_PROGRESS) }
            )
    }

    private suspend fun setNotificationsOfTodoBlock(
        todoBlockId: Int,
        notifications: List<Notification>,
        todoBlockUpdated: Boolean
    ) {
        if (todoBlockUpdated)
            notificationRepository.updateNotificationsOfTodoBlock(todoBlockId, notifications)
        else if (notifications.isNotEmpty())
            notificationRepository.insertNotifications(notifications)
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