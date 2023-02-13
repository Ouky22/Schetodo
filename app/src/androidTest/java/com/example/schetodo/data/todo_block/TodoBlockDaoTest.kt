package com.example.schetodo.data.todo_block

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.schetodo.data.SchetodoDatabase
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

@ExperimentalCoroutinesApi
class TodoBlockDaoTest {
    private lateinit var todoBlockDao: TodoBlockDao
    private lateinit var db: SchetodoDatabase

    private val testTime = LocalTime.now().withNano(0)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SchetodoDatabase::class.java).build()
        todoBlockDao = db.todoBlockDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun when_deleting_todo_block_by_id_and_todo_block_exists_then_delete_it() = runTest {
        val todoBlock1 = TodoBlock(1, null, null, testTime, testTime, null)
        val todoBlock2 =
            TodoBlock(2, "", LocalDate.of(2023, 2, 1), testTime, testTime, null)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)

        todoBlockDao.deleteTodoBlockById(todoBlock1.todoBlockId)

        val allTodoBlocks = todoBlockDao.getAllTodoBlocks().first()
        assertThat(allTodoBlocks.size).isEqualTo(1)
        assertThat(allTodoBlocks).contains(todoBlock2)
    }

    @Test
    fun when_deleting_todo_block_by_id_and_todo_block_not_exists_then_do_nothing() = runTest {
        val todoBlock1 = TodoBlock(1, null, null, testTime, testTime, null)
        todoBlockDao.insertTodoBlock(todoBlock1)

        todoBlockDao.deleteTodoBlockById(2)

        val allTodoBlocks = todoBlockDao.getAllTodoBlocks().first()
        assertThat(allTodoBlocks.size).isEqualTo(1)
        assertThat(allTodoBlocks).contains(todoBlock1)
    }

    @Test
    fun when_getting_todos_on_specific_date_and_there_are_no_todo_blocks_then_return_empty_list() =
        runTest {
            val todoBlock1 = TodoBlock(1, "test", null, testTime, testTime, null)
            val todoBlock2 =
                TodoBlock(2, null, LocalDate.of(2023, 2, 1), testTime, testTime, null)
            todoBlockDao.insertTodoBlock(todoBlock1)
            todoBlockDao.insertTodoBlock(todoBlock2)

            val dateStampWithNoTodoBlocks = LocalDate.of(2023, 2, 2).toEpochDay()
            assertThat(
                todoBlockDao.getTodoBlocksOnDate(dateStampWithNoTodoBlocks).first()
            ).isEmpty()
        }

    @Test
    fun when_getting_todos_on_specific_date_and_there_are_todo_blocks_then_return_them() = runTest {
        val dateWithTodoBlocks = LocalDate.of(2023, 2, 1)
        val todoBlock1 =
            TodoBlock(1, "test", dateWithTodoBlocks, testTime, testTime, null)
        val todoBlock2 =
            TodoBlock(
                2, null, dateWithTodoBlocks.plusDays(1), testTime, testTime, null
            )
        val todoBlock3 = TodoBlock(3, null, null, testTime, testTime, null)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)
        todoBlockDao.insertTodoBlock(todoBlock3)

        val todoBlocksOnDate =
            todoBlockDao.getTodoBlocksOnDate(dateWithTodoBlocks.toEpochDay()).first()
        assertThat(todoBlocksOnDate.size).isEqualTo(1)
        assertThat(todoBlocksOnDate).contains(todoBlock1)
    }

    @Test
    fun when_getting_todo_block_by_id_and_id_exists_then_return_todo_block() = runTest {
        val todoBlock1 = TodoBlock(1, "test", null, testTime, testTime, null)
        val todoBlock2 = TodoBlock(2, null, null, testTime, testTime, null)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)

        assertThat(
            todoBlockDao.getTodoBlockById(todoBlock1.todoBlockId).first()
        ).isEqualTo(todoBlock1)
    }

    @Test
    fun when_getting_todo_block_by_id_and_id_not_exists_then_return_null() = runTest {
        assertThat(
            todoBlockDao.getTodoBlockById(1).first()
        ).isEqualTo(null)
    }
}