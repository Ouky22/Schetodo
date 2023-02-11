package com.example.schetodo.ui.feature.todos.add_edit_todo

import androidx.lifecycle.SavedStateHandle
import com.example.schetodo.data.entity.Todo
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.data.repository.FakeTodoCategoryRepository
import com.example.schetodo.data.repository.FakeTodoRepository
import com.example.schetodo.ui.navigation.AddTodo
import com.example.schetodo.ui.navigation.EditTodo
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class AddEditTodoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeTodoRepository = FakeTodoRepository()
    private val fakeTodoCategoryRepository = FakeTodoCategoryRepository()

    @Test
    fun when_opening_view_model_for_adding_todo_then_load_data_of_parent_category() = runTest {
        val category = TodoCategory(1, "test category", 0, null, "icon")
        val todo = Todo(1, "test", TodoPriority.HIGH, TodoFlag.UNDONE, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo)
        val savedStateHandle =
            SavedStateHandle(mapOf(AddTodo.parentTodoCategoryIdArg to category.categoryId))
        val viewModel =
            AddEditTodoViewModel(fakeTodoRepository, fakeTodoCategoryRepository, savedStateHandle)

        advanceUntilIdle()

        val state = viewModel.addEditTodoState
        assertThat(state.value.parentTodoCategoryName).isEqualTo(category.name)
        assertThat(state.value.parentTodoCategoryColor).isEqualTo(category.color)
        assertThat(state.value.parentTodoCategoryIconName).isEqualTo(category.iconName)
    }

    @Test
    fun when_opening_view_model_for_editing_todo_then_load_data_of_todo() = runTest {
        val category = TodoCategory(1, "test category", 0, null, "icon")
        val todo = Todo(1, "test", TodoPriority.HIGH, TodoFlag.UNDONE, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo)
        val savedStateHandle = SavedStateHandle(mapOf(EditTodo.todoId to todo.todoId))
        val viewModel =
            AddEditTodoViewModel(fakeTodoRepository, fakeTodoCategoryRepository, savedStateHandle)

        advanceUntilIdle()

        val state = viewModel.addEditTodoState
        assertThat(state.value.todoDescription).isEqualTo(todo.description)
        assertThat(state.value.todoPriority).isEqualTo(todo.priority)
        assertThat(state.value.todoIsRecurring).isFalse()
        assertThat(state.value.inEditingMode).isTrue()
    }

    @Test
    fun when_opening_view_model_for_editing_todo_then_load_data_of_parent_category() = runTest {
        val category = TodoCategory(1, "test category", 0, null, "icon")
        val todo = Todo(1, "test", TodoPriority.HIGH, TodoFlag.UNDONE, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo)
        val savedStateHandle = SavedStateHandle(mapOf(EditTodo.todoId to todo.todoId))
        val viewModel =
            AddEditTodoViewModel(fakeTodoRepository, fakeTodoCategoryRepository, savedStateHandle)

        advanceUntilIdle()

        val state = viewModel.addEditTodoState
        assertThat(state.value.parentTodoCategoryName).isEqualTo(category.name)
        assertThat(state.value.parentTodoCategoryColor).isEqualTo(category.color)
        assertThat(state.value.parentTodoCategoryIconName).isEqualTo(category.iconName)
        assertThat(state.value.inEditingMode).isTrue()
    }
}