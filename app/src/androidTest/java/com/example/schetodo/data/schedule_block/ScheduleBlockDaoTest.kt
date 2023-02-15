package com.example.schetodo.data.schedule_block

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.schetodo.data.SchetodoDatabase
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationshipDao
import com.example.schetodo.data.relationship.TodoBlockTodoRelationshipDao
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoDao
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_block.TodoBlockDao
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.todo_category.TodoCategoryDao
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleBlockDaoTest {
    private lateinit var todoBlockDao: TodoBlockDao
    private lateinit var todoDao: TodoDao
    private lateinit var todoCategoryDao: TodoCategoryDao
    private lateinit var todoBlockTodoRelationshipDao: TodoBlockTodoRelationshipDao
    private lateinit var todoBlockTodoCategoryRelationshipDao: TodoBlockCategoryRelationshipDao
    private lateinit var scheduleBlockDao: ScheduleBlockDao
    private lateinit var db: SchetodoDatabase

    // test data
    private lateinit var todo1: Todo
    private lateinit var todo2: Todo
    private lateinit var todo3: Todo
    private lateinit var todoCategory1: TodoCategory
    private lateinit var todoCategory2: TodoCategory
    private lateinit var todoCategory3: TodoCategory
    private lateinit var todoBlock1: TodoBlock
    private lateinit var todoBlock2: TodoBlock
    private lateinit var scheduleBlock1: ScheduleBlock
    private lateinit var scheduleBlock2: ScheduleBlock
    private val testTime = LocalTime.now().withNano(0)


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SchetodoDatabase::class.java).build()
        scheduleBlockDao = db.scheduleBlockDao
        todoBlockDao = db.todoBlockDao
        todoDao = db.todoDao
        todoCategoryDao = db.todoCategoryDao
        todoBlockTodoRelationshipDao = db.todoBlockTodoRelationshipDao
        todoBlockTodoCategoryRelationshipDao = db.todoBlockCategoryRelationshipDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun when_getting_schedule_block_by_todo_block_id_and_id_invalid_then_return_null() =
        runTest {
            initDbWithTestData()

            val scheduleBlock =
                scheduleBlockDao.getScheduleBlockByTodoBlockId(10).first()

            assertThat(scheduleBlock).isNull()
        }

    @Test
    fun when_getting_schedule_block_by_todo_block_id_and_id_valid_then_return_schedule_block() =
        runTest {
            initDbWithTestData()

            val scheduleBlock =
                scheduleBlockDao.getScheduleBlockByTodoBlockId(todoBlock1.todoBlockId).first()

            assertThat(scheduleBlock).isEqualTo(scheduleBlock1)
        }

    @Test
    fun test_getting_todo_block_with_its_todos_and_categories_on_date() = runTest {
        initDbWithTestData()

        val date = todoBlock1.date!!
        val scheduleBlocksOnDate =
            scheduleBlockDao.getScheduleBlocksOnDate(date.toEpochDay()).first()

        assertThat(scheduleBlocksOnDate).containsExactly(scheduleBlock1)
    }

    @Test
    fun test_getting_todo_block_with_its_todos_and_categories() = runTest {
        initDbWithTestData()

        val scheduleBlocks = scheduleBlockDao.getScheduleBlocks().first()

        assertThat(scheduleBlocks).containsExactly(scheduleBlock1, scheduleBlock2)
    }


    private suspend fun initDbWithTestData() {
        todoCategory1 = TodoCategory(1, "category 1", 0, null, "")
        todoCategory2 = TodoCategory(2, "category 2", 0, null, "")
        todoCategory3 = TodoCategory(3, "category 3", 0, todoCategory2.categoryId, "")

        todo1 = Todo(1, "todo 1", TodoPriority.HIGH, TodoFlag.DONE, todoCategory1.categoryId)
        todo2 = Todo(2, "todo 1", TodoPriority.HIGH, TodoFlag.DONE, todoCategory1.categoryId)
        todo3 = Todo(3, "todo 1", TodoPriority.HIGH, TodoFlag.DONE, todoCategory3.categoryId)

        val date = LocalDate.of(2023, 2, 1)
        todoBlock1 = TodoBlock(1, null, date, testTime, testTime, null)
        todoBlock2 = TodoBlock(2, null, null, testTime, testTime, null)

        todoCategoryDao.insertTodoCategory(todoCategory1)
        todoCategoryDao.insertTodoCategory(todoCategory2)
        todoCategoryDao.insertTodoCategory(todoCategory3)

        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)
        todoDao.insertTodo(todo3)

        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)

        scheduleBlock1 = ScheduleBlock(
            todoBlock1, listOf(todo1, todo2, todo3), listOf(todoCategory1)
        )
        scheduleBlock1.todos.forEach {
            todoBlockTodoRelationshipDao.connectTodoBlockAndTodo(
                scheduleBlock1.todoBlock.todoBlockId, it.todoId
            )
        }
        scheduleBlock1.todoCategories.forEach {
            todoBlockTodoCategoryRelationshipDao.connectTodoBlockAndTodoCategory(
                scheduleBlock1.todoBlock.todoBlockId, it.categoryId
            )
        }

        scheduleBlock2 = ScheduleBlock(
            todoBlock2, emptyList(), emptyList()
        )
    }
}