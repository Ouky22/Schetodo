package com.example.schetodo.ui.feature.todos.list

import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.repository.FakeTodoCategoryRepository
import com.example.schetodo.data.todo.FakeTodoRepository
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class TodosViewModelTest {

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private val fakeTodoRepository = FakeTodoRepository()
    private val fakeTodoCategoryRepository = FakeTodoCategoryRepository()

    @Test
    fun when_current_category_is_null_then_todo_can_not_be_added() = runTest {
        val viewModel = TodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        viewModel.onEvent(TodosEvent.ClickOnAddCategoryOrTodoButton)

        assertThat(viewModel.navigateToAddTodoCategoryScreen.first()).isTrue()
        assertThat(viewModel.todosState.value.showAddCategoryOrTodoDialog).isFalse()
    }

    @Test
    fun when_current_category_changes_then_load_categories_and_todos_of_new_category() = runTest {
        val topLevelTodoCategory1 = TodoCategory(1, "C1", 0xFFFFFF, null, "")
        val topLevelTodoCategory2 = TodoCategory(2, "C2", 0xFFFFFF, null, "")
        val childCategory1 = TodoCategory(3, "C3", 0xAAAAAA, topLevelTodoCategory1.categoryId, "")
        val todoOfChildCategory =
            Todo(1, "t1", TodoPriority.MEDIUM, TodoFlag.UNDONE, childCategory1.categoryId)
        val todoOfTopLevelCategory1 =
            Todo(2, "t2", TodoPriority.LOW, TodoFlag.RECURRING, topLevelTodoCategory1.categoryId)
        fakeTodoCategoryRepository.insertTodoCategory(topLevelTodoCategory1)
        fakeTodoCategoryRepository.insertTodoCategory(topLevelTodoCategory2)
        fakeTodoCategoryRepository.insertTodoCategory(childCategory1)
        fakeTodoRepository.insertTodo(todoOfChildCategory)
        fakeTodoRepository.insertTodo(todoOfTopLevelCategory1)

        val todosViewModel = TodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)
        todosViewModel.onEvent(TodosEvent.NavigateToNewTodoCategory(childCategory1.categoryId))

        advanceUntilIdle()

        val todosState = todosViewModel.todosState.value
        assertThat(todosState.todos.size).isEqualTo(1)
        assertThat(todosState.todos).contains(todoOfChildCategory)
        assertThat(todosState.currentCategory).isEqualTo(childCategory1)
        assertThat(todosState.childCategories).isEmpty()
    }

    @Test
    fun when_there_are_top_level_categories_then_view_model_loads_them_at_start() = runTest {
        val topLevelTodoCategory1 = TodoCategory(1, "C1", 0xFFFFFF, null, "")
        val topLevelTodoCategory2 = TodoCategory(2, "C2", 0xFFFFFF, null, "")
        val childCategory1 = TodoCategory(3, "C3", 0xAAAAAA, topLevelTodoCategory1.categoryId, "")
        fakeTodoCategoryRepository.insertTodoCategory(topLevelTodoCategory1)
        fakeTodoCategoryRepository.insertTodoCategory(topLevelTodoCategory2)
        fakeTodoCategoryRepository.insertTodoCategory(childCategory1)

        val todosViewModel = TodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        advanceUntilIdle()

        val todosState = todosViewModel.todosState.value
        assertThat(todosState.currentCategory).isNull()
        assertThat(todosState.childCategories.size).isEqualTo(2)
        assertThat(todosState.childCategories).contains(topLevelTodoCategory1)
        assertThat(todosState.childCategories).contains(topLevelTodoCategory2)
    }
}