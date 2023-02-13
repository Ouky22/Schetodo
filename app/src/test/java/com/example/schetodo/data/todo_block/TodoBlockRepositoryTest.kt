package com.example.schetodo.data.todo_block

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
internal class TodoBlockRepositoryTest {

    private val fakeTodoBlockDao = FakeTodoBlockDao()
    private val todoBlockRepository = TodoBlockRepositoryImpl(fakeTodoBlockDao)
    private val testTime = LocalTime.now()

    @Test
    fun test_getting_todo_on_date() = runTest {
        val date = LocalDate.of(2023, 2, 1)
        val todoBlock1 = TodoBlock(1, null, null, testTime, testTime, null)
        val todoBlock2 = TodoBlock(2, null, date, testTime, testTime, null)
        val todoBlock3 = TodoBlock(3, null, date.minusDays(1), testTime, testTime, null)

        fakeTodoBlockDao.insertTodoBlock(todoBlock1)
        fakeTodoBlockDao.insertTodoBlock(todoBlock2)
        fakeTodoBlockDao.insertTodoBlock(todoBlock3)

        val allTodosOnDate = todoBlockRepository.getTodoBlocksOnDate(date).first()
        assertThat(allTodosOnDate.size).isEqualTo(1)
        assertThat(allTodosOnDate).contains(todoBlock2)
    }
}