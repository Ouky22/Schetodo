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
    fun check_if_template_todo_blocks_overlap_with_todo_block_from_same_template() = runTest {
        val todoBlock1 = TodoBlock(1, "", null, LocalTime.of(10, 0), LocalTime.of(11, 0), 1)
        val todoBlock2 = TodoBlock(2, "", null, LocalTime.of(12, 0), LocalTime.of(13, 0), 1)
        fakeTodoBlockDao.insertTodoBlock(todoBlock1)
        fakeTodoBlockDao.insertTodoBlock(todoBlock2)

        val todoBlock3 = TodoBlock(3, "", null, LocalTime.of(12, 0), LocalTime.of(13, 0), 1)
        val todoBlock4 = TodoBlock(4, "", null, LocalTime.of(12, 0), LocalTime.of(13, 0), 2)
        val todoBlock5 = TodoBlock(5, "", null, LocalTime.of(11, 0), LocalTime.of(12, 0), 1)
        assertThat(
            todoBlockRepository.templateTodoBlockOverlapsWithTodoBlockFromSameTemplate(todoBlock3)
        ).isTrue()
        assertThat(
            todoBlockRepository.templateTodoBlockOverlapsWithTodoBlockFromSameTemplate(todoBlock4)
        ).isFalse()
        assertThat(
            todoBlockRepository.templateTodoBlockOverlapsWithTodoBlockFromSameTemplate(todoBlock5)
        ).isFalse()
    }

    @Test
    fun test_getting_todo_blocks_that_overlap_with_todo_block_on_certain_date() = runTest {
        val date = LocalDate.of(2023, 2, 20)

        val todoBlock1 = TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
        val todoBlock2 = TodoBlock(2, "", date, LocalTime.of(12, 0), LocalTime.of(13, 0), null)
        val todoBlock3 =
            TodoBlock(3, "", date.plusDays(1), LocalTime.of(12, 0), LocalTime.of(13, 0), null)
        fakeTodoBlockDao.insertTodoBlock(todoBlock1)
        fakeTodoBlockDao.insertTodoBlock(todoBlock2)
        fakeTodoBlockDao.insertTodoBlock(todoBlock3)


        val newTodoBlock =
            TodoBlock(2, "", null, LocalTime.of(10, 0), LocalTime.of(12, 0), 1)

        assertThat(
            todoBlockRepository.getTodoBlocksThatOverlapWith(newTodoBlock, date)
        ).containsExactly(
            todoBlock1
        )
    }

    @Test
    fun when_todo_block_is_after_other_todo_block_they_do_not_overlap() = runTest {
        val date = LocalDate.of(2023, 2, 20)

        val alreadyExistingTodoBlock =
            TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
        fakeTodoBlockDao.insertTodoBlock(alreadyExistingTodoBlock)

        val newTodoBlock =
            TodoBlock(2, "", date, LocalTime.of(11, 0), LocalTime.of(12, 0), null)

        assertThat(todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(newTodoBlock)).isFalse()
    }

    @Test
    fun todo_block_can_not_overlap_with_itself() = runTest {
        val date = LocalDate.of(2023, 2, 20)

        val alreadyExistingTodoBlock =
            TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
        fakeTodoBlockDao.insertTodoBlock(alreadyExistingTodoBlock)

        assertThat(todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(alreadyExistingTodoBlock)).isFalse()
    }

    @Test
    fun when_todo_block_is_before_other_todo_blocks_then_they_do_not_overlap() = runTest {
        val date = LocalDate.of(2023, 2, 20)

        val alreadyExistingTodoBlock =
            TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
        fakeTodoBlockDao.insertTodoBlock(alreadyExistingTodoBlock)

        val newTodoBlock =
            TodoBlock(2, "", date, LocalTime.of(9, 0), LocalTime.of(10, 0), null)

        assertThat(todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(newTodoBlock)).isFalse()
    }

    @Test
    fun when_todo_blocks_end_time_overlaps_with_other_todo_block_then_they_overlap() =
        runTest {
            val date = LocalDate.of(2023, 2, 20)

            val alreadyExistingTodoBlock =
                TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
            fakeTodoBlockDao.insertTodoBlock(alreadyExistingTodoBlock)

            val newTodoBlock =
                TodoBlock(2, "", date, LocalTime.of(9, 0), LocalTime.of(10, 1), null)

            assertThat(todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(newTodoBlock)).isTrue()
        }

    @Test
    fun when_todo_blocks_start_time_overlaps_with_other_todo_block_then_they_overlap() =
        runTest {
            val date = LocalDate.of(2023, 2, 20)

            val alreadyExistingTodoBlock =
                TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
            fakeTodoBlockDao.insertTodoBlock(alreadyExistingTodoBlock)

            val newTodoBlock =
                TodoBlock(2, "", date, LocalTime.of(10, 59), LocalTime.of(12, 0), null)

            assertThat(todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(newTodoBlock)).isTrue()
        }

    @Test
    fun when_todo_blocks_time_equals_other_todo_block_time_then_they_overlap() = runTest {
        val date = LocalDate.of(2023, 2, 20)

        val alreadyExistingTodoBlock =
            TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
        fakeTodoBlockDao.insertTodoBlock(alreadyExistingTodoBlock)

        val newTodoBlock =
            TodoBlock(2, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)

        assertThat(todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(newTodoBlock)).isTrue()
    }

    @Test
    fun when_todo_blocks_time_is_in_between_other_todo_blocks_time_interval_then_they_overlap() =
        runTest {
            val date = LocalDate.of(2023, 2, 20)

            val alreadyExistingTodoBlock =
                TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
            fakeTodoBlockDao.insertTodoBlock(alreadyExistingTodoBlock)

            val newTodoBlock =
                TodoBlock(2, "", date, LocalTime.of(10, 1), LocalTime.of(10, 59), null)

            assertThat(todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(newTodoBlock)).isTrue()
        }

    @Test
    fun when_todo_blocks_time_interval_contains_other_todo_blocks_time_interval_then_they_overlap() =
        runTest {
            val date = LocalDate.of(2023, 2, 20)

            val alreadyExistingTodoBlock =
                TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null)
            fakeTodoBlockDao.insertTodoBlock(alreadyExistingTodoBlock)

            val newTodoBlock =
                TodoBlock(2, "", date, LocalTime.of(9, 59), LocalTime.of(11, 1), null)

            assertThat(todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(newTodoBlock)).isTrue()
        }

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