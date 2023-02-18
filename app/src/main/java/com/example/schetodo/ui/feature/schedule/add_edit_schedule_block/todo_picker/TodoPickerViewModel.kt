package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.todo_picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoRepository
import com.example.schetodo.data.todo_category.TodoCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TodoPickerViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val todoCategoryRepository: TodoCategoryRepository
) : ViewModel() {

    private val _todoPickerState = MutableStateFlow(TodoPickerState())
    val todoPickerState: StateFlow<TodoPickerState>
        get() = _todoPickerState.asStateFlow()

    private val idsOfSelectedTodos = mutableListOf<Int>()

    private var stateJob: Job? = null

    init {
        setCurrentTodoCategory(null)
    }

    fun onEvent(event: TodoPickerEvent) {
        when (event) {
            is TodoPickerEvent.NavigateToNewTodoCategory -> setCurrentTodoCategory(event.newTodoCategoryId)
            is TodoPickerEvent.NavigateToPreviousTodoCategory -> loadPreviousCategory()
            is TodoPickerEvent.MarkTodoForSelection -> markTodoForSelection(event.todo)
            is TodoPickerEvent.UndoMarkTodoForSelection -> undoMarkTodoForSelection(event.todo)
        }
    }

    private fun markTodoForSelection(todo: Todo) {
        idsOfSelectedTodos.add(todo.todoId)
        synchronizeStateWithIdsOfSelectedTodos()
    }

    private fun undoMarkTodoForSelection(todo: Todo) {
        idsOfSelectedTodos.removeIf { it == todo.todoId }
        synchronizeStateWithIdsOfSelectedTodos()
    }

    private fun synchronizeStateWithIdsOfSelectedTodos() {
        val currentTodos = _todoPickerState.value.todos
        _todoPickerState.value = _todoPickerState.value.copy(
            todos = currentTodos.map {
                val selected = it.todo.todoId in idsOfSelectedTodos
                it.copy(selected = selected)
            }
        )
    }

    private fun loadPreviousCategory() {
        val currentTodoCategoryIsTopLevelCategory = _todoPickerState.value.currentCategory == null
        if (currentTodoCategoryIsTopLevelCategory)
            return

        val parentCategory = _todoPickerState.value.currentCategory?.parentTodoCategoryId
        setCurrentTodoCategory(parentCategory)
    }

    private fun setCurrentTodoCategory(currentTodoCategoryId: Int?) {
        stateJob?.cancel()
        stateJob = combine(
            todoCategoryRepository.getTodoCategory(currentTodoCategoryId),
            todoCategoryRepository.getChildTodoCategoriesOf(currentTodoCategoryId),
            todoRepository.getTodosOfTodoCategory(currentTodoCategoryId)
        ) { currentCategory, childCategories, todos ->
            _todoPickerState.value = _todoPickerState.value.copy(
                currentCategory = currentCategory,
                childCategories = childCategories,
                todos = todos.map {
                    val selected = it.todoId in idsOfSelectedTodos
                    TodoWithSelector(it, selected)
                },
                showTopBarBackButton = currentCategory != null
            )
        }.launchIn(viewModelScope)
    }
}

data class TodoWithSelector(
    val todo: Todo,
    val selected: Boolean
)