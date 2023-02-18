package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.todo_picker

import com.example.schetodo.data.todo.FakeTodoRepository
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.FakeTodoCategoryRepository
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class TodoPickerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeTodoCategoryRepository = FakeTodoCategoryRepository()
    private val fakeTodoRepository = FakeTodoRepository()

    private lateinit var topLevelTodoCategory1: TodoCategory
    private lateinit var topLevelTodoCategory2: TodoCategory
    private lateinit var childCategory1: TodoCategory
    private lateinit var todoOfChildCategory1: Todo
    private lateinit var todoOfChildCategory2: Todo
    private lateinit var todoOfTopLevelCategory1 : Todo


    @Before
    fun init() {
        topLevelTodoCategory1 = TodoCategory(1, "C1", 0xFFFFFF, null, "")
        topLevelTodoCategory2 = TodoCategory(2, "C2", 0xFFFFFF, null, "")
        childCategory1 = TodoCategory(3, "C3", 0xAAAAAA, topLevelTodoCategory1.categoryId, "")
        todoOfChildCategory1 = Todo(1, "t1", TodoPriority.MEDIUM, TodoFlag.UNDONE, childCategory1.categoryId)
        todoOfChildCategory2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.RECURRING, childCategory1.categoryId)
        todoOfTopLevelCategory1 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.RECURRING, topLevelTodoCategory1.categoryId)
    }

    @Test
    fun test_undo_mark_todo_for_selection() = runTest {
        fakeTodoCategoryRepository.insertTodoCategory(topLevelTodoCategory1)
        fakeTodoCategoryRepository.insertTodoCategory(childCategory1)
        fakeTodoRepository.insertTodo(todoOfChildCategory1)
        fakeTodoRepository.insertTodo(todoOfChildCategory2)

        val viewModel = TodoPickerViewModel(fakeTodoRepository, fakeTodoCategoryRepository)
        viewModel.onEvent(TodoPickerEvent.NavigateToNewTodoCategory(childCategory1.categoryId))
        viewModel.onEvent(TodoPickerEvent.MarkTodoForSelection(todoOfChildCategory1))
        viewModel.onEvent(TodoPickerEvent.UndoMarkTodoForSelection(todoOfChildCategory1))

        advanceUntilIdle()

        val state = viewModel.todoPickerState.value
        assertThat(state.todos).containsExactly(
            TodoWithSelector(todoOfChildCategory1, false),
            TodoWithSelector(todoOfChildCategory2, false)
        )
        assertThat(state.currentCategory).isEqualTo(childCategory1)
        assertThat(state.childCategories).isEmpty()
    }

    @Test
    fun test_mark_todo_for_selection() = runTest {
        fakeTodoCategoryRepository.insertTodoCategory(topLevelTodoCategory1)
        fakeTodoCategoryRepository.insertTodoCategory(childCategory1)
        fakeTodoRepository.insertTodo(todoOfChildCategory1)
        fakeTodoRepository.insertTodo(todoOfChildCategory2)

        val viewModel = TodoPickerViewModel(fakeTodoRepository, fakeTodoCategoryRepository)
        viewModel.onEvent(TodoPickerEvent.NavigateToNewTodoCategory(childCategory1.categoryId))
        viewModel.onEvent(TodoPickerEvent.MarkTodoForSelection(todoOfChildCategory1))

        advanceUntilIdle()

        val state = viewModel.todoPickerState.value
        assertThat(state.todos).containsExactly(
            TodoWithSelector(todoOfChildCategory1, true),
            TodoWithSelector(todoOfChildCategory2, false)
        )
        assertThat(state.currentCategory).isEqualTo(childCategory1)
        assertThat(state.childCategories).isEmpty()
    }

    @Test
    fun when_current_category_changes_then_load_categories_and_todos_of_new_category() = runTest {
        fakeTodoCategoryRepository.insertTodoCategory(topLevelTodoCategory1)
        fakeTodoCategoryRepository.insertTodoCategory(topLevelTodoCategory2)
        fakeTodoCategoryRepository.insertTodoCategory(childCategory1)
        fakeTodoRepository.insertTodo(todoOfChildCategory1)
        fakeTodoRepository.insertTodo(todoOfTopLevelCategory1)

        val viewModel = TodoPickerViewModel(fakeTodoRepository, fakeTodoCategoryRepository)
        viewModel.onEvent(TodoPickerEvent.NavigateToNewTodoCategory(childCategory1.categoryId))

        advanceUntilIdle()

        val state = viewModel.todoPickerState.value
        assertThat(state.todos).containsExactly(
            TodoWithSelector(todoOfChildCategory1, false)
        )
        assertThat(state.currentCategory).isEqualTo(childCategory1)
        assertThat(state.childCategories).isEmpty()
        assertThat(state.showTopBarBackButton).isTrue()
    }

    @Test
    fun when_there_are_top_level_categories_then_view_model_loads_them_at_start() = runTest {
        fakeTodoCategoryRepository.insertTodoCategory(topLevelTodoCategory1)
        fakeTodoCategoryRepository.insertTodoCategory(topLevelTodoCategory2)
        fakeTodoCategoryRepository.insertTodoCategory(childCategory1)

        val viewModel = TodoPickerViewModel(fakeTodoRepository, fakeTodoCategoryRepository)

        advanceUntilIdle()

        val state = viewModel.todoPickerState.value
        assertThat(state.currentCategory).isNull()
        assertThat(state.childCategories.size).isEqualTo(2)
        assertThat(state.childCategories).contains(topLevelTodoCategory1)
        assertThat(state.childCategories).contains(topLevelTodoCategory2)
    }
}