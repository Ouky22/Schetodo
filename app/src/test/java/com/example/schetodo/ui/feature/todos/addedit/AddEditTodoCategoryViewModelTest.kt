package com.example.schetodo.ui.feature.todos.addedit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import com.google.common.truth.Truth.assertThat
import org.junit.Test


internal class AddEditTodoCategoryViewModelTest {

    @Test
    fun when_todo_category_name_changed_then_it_is_updated() {
        val viewModel = AddEditTodoCategoryViewModel()
        val newName = "New Category Name"
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryName(newName))
        assertThat(viewModel.todoCategoryName).isEqualTo(newName)
    }

    @Test
    fun when_todo_category_color_changed_then_it_is_updated() {
        val viewModel = AddEditTodoCategoryViewModel()
        val newColor = 0xffaaddff
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryColor(newColor))
        assertThat(viewModel.todoCategoryColor).isEqualTo(newColor)
    }

    @Test
    fun when_todo_category_icon_changed_then_it_is_updated() {
        val viewModel = AddEditTodoCategoryViewModel()
        val newIconName = Icons.Filled.House.name
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryIcon(newIconName))
        assertThat(viewModel.todoCategoryIconName).isEqualTo(newIconName)
    }
}