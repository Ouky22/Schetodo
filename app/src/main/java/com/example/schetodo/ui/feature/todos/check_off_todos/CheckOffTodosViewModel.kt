package com.example.schetodo.ui.feature.todos.check_off_todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.entity.Todo
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.data.repository.TodoCategoryRepository
import com.example.schetodo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckOffTodosViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val todoCategoryRepository: TodoCategoryRepository
) : ViewModel() {

    private val _todosInProgress = MutableStateFlow(emptyList<TodoCategoryTodoPair>())
    val todosInProgress: StateFlow<List<TodoCategoryTodoPair>>
        get() = _todosInProgress.asStateFlow()

    init {
        viewModelScope.launch {
            todoRepository.getTodosInProgress().map { todos ->
                todos.map { todo ->
                    val category = todoCategoryRepository.getTodoCategory(todo.categoryId).first()!!
                    TodoCategoryTodoPair(todo, category)
                }
            }.collect { todos ->
                _todosInProgress.value = todos
            }
        }
    }

    fun onEvent(event: CheckOffTodosEvent) {
        when (event) {
            is CheckOffTodosEvent.MarkTodoForCheckOff -> onMarkTodoForCheckOff(event.todoId)
            is CheckOffTodosEvent.UndoMarkTodoForCheckOff -> onUndoMarkTodoForCheckOff(event.todoId)
            is CheckOffTodosEvent.CheckOffMarkedTodos -> onCheckOffTodos()
        }
    }

    private fun onCheckOffTodos() {
        viewModelScope.launch {
            _todosInProgress.value.filter { it.checkedOff }.forEach { todoCategoryTodoPair ->
                todoRepository.updateTodo(
                    todoCategoryTodoPair.todo.copy(flag = TodoFlag.DONE)
                )
            }
        }
    }

    private fun onUndoMarkTodoForCheckOff(todoId: Int) {
        _todosInProgress.value = _todosInProgress.value.map {
            if (todoId == it.todo.todoId)
                it.copy(checkedOff = false)
            else
                it
        }
    }

    private fun onMarkTodoForCheckOff(todoId: Int) {
        _todosInProgress.value = _todosInProgress.value.map {
            if (todoId == it.todo.todoId)
                it.copy(checkedOff = true)
            else
                it
        }
    }
}

data class TodoCategoryTodoPair(
    val todo: Todo,
    val todoCategory: TodoCategory,
    var checkedOff: Boolean = false
)