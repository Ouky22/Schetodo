package com.example.schetodo.data.schedule_block

import com.example.schetodo.data.notification.FakeNotificationRepository
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.relationship.FakeTodoBlockCategoryRelationshipDao
import com.example.schetodo.data.relationship.FakeTodoBlockTodoRelationshipDao
import com.example.schetodo.data.todo.FakeTodoDao
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_block.FakeTodoBlockDao
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.user_preferences.FakeUserPreferencesRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
internal class ScheduleBlockRepositoryImplTest {

    private val fakeScheduleBlockDao = FakeScheduleBlockDao()
    private val fakeTodoBlockCategoryRelationshipDao = FakeTodoBlockCategoryRelationshipDao()
    private val fakeTodoBlockTodoRelationshipDao = FakeTodoBlockTodoRelationshipDao()
    private val fakeTodoBlockDao = FakeTodoBlockDao()
    private val fakeTodoDao = FakeTodoDao()
    private val fakeNotificationRepository = FakeNotificationRepository()
    private val fakeUserPreferencesRepository = FakeUserPreferencesRepository()
    private val scheduleBlockRepository =
        ScheduleBlockRepositoryImpl(
            fakeScheduleBlockDao,
            fakeTodoBlockCategoryRelationshipDao,
            fakeTodoBlockTodoRelationshipDao,
            fakeTodoBlockDao,
            fakeTodoDao,
            fakeNotificationRepository,
            fakeUserPreferencesRepository
        )

    private val startTime = LocalTime.of(10, 15)
    private val endTime = LocalTime.of(11, 20)
    private val date = LocalDate.of(2023, 2, 1)
    private val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.DONE, 1)
    private val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.UNDONE, 1)
    private val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.IN_PROGRESS, 1)
    private val todo4 = Todo(4, "t4", TodoPriority.LOW, TodoFlag.RECURRING, 1)
    private val todoBlock = TodoBlock(
        1, "n1", date, startTime, endTime, null
    )

    @Test
    fun `when getting schedule blocks then todos marked for deletion are not returned`() = runTest {
        val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.DONE, 1)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.DONE, 1, true)
        fakeTodoDao.insertTodo(todo1)
        fakeTodoDao.insertTodo(todo2)
        val scheduleBlock = ScheduleBlock(todoBlock, listOf(todo1, todo2), emptyList())
        scheduleBlockRepository.insertOrUpdateScheduleBlock(scheduleBlock)
        fakeScheduleBlockDao.insertScheduleBlock(scheduleBlock)

        val allScheduleBlocks =
            scheduleBlockRepository.getScheduleBlocksOnDate(date).first()
        assertThat(allScheduleBlocks.size).isEqualTo(1)
        assertThat(allScheduleBlocks[0].todos).containsExactly(todo1)
    }

    @Test
    fun `when getting schedule blocks then todo categories marked for deletion are not returned`() =
        runTest {
            val category1 = TodoCategory(1, "c1", 0, 1, "")
            val category2 = TodoCategory(2, "c2", 0, 1, "", true)
            val scheduleBlock = ScheduleBlock(todoBlock, emptyList(), listOf(category1, category2))
            scheduleBlockRepository.insertOrUpdateScheduleBlock(scheduleBlock)
            fakeScheduleBlockDao.insertScheduleBlock(scheduleBlock)

            val allScheduleBlocks =
                scheduleBlockRepository.getScheduleBlocksOnDate(date).first()
            assertThat(allScheduleBlocks.size).isEqualTo(1)
            assertThat(allScheduleBlocks[0].todoCategories).containsExactly(category1)
        }

    @Test
    fun `when inserting schedule block then all containing non recurring todos get IN_PROGRESS flag`() =
        runTest {
            fakeTodoDao.insertTodo(todo1)
            fakeTodoDao.insertTodo(todo2)
            fakeTodoDao.insertTodo(todo3)
            fakeTodoDao.insertTodo(todo4)
            val scheduleBlock =
                ScheduleBlock(todoBlock, listOf(todo1, todo2, todo3, todo4), emptyList())
            scheduleBlockRepository.insertOrUpdateScheduleBlock(scheduleBlock)

            val allTodos = fakeTodoDao.getAllTodos().first()
            allTodos.forEach {
                if (it.flag != TodoFlag.RECURRING)
                    assertThat(it.flag).isEqualTo(TodoFlag.IN_PROGRESS)
            }
        }

    @Test
    fun `when saving schedule block and notification at beginning then save it as user preference`() =
        runTest {
            fakeTodoDao.insertTodo(todo1)
            fakeTodoDao.insertTodo(todo2)
            fakeTodoDao.insertTodo(todo3)
            fakeTodoDao.insertTodo(todo4)
            val notifications = listOf(
                Notification(1, LocalDateTime.of(date, startTime)),
                Notification(2, LocalDateTime.of(date, endTime.minusMinutes(1)))
            )
            val scheduleBlock =
                ScheduleBlock(
                    todoBlock,
                    listOf(todo1, todo2, todo3, todo4),
                    emptyList(),
                    notifications
                )
            scheduleBlockRepository.insertOrUpdateScheduleBlock(scheduleBlock)

            assertTrue(
                fakeUserPreferencesRepository.showScheduleBlockNotificationAtBeginning.first()
            )
            assertFalse(
                fakeUserPreferencesRepository.showScheduleBlockNotificationAtEnd.first()
            )
        }
}