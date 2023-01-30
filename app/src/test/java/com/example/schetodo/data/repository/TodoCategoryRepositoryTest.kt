package com.example.schetodo.data.repository

import app.cash.turbine.test
import com.example.schetodo.data.dao.FakeTodoCategoryDao
import com.example.schetodo.data.dao.TodoCategoryDao
import com.example.schetodo.data.entity.TodoCategory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
internal class TodoCategoryRepositoryTest {

    private lateinit var fakeTodoCategoryDao: TodoCategoryDao
    private lateinit var todoCategoryRepository: TodoCategoryRepository

    @Before
    fun init() {
        fakeTodoCategoryDao = FakeTodoCategoryDao()
        todoCategoryRepository = TodoCategoryRepository(fakeTodoCategoryDao)
    }

    @Test
    fun test_getting_category_by_id_when_existing_id_provided() = runTest {
        val category1 = TodoCategory(1, "Test", 0L, null)
        val category2 = TodoCategory(2, "Test", 1L, 1)
        fakeTodoCategoryDao.insertTodoCategory(category1)
        fakeTodoCategoryDao.insertTodoCategory(category2)

        todoCategoryRepository.getTodoCategory(category1.categoryId).test {
            val category = awaitItem()
            assertThat(category).isEqualTo(category1)
            awaitComplete()
        }
    }

    @Test
    fun test_getting_category_by_id_when_not_existing_id_provided() = runTest {
        val category1 = TodoCategory(1, "Test", 0L, null)
        val category2 = TodoCategory(2, "Test", 1L, 1)
        fakeTodoCategoryDao.insertTodoCategory(category1)
        fakeTodoCategoryDao.insertTodoCategory(category2)

        todoCategoryRepository.getTodoCategory(3).test {
            val category = awaitItem()
            assertThat(category).isEqualTo(null)
            awaitComplete()
        }
    }

    @Test
    fun test_getting_category_by_id_when_null_as_id_provided() = runTest {
        todoCategoryRepository.getTodoCategory(null).test {
            val value = awaitItem()
            assertThat(value).isNull()
            awaitComplete()
        }
    }

    @Test
    fun when_getting_child_categories_of_null_then_return_top_level_categories() = runTest {
        val topLevelCategory1 = TodoCategory(1, "", 0L, null)
        val topLevelCategory2 = TodoCategory(2, "", 0L, null)
        val childCategory1 = TodoCategory(2, "", 0L, topLevelCategory2.categoryId)
        fakeTodoCategoryDao.insertTodoCategory(topLevelCategory1)
        fakeTodoCategoryDao.insertTodoCategory(topLevelCategory2)
        fakeTodoCategoryDao.insertTodoCategory(childCategory1)

        todoCategoryRepository.getChildTodoCategoriesOf(null).test {
            val categories = awaitItem()
            assertThat(categories.size).isEqualTo(2)
            assertThat(categories).contains(topLevelCategory1)
            assertThat(categories).contains(topLevelCategory2)
            awaitComplete()
        }
    }

    @Test
    fun when_no_categories_exist_then_getting_child_categories_returns_flow_of_empty_list() = runTest {
        todoCategoryRepository.getChildTodoCategoriesOf(1).test {
            val categories = awaitItem()
            assertThat(categories).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun when_category_has_no_child_categories_then_return_flow_of_empty_list() = runTest {
        val category1 = TodoCategory(1, "", 0L, null)
        val category2 = TodoCategory(2, "", 0L, category1.categoryId)
        fakeTodoCategoryDao.insertTodoCategory(category1)
        fakeTodoCategoryDao.insertTodoCategory(category2)

        todoCategoryRepository.getChildTodoCategoriesOf(category2.categoryId).test {
            val categories = awaitItem()
            assertThat(categories).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun test_getting_the_direct_child_categories_of_a_category() = runTest {
        val parentCategory = TodoCategory(1, "", 0L, null)
        val childCategory1 = TodoCategory(2, "", 0L, parentCategory.categoryId)
        val childCategory2 = TodoCategory(3, "", 0L, parentCategory.categoryId)
        val category = TodoCategory(4, "", 0L, childCategory1.categoryId)
        fakeTodoCategoryDao.insertTodoCategory(parentCategory)
        fakeTodoCategoryDao.insertTodoCategory(childCategory1)
        fakeTodoCategoryDao.insertTodoCategory(childCategory2)
        fakeTodoCategoryDao.insertTodoCategory(category)

        todoCategoryRepository.getChildTodoCategoriesOf(parentCategory.categoryId).test {
            val categories = awaitItem()
            assertThat(categories.size).isEqualTo(2)
            assertThat(categories).contains(childCategory1)
            assertThat(categories).contains(childCategory2)
            awaitComplete()
        }
    }
}