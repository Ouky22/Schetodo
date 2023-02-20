package com.example.schetodo.data.schedule_block

import com.example.schetodo.data.relationship.FakeTodoBlockCategoryRelationshipDao
import com.example.schetodo.data.relationship.FakeTodoBlockTodoRelationshipDao
import com.example.schetodo.data.todo.FakeTodoDao
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_block.FakeTodoBlockDao
import com.example.schetodo.data.todo_block.TodoBlock
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
internal class ScheduleBlockRepositoryImplTest {

    private val fakeScheduleBlockDao = FakeScheduleBlockDao()
    private val fakeTodoBlockCategoryRelationshipDao = FakeTodoBlockCategoryRelationshipDao()
    private val fakeTodoBlockTodoRelationshipDao = FakeTodoBlockTodoRelationshipDao()
    private val fakeTodoBlockDao = FakeTodoBlockDao()
    private val fakeTodoDao = FakeTodoDao()
    private val scheduleBlockRepository =
        ScheduleBlockRepositoryImpl(
            fakeScheduleBlockDao,
            fakeTodoBlockCategoryRelationshipDao,
            fakeTodoBlockTodoRelationshipDao,
            fakeTodoBlockDao,
            fakeTodoDao
        )

    @Test
    fun `when inserting schedule block then all containing non recurring todos get IN_PROGRESS flag`() =
        runTest {
            val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.DONE, 1)
            val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.UNDONE, 1)
            val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.IN_PROGRESS, 1)
            val todo4 = Todo(4, "t4", TodoPriority.LOW, TodoFlag.RECURRING, 1)
            val todoBlock = TodoBlock(
                1, "n1", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(1), null
            )
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
    fun `test updating schedule block`() {

    }
}