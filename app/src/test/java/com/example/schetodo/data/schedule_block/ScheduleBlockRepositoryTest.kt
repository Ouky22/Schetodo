package com.example.schetodo.data.schedule_block

import com.example.schetodo.data.todo_block.TodoBlock
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
internal class ScheduleBlockRepositoryTest {

    private val fakeScheduleBlockDao = FakeScheduleBlockDao()
    private val scheduleBlockRepository = ScheduleBlockRepositoryImpl(fakeScheduleBlockDao)

    @Test
    fun when_schedule_block_is_after_other_schedule_block_then_return_false() = runTest {
        val date = LocalDate.of(2023, 2, 20)

        val alreadyExistingScheduleBlock = ScheduleBlock(
            TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null),
            emptyList(), emptyList()
        )
        fakeScheduleBlockDao.insertScheduleBlock(alreadyExistingScheduleBlock)

        val newScheduleBlock = ScheduleBlock(
            TodoBlock(2, "", date, LocalTime.of(11, 0), LocalTime.of(12, 0), null),
            emptyList(), emptyList()
        )

        assertThat(
            scheduleBlockRepository.scheduleBlockOverlapsWithOtherScheduleBlock(
                newScheduleBlock
            )
        ).isFalse()
    }

    @Test
    fun when_schedule_block_is_before_other_schedule_blocks_then_return_false() = runTest {
        val date = LocalDate.of(2023, 2, 20)

        val alreadyExistingScheduleBlock = ScheduleBlock(
            TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null),
            emptyList(), emptyList()
        )
        fakeScheduleBlockDao.insertScheduleBlock(alreadyExistingScheduleBlock)

        val newScheduleBlock = ScheduleBlock(
            TodoBlock(2, "", date, LocalTime.of(9, 0), LocalTime.of(10, 0), null),
            emptyList(), emptyList()
        )

        assertThat(
            scheduleBlockRepository.scheduleBlockOverlapsWithOtherScheduleBlock(
                newScheduleBlock
            )
        ).isFalse()
    }

    @Test
    fun when_schedule_blocks_end_time_overlaps_with_other_schedule_block_then_return_true() =
        runTest {
            val date = LocalDate.of(2023, 2, 20)

            val alreadyExistingScheduleBlock = ScheduleBlock(
                TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null),
                emptyList(), emptyList()
            )
            fakeScheduleBlockDao.insertScheduleBlock(alreadyExistingScheduleBlock)

            val newScheduleBlock = ScheduleBlock(
                TodoBlock(2, "", date, LocalTime.of(9, 0), LocalTime.of(10, 1), null),
                emptyList(), emptyList()
            )

            assertThat(
                scheduleBlockRepository.scheduleBlockOverlapsWithOtherScheduleBlock(
                    newScheduleBlock
                )
            ).isTrue()
        }

    @Test
    fun when_schedule_blocks_start_time_overlaps_with_other_schedule_block_then_return_true() =
        runTest {
            val date = LocalDate.of(2023, 2, 20)

            val alreadyExistingScheduleBlock = ScheduleBlock(
                TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null),
                emptyList(), emptyList()
            )
            fakeScheduleBlockDao.insertScheduleBlock(alreadyExistingScheduleBlock)

            val newScheduleBlock = ScheduleBlock(
                TodoBlock(2, "", date, LocalTime.of(10, 59), LocalTime.of(12, 0), null),
                emptyList(), emptyList()
            )

            assertThat(
                scheduleBlockRepository.scheduleBlockOverlapsWithOtherScheduleBlock(
                    newScheduleBlock
                )
            ).isTrue()
        }

    @Test
    fun when_schedule_blocks_time_equals_other_schedule_block_time_then_return_true() = runTest {
        val date = LocalDate.of(2023, 2, 20)

        val alreadyExistingScheduleBlock = ScheduleBlock(
            TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null),
            emptyList(), emptyList()
        )
        fakeScheduleBlockDao.insertScheduleBlock(alreadyExistingScheduleBlock)

        val newScheduleBlock = ScheduleBlock(
            TodoBlock(2, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null),
            emptyList(), emptyList()
        )

        assertThat(
            scheduleBlockRepository.scheduleBlockOverlapsWithOtherScheduleBlock(
                newScheduleBlock
            )
        ).isTrue()
    }

    @Test
    fun when_schedule_blocks_time_is_in_between_other_schedule_blocks_time_interval_then_return_true() =
        runTest {
            val date = LocalDate.of(2023, 2, 20)

            val alreadyExistingScheduleBlock = ScheduleBlock(
                TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null),
                emptyList(), emptyList()
            )
            fakeScheduleBlockDao.insertScheduleBlock(alreadyExistingScheduleBlock)

            val newScheduleBlock = ScheduleBlock(
                TodoBlock(2, "", date, LocalTime.of(10, 1), LocalTime.of(10, 59), null),
                emptyList(), emptyList()
            )

            assertThat(
                scheduleBlockRepository.scheduleBlockOverlapsWithOtherScheduleBlock(
                    newScheduleBlock
                )
            ).isTrue()
        }

    @Test
    fun when_schedule_blocks_time_interval_contains_other_schedule_blocks_time_interval_then_return_true() =
        runTest {
            val date = LocalDate.of(2023, 2, 20)

            val alreadyExistingScheduleBlock = ScheduleBlock(
                TodoBlock(1, "", date, LocalTime.of(10, 0), LocalTime.of(11, 0), null),
                emptyList(), emptyList()
            )
            fakeScheduleBlockDao.insertScheduleBlock(alreadyExistingScheduleBlock)

            val newScheduleBlock = ScheduleBlock(
                TodoBlock(2, "", date, LocalTime.of(9, 59), LocalTime.of(11, 1), null),
                emptyList(), emptyList()
            )

            assertThat(
                scheduleBlockRepository.scheduleBlockOverlapsWithOtherScheduleBlock(
                    newScheduleBlock
                )
            ).isTrue()
        }
}