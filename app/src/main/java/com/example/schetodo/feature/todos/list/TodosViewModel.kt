package com.example.schetodo.feature.todos.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.todo.TodoFilterSettings
import com.example.schetodo.data.todo_category.TodoCategoryRepository
import com.example.schetodo.data.todo.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.schetodo.feature.todos.list.TodosEvent.*
import javax.inject.Inject

@HiltViewModel
class TodosViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val todoCategoryRepository: TodoCategoryRepository
) : ViewModel() {

    private val _todosState = MutableStateFlow(TodosState())
    val todosState: StateFlow<TodosState>
        get() = _todosState.asStateFlow()

    private val _navigateToAddTodoCategoryScreen = MutableSharedFlow<Boolean>()
    val navigateToAddTodoCategoryScreen: SharedFlow<Boolean>
        get() = _navigateToAddTodoCategoryScreen.asSharedFlow()

    private val _navigateToAddTodoScreen = MutableSharedFlow<Boolean>()
    val navigateToAddTodoScreen: SharedFlow<Boolean>
        get() = _navigateToAddTodoScreen.asSharedFlow()

    private var stateJob: Job? = null

    init {
        setCurrentTodoCategory(null)
        checkIfThereAreTodosInProgress()
    }

    fun onEvent(event: TodosEvent) {
        when (event) {
            is NavigateToNewTodoCategory -> setCurrentTodoCategory(event.newTodoCategoryId)
            is NavigateToPreviousTodoCategory -> loadPreviousCategory()
            is ClickOnAddCategoryOrTodoButton -> onClickAddCategoryOrTodoButton()
            is CloseAddCategoryOrTodoDialog -> onCloseAddCategoryOrTodoDialog()
            is NavigateToAddTodoCategoryScreen -> onNavigateToAddTodoCategoryScreen()
            is NavigateToAddTodoScreen -> onNavigateToAddTodoScreen()
            is ChangeTodoFilterSettings -> onChangeTodoFilterSettings(event.newFilterSettings)
            is UnmarkTodoForDeletion -> onUnmarkTodoForDeletion(event.todoId)
            is UnmarkTodoCategoryForDeletion -> onUnmarkTodoCategoryForDeletion(event.categoryId)
        }
    }

    private fun onUnmarkTodoCategoryForDeletion(categoryId: Int) {
        viewModelScope.launch {
            unmarkAllSubCategoriesAndTodosForDeletion(mutableListOf(categoryId))
        }
    }

    private suspend fun unmarkAllSubCategoriesAndTodosForDeletion(todoCategoryIds: MutableList<Int>) {
        if (todoCategoryIds.isEmpty())
            return

        val todoCategoryId = todoCategoryIds.removeLast()
        todoCategoryRepository.unmarkTodoCategoryForDeletion(todoCategoryId)
        todoRepository.unmarkAllTodosOfCategoryForDeletion(todoCategoryId)

        val subCategoryIds = todoCategoryRepository.getChildTodoCategoriesOf(
            todoCategoryId, withMarkedForDeletion = true
        ).first().map { it.categoryId }

        unmarkAllSubCategoriesAndTodosForDeletion((todoCategoryIds + subCategoryIds).toMutableList())
    }

    private fun onUnmarkTodoForDeletion(todoId: Int) {
        viewModelScope.launch {
            todoRepository.unmarkTodoForDeletion(todoId)
        }
    }

    private fun onChangeTodoFilterSettings(newFilterSettings: TodoFilterSettings) {
        _todosState.value = _todosState.value.copy(todoFilterSettings = newFilterSettings)
        viewModelScope.launch {
            todoRepository.setTodoFilterSettings(newFilterSettings)
        }
    }

    private fun onCloseAddCategoryOrTodoDialog() {
        _todosState.value = _todosState.value.copy(showAddCategoryOrTodoDialog = false)
    }

    private fun onClickAddCategoryOrTodoButton() {
        val openAddCategoryOrTodoDialog = _todosState.value.currentCategory != null
        if (openAddCategoryOrTodoDialog)
            _todosState.value = _todosState.value.copy(showAddCategoryOrTodoDialog = true)
        else
            viewModelScope.launch {
                _navigateToAddTodoCategoryScreen.emit(true)
            }
    }

    private fun onNavigateToAddTodoScreen() {
        _todosState.value = _todosState.value.copy(showAddCategoryOrTodoDialog = false)
        viewModelScope.launch {
            _navigateToAddTodoScreen.emit(true)
        }
    }

    private fun onNavigateToAddTodoCategoryScreen() {
        _todosState.value = _todosState.value.copy(showAddCategoryOrTodoDialog = false)
        viewModelScope.launch {
            _navigateToAddTodoCategoryScreen.emit(true)
        }
    }

    private fun loadPreviousCategory() {
        val currentTodoCategoryIsTopLevelCategory = todosState.value.currentCategory == null
        if (currentTodoCategoryIsTopLevelCategory)
            return

        val parentCategory = todosState.value.currentCategory?.parentTodoCategoryId
        setCurrentTodoCategory(parentCategory)
    }

    private fun checkIfThereAreTodosInProgress() {
        viewModelScope.launch {
            todoRepository.getTodosInProgress().collect {
                if (it.isEmpty())
                    _todosState.value = _todosState.value.copy(checkOffTodosButtonActivated = false)
                else
                    _todosState.value = _todosState.value.copy(checkOffTodosButtonActivated = true)
            }
        }
    }

    private fun setCurrentTodoCategory(currentTodoCategoryId: Int?) {
        stateJob?.cancel()
        stateJob = combine(
            todoCategoryRepository.getTodoCategory(currentTodoCategoryId),
            todoCategoryRepository.getChildTodoCategoriesOf(currentTodoCategoryId),
            todoRepository.getTodosOfTodoCategory(currentTodoCategoryId),
            todoRepository.getTodoFilterSettings()
        ) { currentCategory, childCategories, todos, todoFilterSettings ->
            _todosState.value = _todosState.value.copy(
                currentCategory = currentCategory,
                childCategories = childCategories,
                todos = todos,
                todoFilterSettings = todoFilterSettings
            )
        }.launchIn(viewModelScope)
    }
}