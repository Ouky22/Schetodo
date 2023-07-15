package com.example.schetodo.data.todo_category

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
    fun test_getting_child_todo_categories_with_categories_marked_for_deletion() = runTest {
        val category1 = TodoCategory(1, "c1", 0, null, "", true)
        val category2 = TodoCategory(2, "c2", 0, category1.categoryId, "", true)
        val category3 = TodoCategory(3, "c3", 0, category1.categoryId, "")
        todoCategoryDao.insertTodoCategory(category1)
        todoCategoryDao.insertTodoCategory(category2)
        todoCategoryDao.insertTodoCategory(category3)

        assertThat(
            todoCategoryDao.getDirectChildTodoCategoriesOf(
                category1.categoryId, withMarkedForDeletion = true
            ).first()
        ).containsExactly(category2, category3)
    }

    @Test
    fun test_delete_all_todo_categories_marked_for_deletion() = runTest {
        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, null, "")
        todoCategoryDao.insertTodoCategory(category1)
        todoCategoryDao.insertTodoCategory(category2)
        todoCategoryDao.markTodoCategoryForDeletion(category1.categoryId)

        todoCategoryDao.deleteAllTodoCategoriesMarkedForDeletion()

        assertThat(todoCategoryDao.getTopLevelTodoCategories().first()).containsExactly(category2)
    }

    @Test
    fun test_mark_todo_category_for_deletion() = runTest {
        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, null, "")
        todoCategoryDao.insertTodoCategory(category1)
        todoCategoryDao.insertTodoCategory(category2)
        todoCategoryDao.markTodoCategoryForDeletion(category1.categoryId)

        assertThat(
            todoCategoryDao.getTodoCategoryById(category1.categoryId).first()?.markedForDeletion
        ).isTrue()
        assertThat(
            todoCategoryDao.getTodoCategoryById(category2.categoryId).first()?.markedForDeletion
        ).isFalse()
    }

    @Test
    fun test_unmark_todo_category_for_deletion() = runTest {
        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, null, "", markedForDeletion = true)
        todoCategoryDao.insertTodoCategory(category1)
        todoCategoryDao.insertTodoCategory(category2)
        todoCategoryDao.markTodoCategoryForDeletion(category1.categoryId)
        todoCategoryDao.unmarkTodoCategoryForDeletion(category1.categoryId)

        assertThat(
            todoCategoryDao.getTodoCategoryById(category1.categoryId).first()?.markedForDeletion
        ).isFalse()
        assertThat(
            todoCategoryDao.getTodoCategoryById(category2.categoryId).first()?.markedForDeletion
        ).isTrue()
    }

    @Test
    fun when_deleting_category_then_also_delete_child_categories() = runTest {
        val parentCategory = TodoCategory(1, "c1", 0, null, "")
        val childCategory = TodoCategory(2, "c2", 0, parentCategory.categoryId, "")
        todoCategoryDao.insertTodoCategory(parentCategory)
        todoCategoryDao.insertTodoCategory(childCategory)
        todoCategoryDao.deleteTodoCategoryById(parentCategory.categoryId)

        assertThat(todoCategoryDao.getTodoCategoryById(parentCategory.categoryId).first()).isNull()
        assertThat(todoCategoryDao.getTodoCategoryById(childCategory.categoryId).first()).isNull()
    }

    @Test
    fun when_deleting_non_existing_category_by_id_then_nothing_happens() = runTest {
        todoCategoryDao.deleteTodoCategoryById(1)
    }

    @Test
    fun when_deleting_existing_category_by_id_then_it_is_deleted() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "")
        todoCategoryDao.insertTodoCategory(category)
        todoCategoryDao.deleteTodoCategoryById(category.categoryId)

        assertThat(todoCategoryDao.getTodoCategoryById(category.categoryId).first()).isNull()
    }

    @Test
    fun test_get_top_level_todo_categories() = runTest {
        val topLevelCategory1 = TodoCategory(1, "c1", 0, null, "")
        val topLevelCategory2 = TodoCategory(2, "c2", 0, null, "")
        val topLevelCategory3 = TodoCategory(3, "c3", 0, null, "", markedForDeletion = true)
        val childCategory1 = TodoCategory(4, "c4", 0, 1, "")
        val childCategory2 = TodoCategory(5, "c5", 0, 3, "")
        val childCategory3 = TodoCategory(6, "c6", 0, 2, "")

        todoCategoryDao.insertTodoCategory(topLevelCategory1)
        todoCategoryDao.insertTodoCategory(topLevelCategory2)
        todoCategoryDao.insertTodoCategory(topLevelCategory3)
        todoCategoryDao.insertTodoCategory(childCategory1)
        todoCategoryDao.insertTodoCategory(childCategory2)
        todoCategoryDao.insertTodoCategory(childCategory3)

        assertThat(todoCategoryDao.getTopLevelTodoCategories().first()).containsExactly(
            topLevelCategory1,
            topLevelCategory2
        )
    }

    @Test
    fun test_get_child_todo_categories_of_parent_category() = runTest {
        val topLevelCategory1 = TodoCategory(1, "c1", 0, null, "")
        val topLevelCategory2 = TodoCategory(2, "c2", 0, null, "")
        val childCategory1 = TodoCategory(3, "c3", 0, 1, "")
        val childCategory2 = TodoCategory(4, "c4", 0, 1, "", markedForDeletion = true)
        val childCategory3 = TodoCategory(5, "c5", 0, 3, "")
        val childCategory4 = TodoCategory(6, "c6", 0, 2, "")

        todoCategoryDao.insertTodoCategory(topLevelCategory1)
        todoCategoryDao.insertTodoCategory(topLevelCategory2)
        todoCategoryDao.insertTodoCategory(childCategory1)
        todoCategoryDao.insertTodoCategory(childCategory2)
        todoCategoryDao.insertTodoCategory(childCategory3)
        todoCategoryDao.insertTodoCategory(childCategory4)

        assertThat(todoCategoryDao.getDirectChildTodoCategoriesOf(1).first()).containsExactly(
            childCategory1
        )
    }
}