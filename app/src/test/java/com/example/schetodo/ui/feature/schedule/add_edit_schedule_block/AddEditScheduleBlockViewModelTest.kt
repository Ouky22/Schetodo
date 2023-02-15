package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import androidx.lifecycle.SavedStateHandle
import com.example.schetodo.ui.navigation.schedule.AddScheduleBlock
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class AddEditScheduleBlockViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    @Before
    fun init() {
        Locale.setDefault(Locale.US)
    }

    @Test
    fun when_creating_view_model_for_adding_and_start_and_time_passed_then_load_times() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val startTime = LocalTime.of(13, 45)
        val endTime = LocalTime.of(15, 0)
        val savedStateHandle = SavedStateHandle(
            mapOf(
                AddScheduleBlock.dateStampArg to date.toEpochDay(),
                AddScheduleBlock.startTimeStampArg to startTime.toSecondOfDay(),
                AddScheduleBlock.endTimeStampArg to endTime.toSecondOfDay()
            )
        )
        val viewModel = AddEditScheduleBlockViewModel(savedStateHandle)

        assertThat(viewModel.state.startTime).isEqualTo("1:45 PM")
        assertThat(viewModel.state.endTime).isEqualTo("3:00 PM")
    }

    @Test
    fun when_creating_view_model_for_adding_and_end_time_passed_then_load_end_time() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val endTime = LocalTime.of(13, 45)
        val savedStateHandle = SavedStateHandle(
            mapOf(
                AddScheduleBlock.dateStampArg to date.toEpochDay(),
                AddScheduleBlock.endTimeStampArg to endTime.toSecondOfDay()
            )
        )
        val viewModel = AddEditScheduleBlockViewModel(savedStateHandle)

        assertThat(viewModel.state.endTime).isEqualTo("1:45 PM")
    }

    @Test
    fun when_creating_view_model_for_adding_and_start_time_passed_then_load_start_time() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val startTime = LocalTime.of(13, 45)
        val savedStateHandle = SavedStateHandle(
            mapOf(
                AddScheduleBlock.dateStampArg to date.toEpochDay(),
                AddScheduleBlock.startTimeStampArg to startTime.toSecondOfDay()
            )
        )
        val viewModel = AddEditScheduleBlockViewModel(savedStateHandle)

        assertThat(viewModel.state.startTime).isEqualTo("1:45 PM")
    }

    @Test
    fun when_creating_view_model_then_load_the_passed_date() = runTest {
        val date = LocalDate.of(2023, 2, 15)
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to date.toEpochDay())
        )
        val viewModel = AddEditScheduleBlockViewModel(savedStateHandle)
        assertThat(viewModel.state.date).isEqualTo("Wed 15 Feb, 2023")
    }

    @Test
    fun when_creating_view_model_and_no_valid_date_is_passed_then_throw_exception() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(AddScheduleBlock.dateStampArg to -1)
        )
        assertThrows(Exception::class.java) {
            AddEditScheduleBlockViewModel(savedStateHandle)
        }
    }

    @Test
    fun when_creating_view_model_and_no_date_is_passed_then_throw_exception() = runTest {
        assertThrows(Exception::class.java) {
            AddEditScheduleBlockViewModel(SavedStateHandle())
        }
    }
}