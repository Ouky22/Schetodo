package com.example.schetodo.ui.feature.todos.check_off_todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.entity.Todo
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.entity.TodoFlag
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

    val todosMarkedForCheckOff: Boolean
        get() = _todosInProgress.value.any { it.markedForCheckOff }

    private val _snackBarFlow = MutableSharedFlow<CheckOffTodosSnackBarType>()
    val snackBarFlow: SharedFlow<CheckOffTodosSnackBarType>
        get() = _snackBarFlow.asSharedFlow()

    private val todosRecentlyCheckedOff = mutableListOf<Todo>()
    private val todosRecentlyMarkedAsUndone = mutableListOf<Todo>()

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
            is CheckOffTodosEvent.MarkTodoForCheckOff -> markTodoForCheckOff(event.todoId)
            is CheckOffTodosEvent.UndoMarkTodoForCheckOff -> undoMarkTodoForCheckOff(event.todoId)
            is CheckOffTodosEvent.CheckOffMarkedTodos -> checkOffTodos()
            is CheckOffTodosEvent.CheckOffTodo -> checkOffTodo(event.todoId)
            is CheckOffTodosEvent.MarkTodoAsUndone -> markTodoAsUndone(event.todoId)
            is CheckOffTodosEvent.UndoCheckOffTodos -> undoCheckOffTodos()
            is CheckOffTodosEvent.UndoMarkTodoAsUndone -> undoMarkTodoAsUndone()
        }
    }

    private fun undoCheckOffTodos() {
        viewModelScope.launch {
            todosRecentlyCheckedOff.forEach {
                todoRepository.updateTodo(it.copy(flag = TodoFlag.IN_PROGRESS))
            }
            todosRecentlyCheckedOff.clear()
        }
    }

    private fun undoMarkTodoAsUndone() {
        viewModelScope.launch {
            todosRecentlyMarkedAsUndone.forEach {
                todoRepository.updateTodo(it.copy(flag = TodoFlag.IN_PROGRESS))
            }
            todosRecentlyMarkedAsUndone.clear()
        }
    }

    private fun markTodoAsUndone(todoId: Int) {
        viewModelScope.launch {
            val undoneTodo = _todosInProgress.value.find {
                it.todo.todoId == todoId
            }?.todo?.copy(flag = TodoFlag.UNDONE) ?: return@launch

            todoRepository.updateTodo(undoneTodo)

            _snackBarFlow.emit(CheckOffTodosSnackBarType.UNDO_MARK_TODO_AS_UNDONE)
            todosRecentlyMarkedAsUndone.clear()
            todosRecentlyMarkedAsUndone.add(undoneTodo)
        }
    }

    private fun checkOffTodo(todoId: Int) {
        viewModelScope.launch {
            val todoToCheckOff =
                _todosInProgress.value.find {
                    it.todo.todoId == todoId
                }?.todo?.copy(flag = TodoFlag.DONE) ?: return@launch

            todoRepository.updateTodo(todoToCheckOff)

            _snackBarFlow.emit(CheckOffTodosSnackBarType.UNDO_CHECK_OFF_TODOS)
            todosRecentlyCheckedOff.clear()
            todosRecentlyCheckedOff.add(todoToCheckOff)
        }
    }

    private fun checkOffTodos() {
        viewModelScope.launch {
            val todosToCheckOff = _todosInProgress.value.filter { it.markedForCheckOff }.map { it.todo }

            todosToCheckOff.forEach { todoCategoryTodoPair ->
                todoRepository.updateTodo(
                    todoCategoryTodoPair.copy(flag = TodoFlag.DONE)
                )
            }

            _snackBarFlow.emit(CheckOffTodosSnackBarType.UNDO_CHECK_OFF_TODOS)
            todosRecentlyCheckedOff.clear()
            todosRecentlyCheckedOff.addAll(todosToCheckOff)
        }
    }

    private fun undoMarkTodoForCheckOff(todoId: Int) {
        _todosInProgress.value = _todosInProgress.value.map {
            if (todoId == it.todo.todoId)
                it.copy(markedForCheckOff = false)
            else
                it
        }
    }

    private fun markTodoForCheckOff(todoId: Int) {
        _todosInProgress.value = _todosInProgress.value.map {
            if (todoId == it.todo.todoId)
                it.copy(markedForCheckOff = true)
            else
                it
        }
    }
}

enum class CheckOffTodosSnackBarType {
    UNDO_CHECK_OFF_TODOS,
    UNDO_MARK_TODO_AS_UNDONE
}

data class TodoCategoryTodoPair(
    val todo: Todo,
    val todoCategory: TodoCategory,
    var markedForCheckOff: Boolean = false
)