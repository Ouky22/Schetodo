package com.example.schetodo.ui.feature.todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.repository.TodoCategoryRepository
import com.example.schetodo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TodosViewModel @Inject constructor(
    private val todoRepositoryImpl: TodoRepository,
    private val todoCategoryRepositoryImpl: TodoCategoryRepository
) : ViewModel() {

    private val _todosState = MutableStateFlow(TodosState())
    val todosState: StateFlow<TodosState>
        get() = _todosState.asStateFlow()

    private var stateJob: Job? = null


    init {
        updateCurrentTodoCategory(null)
    }

    fun onEvent(event: TodosEvent) {
        when (event) {
            is TodosEvent.NavigateToNewTodoCategory -> updateCurrentTodoCategory(event.newTodoCategoryId)
        }
    }

    private fun updateCurrentTodoCategory(currentTodoCategoryId: Int?) {
        stateJob?.cancel()
        stateJob = combine(
            todoCategoryRepositoryImpl.getTodoCategory(currentTodoCategoryId),
            todoCategoryRepositoryImpl.getChildTodoCategoriesOf(currentTodoCategoryId),
            todoRepositoryImpl.getTodosOfTodoCategory(currentTodoCategoryId)
        ) { currentCategory, childCategories, todos ->
            _todosState.value = _todosState.value.copy(
                currentCategory = currentCategory,
                childCategories = childCategories,
                todos = todos
            )
        }.launchIn(viewModelScope)
    }
}