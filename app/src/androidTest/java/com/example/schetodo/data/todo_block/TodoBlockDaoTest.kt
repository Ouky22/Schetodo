package com.example.schetodo.data.todo_block

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.schetodo.data.SchetodoDatabase
import com.example.schetodo.data.schedule_template.ScheduleTemplate
import com.example.schetodo.data.schedule_template.ScheduleTemplateDao
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
    private lateinit var templateDao: ScheduleTemplateDao
    private lateinit var db: SchetodoDatabase

    private val testTime = LocalTime.now().withNano(0)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SchetodoDatabase::class.java).build()
        todoBlockDao = db.todoBlockDao
        templateDao = db.scheduleTemplateDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun unmark_all_todo_blocks_of_template_for_deletion() = runTest {
        val date = LocalDate.of(2023, 2, 1)
        val template1 = ScheduleTemplate(1, "test")
        val template2 = ScheduleTemplate(2, "test")
        val todoBlock1 = TodoBlock(1, null, null, testTime, testTime, template1.templateId)
        val todoBlock2 = TodoBlock(2, null, date, testTime, testTime, null)
        val todoBlock3 = TodoBlock(3, null, null, testTime, testTime, template2.templateId, true)
        templateDao.insert(template1)
        templateDao.insert(template2)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)
        todoBlockDao.insertTodoBlock(todoBlock3)

        todoBlockDao.markTodoBlocksOfScheduleTemplateForDeletion(template1.templateId)
        todoBlockDao.unmarkTodoBlocksOfScheduleTemplateForDeletion(template1.templateId)

        assertThat(todoBlockDao.getAllTodoBlocks().first()).containsExactly(
            todoBlock1, todoBlock2
        )
    }

    @Test
    fun mark_all_todo_blocks_of_template_for_deletion() = runTest {
        val date = LocalDate.of(2023, 2, 1)
        val template1 = ScheduleTemplate(1, "test")
        val template2 = ScheduleTemplate(2, "test")
        val todoBlock1 = TodoBlock(1, null, null, testTime, testTime, template1.templateId)
        val todoBlock2 = TodoBlock(2, null, date, testTime, testTime, null)
        val todoBlock3 = TodoBlock(3, null, null, testTime, testTime, template2.templateId)
        templateDao.insert(template1)
        templateDao.insert(template2)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)
        todoBlockDao.insertTodoBlock(todoBlock3)

        todoBlockDao.markTodoBlocksOfScheduleTemplateForDeletion(template1.templateId)

        assertThat(todoBlockDao.getAllTodoBlocks().first()).containsExactly(todoBlock2, todoBlock3)
    }

    @Test
    fun unmark_all_todo_blocks_on_date_for_deletion() = runTest {
        val date = LocalDate.of(2023, 2, 1)
        val template = ScheduleTemplate(1, "test")
        val todoBlock1 = TodoBlock(1, null, date, testTime, testTime, null)
        val todoBlock2 = TodoBlock(2, null, date, testTime, testTime, null, true)
        val todoBlock3 = TodoBlock(3, null, null, testTime, testTime, template.templateId)
        templateDao.insert(template)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)
        todoBlockDao.insertTodoBlock(todoBlock3)

        todoBlockDao.unmarkTodoBlocksOnDateForDeletion(date.toEpochDay())

        assertThat(todoBlockDao.getAllTodoBlocks().first()).containsExactly(
            todoBlock1, todoBlock2.copy(markedForDeletion = false), todoBlock3
        )
        assertThat(todoBlockDao.getTodoBlocksOnDate(date.toEpochDay()).first()).containsExactly(
            todoBlock1, todoBlock2.copy(markedForDeletion = false)
        )
    }

    @Test
    fun mark_all_todo_blocks_on_date_for_deletion() = runTest {
        val date = LocalDate.of(2023, 2, 1)
        val template = ScheduleTemplate(1, "test")
        val todoBlock1 = TodoBlock(1, null, date, testTime, testTime, null)
        val todoBlock2 = TodoBlock(2, null, date, testTime, testTime, null, true)
        val todoBlock3 = TodoBlock(3, null, null, testTime, testTime, template.templateId)
        templateDao.insert(template)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)
        todoBlockDao.insertTodoBlock(todoBlock3)

        todoBlockDao.markTodoBlocksOnDateForDeletion(date.toEpochDay())

        assertThat(todoBlockDao.getAllTodoBlocks().first()).containsExactly(todoBlock3)
        assertThat(todoBlockDao.getTodoBlocksOnDate(date.toEpochDay()).first()).isEmpty()
    }

    @Test
    fun delete_all_todo_blocks_of_schedule_template() = runTest {
        val template = ScheduleTemplate(1, "st")
        val date = LocalDate.of(2023, 2, 1)
        val todoBlock1 = TodoBlock(1, null, date, testTime, testTime, template.templateId)
        val todoBlock2 = TodoBlock(2, null, date, testTime, testTime, template.templateId)
        templateDao.insert(template)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)

        todoBlockDao.deleteAllTodoBlocksOfScheduleTemplate(template.templateId)

        assertThat(todoBlockDao.getTodoBlockById(todoBlock1.todoBlockId).first()).isNull()
        assertThat(todoBlockDao.getTodoBlockById(todoBlock2.todoBlockId).first()).isNull()
    }

    @Test
    fun when_getting_todo_blocks_then_todo_blocks_marked_for_deletion_are_not_returned() = runTest {
        val date = LocalDate.of(2023, 2, 1)
        val todoBlock1 = TodoBlock(1, null, date, testTime, testTime, null)
        val todoBlock2 = TodoBlock(2, null, date, testTime, testTime, null)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)
        todoBlockDao.markTodoBlockForDeletion(todoBlock1.todoBlockId)

        assertThat(todoBlockDao.getAllTodoBlocks().first()).containsExactly(todoBlock2)
        assertThat(todoBlockDao.getTodoBlocksOnDate(date.toEpochDay()).first())
            .containsExactly(todoBlock2)
    }

    @Test
    fun test_delete_all_todo_blocks_marked_for_deletion() = runTest {
        val todoBlock1 = TodoBlock(1, null, null, testTime, testTime, null)
        val todoBlock2 = TodoBlock(2, "", null, testTime, testTime, null)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)
        todoBlockDao.markTodoBlockForDeletion(todoBlock1.todoBlockId)

        todoBlockDao.deleteAllTodoBlocksMarkedForDeletion()

        assertThat(todoBlockDao.getAllTodoBlocks().first()).containsExactly(todoBlock2)
    }

    @Test
    fun test_mark_todo_block_for_deletion() = runTest {
        val todoBlock1 = TodoBlock(1, null, null, testTime, testTime, null)
        val todoBlock2 = TodoBlock(2, "", null, testTime, testTime, null)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)

        todoBlockDao.markTodoBlockForDeletion(todoBlock1.todoBlockId)

        assertThat(
            todoBlockDao.getTodoBlockById(todoBlock1.todoBlockId).first()?.markedForDeletion
        ).isTrue()
        assertThat(
            todoBlockDao.getTodoBlockById(todoBlock2.todoBlockId).first()?.markedForDeletion
        ).isFalse()
    }

    @Test
    fun test_unmark_todo_block_for_deletion() = runTest {
        val todoBlock1 = TodoBlock(1, null, null, testTime, testTime, null)
        val todoBlock2 = TodoBlock(2, "", null, testTime, testTime, null, markedForDeletion = true)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)

        todoBlockDao.markTodoBlockForDeletion(todoBlock1.todoBlockId)
        todoBlockDao.unmarkTodoBlockForDeletion(todoBlock1.todoBlockId)

        assertThat(
            todoBlockDao.getTodoBlockById(todoBlock1.todoBlockId).first()?.markedForDeletion
        ).isFalse()
        assertThat(
            todoBlockDao.getTodoBlockById(todoBlock2.todoBlockId).first()?.markedForDeletion
        ).isTrue()
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