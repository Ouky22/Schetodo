package com.example.schetodo.feature.todos.list

import com.example.schetodo.data.todo.FakeTodoRepository
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.FakeTodoCategoryRepository
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.feature.todos.list.TodosEvent.*
import com.example.schetodo.feature.todos.list.TodosViewModel
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class TodosViewModelTest {

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private val fakeTodoRepository = FakeTodoRepository()
    private val fakeTodoCategoryRepository = FakeTodoCategoryRepository()

    @Test
    fun when_unmarking_todo_category_for_deletion_then_all_sub_categories_are_unmarked_for_deletion() =
        runTest {
            val category1 = TodoCategory(1, "", 0, null, "", true)
            val category2 = TodoCategory(2, "", 0, category1.categoryId, "", true)
            val category3 = TodoCategory(3, "", 0, category2.categoryId, "", true)
            val category4 = TodoCategory(4, "", 0, category2.categoryId, "", true)
            fakeTodoCategoryRepository.insertTodoCategory(category1)
            fakeTodoCategoryRepository.insertTodoCategory(category2)
            fakeTodoCategoryRepository.insertTodoCategory(category3)
            fakeTodoCategoryRepository.insertTodoCategory(category4)
            val viewModel = TodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

            viewModel.onEvent(UnmarkTodoCategoryForDeletion(category1.categoryId))
            advanceUntilIdle()

            assertThat(
                fakeTodoCategoryRepository.getTodoCategory(category1.categoryId)
                    .first()?.markedForDeletion
            ).isFalse()
            assertThat(
                fakeTodoCategoryRepository.getTodoCategory(category2.categoryId)
                    .first()?.markedForDeletion
            ).isFalse()
            assertThat(
                fakeTodoCategoryRepository.getTodoCategory(category3.categoryId)
                    .first()?.markedForDeletion
            ).isFalse()
            assertThat(
                fakeTodoCategoryRepository.getTodoCategory(category4.categoryId)
                    .first()?.markedForDeletion
            ).isFalse()
        }

    @Test
    fun when_unmarking_todo_category_for_deletion_then_all_child_todos_are_unmarked_for_deletion() =
        runTest {
            val category = TodoCategory(1, "Test", 0xffeeddaa, null, "Icon", true)
            val todo1 = Todo(1, "t1", TodoPriority.LOW, TodoFlag.DONE, category.categoryId, true)
            val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.DONE, category.categoryId, true)
            fakeTodoCategoryRepository.insertTodoCategory(category)
            fakeTodoRepository.insertTodo(todo1)
            fakeTodoRepository.insertTodo(todo2)
            val viewModel = TodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

            viewModel.onEvent(UnmarkTodoCategoryForDeletion(category.categoryId))
            advanceUntilIdle()

            assertThat(
                fakeTodoCategoryRepository.getTodoCategory(category.categoryId)
                    .first()?.markedForDeletion
            ).isFalse()
            assertThat(
                fakeTodoRepository.getTodoById(todo1.todoId).first()?.markedForDeletion
            ).isFalse()
            assertThat(
                fakeTodoRepository.getTodoById(todo2.todoId).first()?.markedForDeletion
            ).isFalse()
        }

    @Test
    fun test_unmark_todo_for_deletion() = runTest {
        val todo1 = Todo(1, "t1", TodoPriority.HIGH, TodoFlag.UNDONE, 1)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.IN_PROGRESS, 1)
        val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.RECURRING, 1)
        fakeTodoRepository.insertTodo(todo1)
        fakeTodoRepository.insertTodo(todo2)
        fakeTodoRepository.insertTodo(todo3)
        fakeTodoRepository.markTodoForDeletion(todo1.todoId)
        val viewModel = TodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        viewModel.onEvent(UnmarkTodoForDeletion(todo1.todoId))
        advanceUntilIdle()

        assertThat(
            fakeTodoRepository.getTodoById(todo1.todoId).first()?.markedForDeletion
        ).isFalse()
    }

    @Test
    fun when_current_category_is_null_then_todo_can_not_be_added() = runTest {
        val viewModel = TodosViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        viewModel.onEvent(ClickOnAddCategoryOrTodoButton)

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
        todosViewModel.onEvent(NavigateToNewTodoCategory(childCategory1.categoryId))

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