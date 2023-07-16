package com.example.schetodo.data.todo_category

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
internal class TodoCategoryRepositoryImplTest {

    private lateinit var fakeTodoCategoryDao: TodoCategoryDao
    private lateinit var todoCategoryRepositoryImpl: TodoCategoryRepositoryImpl

    @Before
    fun init() {
        fakeTodoCategoryDao = FakeTodoCategoryDao()
        todoCategoryRepositoryImpl = TodoCategoryRepositoryImpl(
            fakeTodoCategoryDao, CoroutineScope(SupervisorJob())
        )
    }

    @Test
    fun test_mark_todo_category_for_deletion() = runTest {
        val category1 = TodoCategory(1, "Test", 0L, null, "")
        val category2 = TodoCategory(2, "Test", 1L, 1, "")
        fakeTodoCategoryDao.insertTodoCategory(category1)
        fakeTodoCategoryDao.insertTodoCategory(category2)

        fakeTodoCategoryDao.markTodoCategoryForDeletion(category1.categoryId)

        assertThat(
            fakeTodoCategoryDao.getTodoCategoryById(category1.categoryId).first()?.markedForDeletion
        ).isTrue()
        assertThat(
            fakeTodoCategoryDao.getTodoCategoryById(category2.categoryId).first()?.markedForDeletion
        ).isFalse()
    }

    @Test
    fun test_unmark_todo_category_for_deletion() = runTest {
        val category1 = TodoCategory(1, "Test", 0L, null, "")
        val category2 = TodoCategory(2, "Test", 1L, 1, "", markedForDeletion = true)
        fakeTodoCategoryDao.insertTodoCategory(category1)
        fakeTodoCategoryDao.insertTodoCategory(category2)

        fakeTodoCategoryDao.markTodoCategoryForDeletion(category1.categoryId)
        fakeTodoCategoryDao.unmarkTodoCategoryForDeletion(category1.categoryId)

        assertThat(
            fakeTodoCategoryDao.getTodoCategoryById(category1.categoryId).first()?.markedForDeletion
        ).isFalse()
        assertThat(
            fakeTodoCategoryDao.getTodoCategoryById(category2.categoryId).first()?.markedForDeletion
        ).isTrue()
    }

    @Test
    fun test_getting_category_by_id_when_existing_id_provided() = runTest {
        val category1 = TodoCategory(1, "Test", 0L, null, "")
        val category2 = TodoCategory(2, "Test", 1L, 1, "")
        fakeTodoCategoryDao.insertTodoCategory(category1)
        fakeTodoCategoryDao.insertTodoCategory(category2)

        assertThat(
            todoCategoryRepositoryImpl.getTodoCategory(category1.categoryId).first()
        ).isEqualTo(category1)
    }

    @Test
    fun test_getting_category_by_id_when_not_existing_id_provided() = runTest {
        val category1 = TodoCategory(1, "Test", 0L, null, "")
        val category2 = TodoCategory(2, "Test", 1L, 1, "")
        fakeTodoCategoryDao.insertTodoCategory(category1)
        fakeTodoCategoryDao.insertTodoCategory(category2)

        assertThat(todoCategoryRepositoryImpl.getTodoCategory(3).first()).isNull()
    }

    @Test
    fun test_getting_category_by_id_when_null_as_id_provided() = runTest {
        assertThat(todoCategoryRepositoryImpl.getTodoCategory(null).first()).isNull()
    }

    @Test
    fun when_getting_child_categories_of_null_then_return_top_level_categories() = runTest {
        val topLevelCategory1 = TodoCategory(1, "", 0L, null, "")
        val topLevelCategory2 = TodoCategory(2, "", 0L, null, "")
        val childCategory1 = TodoCategory(2, "", 0L, topLevelCategory2.categoryId, "")
        fakeTodoCategoryDao.insertTodoCategory(topLevelCategory1)
        fakeTodoCategoryDao.insertTodoCategory(topLevelCategory2)
        fakeTodoCategoryDao.insertTodoCategory(childCategory1)

        assertThat(
            todoCategoryRepositoryImpl.getChildTodoCategoriesOf(null).first()
        ).containsExactly(
            topLevelCategory1, topLevelCategory2
        )
    }

    @Test
    fun when_no_categories_exist_then_getting_child_categories_returns_flow_of_empty_list() =
        runTest {
            assertThat(todoCategoryRepositoryImpl.getChildTodoCategoriesOf(1).first()).isEmpty()
        }

    @Test
    fun when_category_has_no_child_categories_then_return_flow_of_empty_list() = runTest {
        val category1 = TodoCategory(1, "", 0L, null, "")
        val category2 = TodoCategory(2, "", 0L, category1.categoryId, "")
        fakeTodoCategoryDao.insertTodoCategory(category1)
        fakeTodoCategoryDao.insertTodoCategory(category2)

        assertThat(
            todoCategoryRepositoryImpl.getChildTodoCategoriesOf(category2.categoryId).first()
        ).isEmpty()
    }

    @Test
    fun test_getting_the_direct_child_categories_of_a_category() = runTest {
        val parentCategory = TodoCategory(1, "", 0L, null, "")
        val childCategory1 = TodoCategory(2, "", 0L, parentCategory.categoryId, "")
        val childCategory2 = TodoCategory(3, "", 0L, parentCategory.categoryId, "")
        val category = TodoCategory(4, "", 0L, childCategory1.categoryId, "")
        fakeTodoCategoryDao.insertTodoCategory(parentCategory)
        fakeTodoCategoryDao.insertTodoCategory(childCategory1)
        fakeTodoCategoryDao.insertTodoCategory(childCategory2)
        fakeTodoCategoryDao.insertTodoCategory(category)

        assertThat(
            todoCategoryRepositoryImpl.getChildTodoCategoriesOf(parentCategory.categoryId).first()
        ).containsExactly(
            childCategory1, childCategory2
        )
    }
}