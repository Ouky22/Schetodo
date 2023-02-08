package com.example.schetodo.ui.feature.todos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face3
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.entity.Todo
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.data.repository.TodoCategoryRepository
import com.example.schetodo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodosViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val todoCategoryRepository: TodoCategoryRepository
) : ViewModel() {

    private val _todosState = MutableStateFlow(TodosState())
    val todosState: StateFlow<TodosState>
        get() = _todosState.asStateFlow()

    private var stateJob: Job? = null


    init {
        updateCurrentTodoCategory(null)

        // add test data
//        val category1 = TodoCategory(0, "Test Category 1", 0xff799FCB, null, sportIcons.values.toList()[0].name)
//        val category2 = TodoCategory(0, "Test Category 2", 0xffA7727D, null, sportIcons.values.toList()[1].name)
//        viewModelScope.launch {
//            val c1Id = todoCategoryRepository.insertTodoCategory(category1)
//            val c2Id = todoCategoryRepository.insertTodoCategory(category2)
//
//            val category3 = TodoCategory(0, "Test Category 3", 0xffD3756B, c1Id.toInt(), sportIcons.values.toList()[2].name)
//            todoCategoryRepository.insertTodoCategory(category3)
//
//            val todo1 = Todo(0, "Test 1", TodoPriority.LOW, TodoFlag.UNDONE, c1Id.toInt())
//            val todo2 =
//                Todo(0, "Test 2", TodoPriority.HIGH, TodoFlag.RECURRING, c1Id.toInt())
//            val todo3 =
//                Todo(0, "Test 3", TodoPriority.MEDIUM, TodoFlag.UNDONE, c2Id.toInt())
//
//            todoRepository.insertTodo(todo1)
//            todoRepository.insertTodo(todo2)
//            todoRepository.insertTodo(todo3)
//        }
    }

    fun onEvent(event: TodosEvent) {
        when (event) {
            is TodosEvent.NavigateToNewTodoCategory -> updateCurrentTodoCategory(event.newTodoCategoryId)
            is TodosEvent.NavigateToPreviousTodoCategory -> loadPreviousCategory()
        }
    }

    private fun loadPreviousCategory() {
        val currentTodoCategoryIsTopLevelCategory = todosState.value.currentCategory == null
        if (currentTodoCategoryIsTopLevelCategory)
            return

        val parentCategory = todosState.value.currentCategory?.parentTodoCategoryId
        updateCurrentTodoCategory(parentCategory)
    }

    private fun updateCurrentTodoCategory(currentTodoCategoryId: Int?) {
        stateJob?.cancel()
        stateJob = combine(
            todoCategoryRepository.getTodoCategory(currentTodoCategoryId),
            todoCategoryRepository.getChildTodoCategoriesOf(currentTodoCategoryId),
            todoRepository.getTodosOfTodoCategory(currentTodoCategoryId)
        ) { currentCategory, childCategories, todos ->
            _todosState.value = _todosState.value.copy(
                currentCategory = currentCategory,
                childCategories = childCategories,
                todos = todos
            )
        }.launchIn(viewModelScope)
    }
}