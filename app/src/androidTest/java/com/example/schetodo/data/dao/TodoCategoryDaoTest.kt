package com.example.schetodo.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.schetodo.data.SchetodoDatabase
import com.example.schetodo.data.entity.TodoCategory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class TodoCategoryDaoTest {
    private lateinit var todoCategoryDao: TodoCategoryDao
    private lateinit var db: SchetodoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SchetodoDatabase::class.java).build()
        todoCategoryDao = db.todoCategoryDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun test_getTopLevelTodoCategories() = runTest {
        val topLevelCategory1 = TodoCategory(1, "c1", 0, null)
        val topLevelCategory2 = TodoCategory(2, "c2", 0, null)
        val childCategory1 = TodoCategory(3, "c3", 0, 1)
        val childCategory2 = TodoCategory(4, "c4", 0, 3)
        val childCategory3 = TodoCategory(5, "c5", 0, 2)

        todoCategoryDao.insertTodoCategory(topLevelCategory1)
        todoCategoryDao.insertTodoCategory(topLevelCategory2)
        todoCategoryDao.insertTodoCategory(childCategory1)
        todoCategoryDao.insertTodoCategory(childCategory2)
        todoCategoryDao.insertTodoCategory(childCategory3)

        todoCategoryDao.getTopLevelTodoCategories().test {
            val topLevelCategories = awaitItem()
            assertThat(topLevelCategories.size).isEqualTo(2)
            assertThat(topLevelCategories).contains(topLevelCategory1)
            assertThat(topLevelCategories).contains(topLevelCategory2)
        }
    }

    @Test
    fun test_getChildTodoCategoriesOfParentCategory() = runTest {
        val topLevelCategory1 = TodoCategory(1, "c1", 0, null)
        val topLevelCategory2 = TodoCategory(2, "c2", 0, null)
        val childCategory1 = TodoCategory(3, "c3", 0, 1)
        val childCategory2 = TodoCategory(4, "c4", 0, 3)
        val childCategory3 = TodoCategory(5, "c5", 0, 2)

        todoCategoryDao.insertTodoCategory(topLevelCategory1)
        todoCategoryDao.insertTodoCategory(topLevelCategory2)
        todoCategoryDao.insertTodoCategory(childCategory1)
        todoCategoryDao.insertTodoCategory(childCategory2)
        todoCategoryDao.insertTodoCategory(childCategory3)

        todoCategoryDao.getDirectChildTodoCategoriesOf(1).test {
            val childCategories = awaitItem()
            assertThat(childCategories.size).isEqualTo(1)
            assertThat(childCategories).contains(childCategory1)
        }
    }
}