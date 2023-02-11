package com.example.schetodo.ui.feature.todos.add_edit_todo

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.data.repository.TodoCategoryRepository
import com.example.schetodo.data.repository.TodoRepository
import com.example.schetodo.ui.navigation.AddTodo
import com.example.schetodo.ui.navigation.EditTodo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val todoCategoryRepository: TodoCategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _addEditTodoState = mutableStateOf(AddEditTodoState())
    val addEditTodoState: State<AddEditTodoState>
        get() = _addEditTodoState

    init {
        val todoIdForEditing = savedStateHandle.get<Int>(EditTodo.todoId)
        if (todoIdForEditing != null) {
            setTodoForEditing(todoIdForEditing)
        } else {
            val parentTodoCategory = savedStateHandle.get<Int>(AddTodo.parentTodoCategoryIdArg)
                ?: throw Exception("No parent category id provided while adding new Todo")
            setParentTodoCategoryOfTodo(parentTodoCategory)
        }
    }

    fun onEvent(event: AddEditTodoEvent) {
        when (event) {
            is AddEditTodoEvent.ChangeTodoDescription -> onChangeDescription(event.todoDescription)
            is AddEditTodoEvent.ChangeTodoPriority -> onChangeTodoPriority(event.todoPriority)
            is AddEditTodoEvent.ChangeTodoIsRecurring -> onChangeTodoIsRecurring(event.recurring)
        }
    }

    private fun onChangeDescription(newDescription: String) {
        _addEditTodoState.value = _addEditTodoState.value.copy(todoDescription = newDescription)
    }

    private fun onChangeTodoPriority(newPriority: TodoPriority) {
        _addEditTodoState.value = _addEditTodoState.value.copy(todoPriority = newPriority)
    }

    private fun onChangeTodoIsRecurring(recurring: Boolean) {
        _addEditTodoState.value = _addEditTodoState.value.copy(todoIsRecurring = recurring)
    }

    private fun setParentTodoCategoryOfTodo(todoCategoryId: Int) {
        viewModelScope.launch {
            val category = todoCategoryRepository.getTodoCategory(todoCategoryId).first()
                ?: throw Exception("There is no todo category with id $todoCategoryId")

            _addEditTodoState.value = _addEditTodoState.value.copy(
                parentTodoCategoryName = category.name,
                parentTodoCategoryIconName = category.iconName,
                parentTodoCategoryColor = category.color
            )
        }
    }

    private fun setTodoForEditing(todoId: Int) {
        viewModelScope.launch {
            val todo = todoRepository.getTodoById(todoId).first()
                ?: throw Exception("There is no todo with id $todoId")

            _addEditTodoState.value = _addEditTodoState.value.copy(
                todoDescription = todo.description,
                todoPriority = todo.priority,
                todoIsRecurring = todo.flag == TodoFlag.RECURRING,
                inEditingMode = true
            )
            setParentTodoCategoryOfTodo(todo.categoryId)
        }
    }
}