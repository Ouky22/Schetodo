package com.example.schetodo.ui.feature.todos.addedit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.House
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.repository.FakeTodoCategoryRepository
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
internal class AddEditTodoCategoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val fakeTodoCategoryRepository = FakeTodoCategoryRepository()

    @Test
    fun when_saving_and_invalid_category_name_do_not_save_and_show_error() = runTest {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        val todoCategory = TodoCategory(1, "Test", 0xffeeddaa, null, "Icon")
        viewModel.setTodoCategoryForEditing(todoCategory.categoryId)

        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryName("  "))
        viewModel.onEvent(AddEditTodoCategoryEvent.SaveTodoCategory)

        assertThat(viewModel.showInvalidTodoCategoryNameError).isTrue()
        assertThat(
            fakeTodoCategoryRepository.getTodoCategory(todoCategory.categoryId).first()
        ).isNull()
    }

    @Test
    fun when_add_and_save_category_it_is_added_accordingly() = runTest {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        val parentCategory = TodoCategory(1, "Test", 0xffeeddaa, null, "Icon")
        fakeTodoCategoryRepository.insertTodoCategory(parentCategory)
        viewModel.setParentTodoCategoryForAdding(parentCategory.categoryId)

        val categoryName = "Category Name"
        val categoryIcon = Icons.Filled.Architecture.name
        val categoryColor = 0xeeeeeeee
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryName(categoryName))
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryIcon(categoryIcon))
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryColor(categoryColor))
        viewModel.onEvent(AddEditTodoCategoryEvent.SaveTodoCategory)

        val childCategories =
            fakeTodoCategoryRepository.getChildTodoCategoriesOf(parentCategory.categoryId).first()

        assertThat(childCategories.size).isEqualTo(1)
        val newCategory = childCategories[0]
        assertThat(newCategory.name).isEqualTo(categoryName)
        assertThat(newCategory.iconName).isEqualTo(categoryIcon)
        assertThat(newCategory.color).isEqualTo(categoryColor)
        assertThat(newCategory.parentTodoCategoryId).isEqualTo(parentCategory.categoryId)
    }

    @Test
    fun when_edit_and_save_category_it_is_edited_accordingly() = runTest {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        val category = TodoCategory(1, "Test", 0xffeeddaa, 10, "Icon")
        fakeTodoCategoryRepository.insertTodoCategory(category)
        viewModel.setTodoCategoryForEditing(category.categoryId)

        val newCategoryName = "New Category Name"
        val newCategoryIcon = Icons.Filled.House.name
        val newCategoryColor = 0xffaaeeff
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryName(newCategoryName))
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryIcon(newCategoryIcon))
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryColor(newCategoryColor))
        viewModel.onEvent(AddEditTodoCategoryEvent.SaveTodoCategory)

        val updatedCategory =
            fakeTodoCategoryRepository.getTodoCategory(category.categoryId).first()

        assertThat(updatedCategory?.name).isEqualTo(newCategoryName)
        assertThat(updatedCategory?.iconName).isEqualTo(newCategoryIcon)
        assertThat(updatedCategory?.color).isEqualTo(newCategoryColor)
        assertThat(updatedCategory?.parentTodoCategoryId).isEqualTo(category.parentTodoCategoryId)
    }

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

    @Test
    fun when_todo_category_icon_selected_do_not_show_icon_picker_anymore() {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        viewModel.onEvent(AddEditTodoCategoryEvent.ShowColorPicker)
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryIcon(Icons.Filled.Category.name))
        assertThat(viewModel.showIconPicker).isFalse()
    }

    @Test
    fun when_todo_category_color_selected_do_not_show_color_picker_anymore() {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        viewModel.onEvent(AddEditTodoCategoryEvent.ShowColorPicker)
        viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryColor(0xaaffeedd))
        assertThat(viewModel.showColorPicker).isFalse()
    }

    @Test
    fun on_show_todo_category_icon_selection_only_show_icon_picker() {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        viewModel.onEvent(AddEditTodoCategoryEvent.ShowColorPicker)
        viewModel.onEvent(AddEditTodoCategoryEvent.ShowIconPicker)
        assertThat(viewModel.showIconPicker).isTrue()
        assertThat(viewModel.showColorPicker).isFalse()
    }

    @Test
    fun on_open_todo_category_color_selection_only_show_color_picker() {
        val viewModel = AddEditTodoCategoryViewModel(fakeTodoCategoryRepository)
        viewModel.onEvent(AddEditTodoCategoryEvent.ShowIconPicker)
        viewModel.onEvent(AddEditTodoCategoryEvent.ShowColorPicker)
        assertThat(viewModel.showColorPicker).isTrue()
        assertThat(viewModel.showIconPicker).isFalse()
    }
}