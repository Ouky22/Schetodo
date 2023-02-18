package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.todo.TodoRepository
import com.example.schetodo.data.todo_category.TodoCategoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

open class PickerViewModel<T>(
    private val todoRepository: TodoRepository,
    private val todoCategoryRepository: TodoCategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PickerState<T>())
    val state: StateFlow<PickerState<T>>
        get() = _state.asStateFlow()

    private var stateJob: Job? = null


    init {
        navigateToTodoCategory(null)
    }

    fun markItemForSelection(item: T) {
        val currentlySelectedItems = _state.value.selectedItems.toMutableList()
        _state.value = _state.value.copy(
            selectedItems = currentlySelectedItems.apply { add(item) }
        )
    }

    fun undoMarkItemForSelection(item: T) {
        val currentlySelectedItems = _state.value.selectedItems.toMutableList()
        _state.value = _state.value.copy(
            selectedItems = currentlySelectedItems.apply { remove(item) }
        )
    }

    fun navigateToPreviousCategory() {
        val currentTodoCategoryIsTopLevelCategory = _state.value.currentCategory == null
        if (currentTodoCategoryIsTopLevelCategory)
            return

        val parentCategory = _state.value.currentCategory?.parentTodoCategoryId
        navigateToTodoCategory(parentCategory)
    }

    fun navigateToTodoCategory(todoCategoryId: Int?) {
        stateJob?.cancel()
        stateJob = combine(
            todoCategoryRepository.getTodoCategory(todoCategoryId),
            todoCategoryRepository.getChildTodoCategoriesOf(todoCategoryId),
            todoRepository.getTodosOfTodoCategory(todoCategoryId)
        ) { currentCategory, childCategories, todos ->
            _state.value = _state.value.copy(
                currentCategory = currentCategory,
                childCategories = childCategories,
                todos = todos,
                showTopBarBackButton = currentCategory != null
            )
        }.launchIn(viewModelScope)
    }
}