package com.example.schetodo.ui.feature.todos.addedit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.repository.FakeTodoCategoryRepository
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
internal class AddEditTodoCategoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeTodoCategoryRepository = FakeTodoCategoryRepository()

    @Test
    fun when_valid_id_of_category_for_editing_set_then_load_data_of_category() = runTest {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        val category = TodoCategory(1, "Test", 0xffeeddaa, 10, "Icon")
        fakeTodoCategoryRepository.insertTodoCategory(category)

        viewModel.setTodoCategoryForEditing(category.categoryId)

        advanceUntilIdle()

        assertThat(viewModel.todoCategoryName).isEqualTo(category.name)
        assertThat(viewModel.todoCategoryColor).isEqualTo(category.color)
        assertThat(viewModel.todoCategoryIconName).isEqualTo(category.iconName)
        assertThat(viewModel.inEditingMode).isTrue()
    }

    @Test
    fun when_todo_category_name_changed_then_it_is_updated() {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        val newName = "New Category Name"
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryName(newName))
        assertThat(viewModel.todoCategoryName).isEqualTo(newName)
    }

    @Test
    fun when_todo_category_color_changed_then_it_is_updated() {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        val newColor = 0xffaaddff
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryColor(newColor))
        assertThat(viewModel.todoCategoryColor).isEqualTo(newColor)
    }

    @Test
    fun when_todo_category_icon_changed_then_it_is_updated() {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        val newIconName = Icons.Filled.House.name
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryIcon(newIconName))
        assertThat(viewModel.todoCategoryIconName).isEqualTo(newIconName)
    }
}