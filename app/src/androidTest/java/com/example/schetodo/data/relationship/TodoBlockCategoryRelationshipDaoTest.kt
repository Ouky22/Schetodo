package com.example.schetodo.data.relationship

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.schetodo.data.SchetodoDatabase
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
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class TodoBlockCategoryRelationshipDaoTest {
    private lateinit var todoBlockDao: TodoBlockDao
    private lateinit var todoCategoryDao: TodoCategoryDao
    private lateinit var todoBlockTodoCategoryRelationshipDao: TodoBlockCategoryRelationshipDao
    private lateinit var db: SchetodoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SchetodoDatabase::class.java).build()
        todoBlockDao = db.todoBlockDao
        todoCategoryDao = db.todoCategoryDao
        todoBlockTodoCategoryRelationshipDao = db.todoBlockCategoryRelationshipDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun test_disconnecting_all_todo_categories_of_todo_block() = runTest {
        val todoCategory1 = TodoCategory(1, "c1", 0, null, "")
        val todoCategory2 = TodoCategory(2, "c2", 0, todoCategory1.categoryId, "")
        val time = LocalTime.now()
        val todoBlock1 = TodoBlock(1, "n", null, time, time.plusHours(2), null)
        val todoBlock2 = TodoBlock(2, "n", null, time, time.plusHours(2), null)

        todoCategoryDao.insertTodoCategory(todoCategory1)
        todoCategoryDao.insertTodoCategory(todoCategory2)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)
        val todoCategories = listOf(todoCategory1, todoCategory2)
        todoCategories.forEach {
            todoBlockTodoCategoryRelationshipDao.connectTodoBlockAndTodoCategory(
                todoBlock1.todoBlockId, it.categoryId
            )
        }
        todoBlockTodoCategoryRelationshipDao.connectTodoBlockAndTodoCategory(
            todoBlock2.todoBlockId, todoCategory1.categoryId
        )

        todoBlockTodoCategoryRelationshipDao.disconnectAllTodoCategoriesFromTodoBlock(todoBlock1.todoBlockId)

        val relationships = todoBlockTodoCategoryRelationshipDao
            .getAllTodoBlockCategoryRelationships().first()
        assertThat(relationships).containsExactly(
            TodoBlockCategoryRelationship(todoBlock2.todoBlockId, todoCategory1.categoryId)
        )
    }

    @Test
    fun test_connecting_todo_block_and_todo_categories() = runTest {
        val todoCategory1 = TodoCategory(1, "c1", 0, null, "")
        val todoCategory2 = TodoCategory(2, "c2", 0, todoCategory1.categoryId, "")
        val time = LocalTime.now()
        val todoBlock = TodoBlock(1, "n", null, time, time.plusHours(2), null)

        todoCategoryDao.insertTodoCategory(todoCategory1)
        todoCategoryDao.insertTodoCategory(todoCategory2)
        todoBlockDao.insertTodoBlock(todoBlock)

        val todoCategories = listOf(todoCategory1, todoCategory2)
        todoCategories.forEach {
            todoBlockTodoCategoryRelationshipDao.connectTodoBlockAndTodoCategory(
                todoBlock.todoBlockId, it.categoryId
            )
        }

        val relationships = todoBlockTodoCategoryRelationshipDao
            .getAllTodoBlockCategoryRelationships().first()
        assertThat(relationships).containsExactly(
            TodoBlockCategoryRelationship(todoBlock.todoBlockId, todoCategory1.categoryId),
            TodoBlockCategoryRelationship(todoBlock.todoBlockId, todoCategory2.categoryId)
        )
    }
}