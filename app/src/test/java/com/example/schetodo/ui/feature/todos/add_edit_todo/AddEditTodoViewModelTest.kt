package com.example.schetodo.ui.feature.todos.add_edit_todo

import androidx.lifecycle.SavedStateHandle
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.FakeTodoCategoryRepository
import com.example.schetodo.data.todo.FakeTodoRepository
import com.example.schetodo.ui.navigation.todos.AddTodo
import com.example.schetodo.ui.navigation.todos.EditTodo
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class AddEditTodoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val fakeTodoRepository = FakeTodoRepository()
    private val fakeTodoCategoryRepository = FakeTodoCategoryRepository()

    @Test
    fun when_in_editing_mode_and_delete_todo_event_happens_then_delete_todo() = runTest {
        val category = TodoCategory(1, "test category", 0, null, "icon")
        val todo = Todo(1, "test", TodoPriority.HIGH, TodoFlag.UNDONE, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo)
        val savedStateHandle =
            SavedStateHandle(mapOf(EditTodo.todoId to category.categoryId))
        val viewModel =
            AddEditTodoViewModel(fakeTodoRepository, fakeTodoCategoryRepository, savedStateHandle)

        viewModel.onEvent(AddEditTodoEvent.DeleteTodo)

        assertThat(fakeTodoRepository.getTodoById(todo.todoId).first()).isNull()
        assertThat(viewModel.closeAddEditTodoScreen.value).isTrue()
    }

    @Test
    fun when_in_adding_mode_and_delete_todo_event_happens_throw_exception() = runTest {
        val category = TodoCategory(1, "test category", 0, null, "icon")
        fakeTodoCategoryRepository.insertTodoCategory(category)
        val savedStateHandle =
            SavedStateHandle(mapOf(AddTodo.parentTodoCategoryIdArg to category.categoryId))
        val viewModel =
            AddEditTodoViewModel(fakeTodoRepository, fakeTodoCategoryRepository, savedStateHandle)

        assertThrows(Exception::class.java) { viewModel.onEvent(AddEditTodoEvent.DeleteTodo) }
    }

    @Test
    fun test_editing_and_saving_todo() = runTest {
        val category = TodoCategory(1, "test category", 0, null, "icon")
        val todo = Todo(1, "test", TodoPriority.HIGH, TodoFlag.UNDONE, category.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(category)
        fakeTodoRepository.insertTodo(todo)
        val savedStateHandle =
            SavedStateHandle(mapOf(EditTodo.todoId to category.categoryId))
        val viewModel =
            AddEditTodoViewModel(fakeTodoRepository, fakeTodoCategoryRepository, savedStateHandle)

        val newDescription = "new"
        val newPriority = TodoPriority.LOW
        val newTodoFlag = TodoFlag.RECURRING

        viewModel.onEvent(AddEditTodoEvent.ChangeTodoDescription(newDescription))
        viewModel.onEvent(AddEditTodoEvent.ChangeTodoPriority(newPriority))
        viewModel.onEvent(AddEditTodoEvent.ChangeTodoFlag(newTodoFlag))
        viewModel.onEvent(AddEditTodoEvent.SaveTodo)

        val editedTodo = fakeTodoRepository.getTodoById(todo.todoId).first()
        assertThat(editedTodo?.flag).isEqualTo(newTodoFlag)
        assertThat(editedTodo?.priority).isEqualTo(newPriority)
        assertThat(editedTodo?.description).isEqualTo(newDescription)
        assertThat(editedTodo?.categoryId).isEqualTo(category.categoryId)
        assertThat(viewModel.closeAddEditTodoScreen.value).isTrue()
    }

    @Test
    fun test_adding_and_saving_todo() = runTest {
        val category = TodoCategory(1, "test category", 0, null, "icon")
        fakeTodoCategoryRepository.insertTodoCategory(category)
        val savedStateHandle =
            SavedStateHandle(mapOf(AddTodo.parentTodoCategoryIdArg to category.categoryId))
        val viewModel =
            AddEditTodoViewModel(fakeTodoRepository, fakeTodoCategoryRepository, savedStateHandle)

        val description = "new"
        val priority = TodoPriority.LOW
        val todoFlag = TodoFlag.RECURRING

        viewModel.onEvent(AddEditTodoEvent.ChangeTodoDescription(description))
        viewModel.onEvent(AddEditTodoEvent.ChangeTodoPriority(priority))
        viewModel.onEvent(AddEditTodoEvent.ChangeTodoFlag(todoFlag))
        viewModel.onEvent(AddEditTodoEvent.SaveTodo)

        val addedTodo = fakeTodoRepository.getTodosOfTodoCategory(category.categoryId).first()[0]
        assertThat(addedTodo.flag).isEqualTo(todoFlag)
        assertThat(addedTodo.priority).isEqualTo(priority)
        assertThat(addedTodo.description).isEqualTo(description)
        assertThat(addedTodo.categoryId).isEqualTo(category.categoryId)
        assertThat(viewModel.closeAddEditTodoScreen.value).isTrue()
    }

    @Test
    fun when_saving_todo_without_description_then_show_error_message() = runTest {
        val category = TodoCategory(1, "test category", 0, null, "icon")
        fakeTodoCategoryRepository.insertTodoCategory(category)
        val savedStateHandle =
            SavedStateHandle(mapOf(AddTodo.parentTodoCategoryIdArg to category.categoryId))
        val viewModel =
            AddEditTodoViewModel(fakeTodoRepository, fakeTodoCategoryRepository, savedStateHandle)

        viewModel.onEvent(AddEditTodoEvent.SaveTodo)

        assertThat(viewModel.addEditTodoState.value.showInvalidDescriptionError).isTrue()
        assertThat(fakeTodoRepository.getTodosOfTodoCategory(category.categoryId).first()).isEmpty()
    }

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

        val state = viewModel.addEditTodoState
        assertThat(state.value.todoDescription).isEqualTo(todo.description)
        assertThat(state.value.todoPriority).isEqualTo(todo.priority)
        assertThat(state.value.todoFlag).isEqualTo(todo.flag)
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

        val state = viewModel.addEditTodoState
        assertThat(state.value.parentTodoCategoryName).isEqualTo(category.name)
        assertThat(state.value.parentTodoCategoryColor).isEqualTo(category.color)
        assertThat(state.value.parentTodoCategoryIconName).isEqualTo(category.iconName)
        assertThat(state.value.inEditingMode).isTrue()
    }
}