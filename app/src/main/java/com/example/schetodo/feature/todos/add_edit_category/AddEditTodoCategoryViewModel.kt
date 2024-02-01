package com.example.schetodo.feature.todos.add_edit_category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.todo.TodoRepository
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.todo_category.TodoCategoryRepository
import com.example.schetodo.ui.navigation.todos.AddTodoCategory
import com.example.schetodo.ui.navigation.todos.EditTodoCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoCategoryViewModel @Inject constructor(
    private val todoCategoryRepository: TodoCategoryRepository,
    private val todoRepository: TodoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var todoCategoryName by mutableStateOf("")
        private set

    var todoCategoryColor by mutableStateOf(0xffaaaaaa)
        private set

    var todoCategoryIconName by mutableStateOf("")
        private set

    val inEditingMode: Boolean
        get() = todoCategoryId >= 1

    var showInvalidTodoCategoryNameError by mutableStateOf(false)
        private set

    private val _closeAddEditTodoCategoryScreen = MutableStateFlow(false)
    val closeAddEditTodoCategoryScreen: StateFlow<Boolean>
        get() = _closeAddEditTodoCategoryScreen.asStateFlow()

    var showColorPicker by mutableStateOf(false)
        private set

    var showIconPicker by mutableStateOf(false)
        private set

    var todoCategoryId: Int = 0
        private set

    private var parentTodoCategoryId: Int? = null

    private val maxNameLength = 50

    init {
        val categoryIdForEditing = savedStateHandle.get<Int>(EditTodoCategory.todoCategoryIdArg)
        if (categoryIdForEditing != null) {
            setTodoCategoryForEditing(categoryIdForEditing)
        } else {
            val parentCategoryId =
                savedStateHandle.get<Int>(AddTodoCategory.parentTodoCategoryIdArg)
                    ?: throw Exception("No parent category id provided while adding new TodoCategory")
            setParentTodoCategoryForAdding(parentCategoryId)
        }
    }


    fun onEvent(event: AddEditTodoCategoryEvent) {
        when (event) {
            is AddEditTodoCategoryEvent.ChangeTodoCategoryName -> onTodoCategoryNameChanged(event.name)
            is AddEditTodoCategoryEvent.ChangeTodoCategoryColor -> onTodoCategoryColorChanged(event.color)
            is AddEditTodoCategoryEvent.ChangeTodoCategoryIcon -> onTodoCategoryIconChanged(event.name)
            is AddEditTodoCategoryEvent.SaveTodoCategory -> saveTodoCategory()
            is AddEditTodoCategoryEvent.ShowColorPicker -> onShowColorPicker()
            is AddEditTodoCategoryEvent.ShowIconPicker -> onShowIconPicker()
            is AddEditTodoCategoryEvent.MarkTodoCategoryForDeletion -> onMarkTodoCategoryForDeletion()
        }
    }

    private fun setTodoCategoryForEditing(todoCategoryId: Int) {
        this.todoCategoryId = todoCategoryId

        viewModelScope.launch {
            val category = todoCategoryRepository.getTodoCategory(todoCategoryId).first()
                ?: throw NoSuchElementException("There is no TodoCategory with id $todoCategoryId")
            todoCategoryName = category.name
            todoCategoryColor = category.color
            todoCategoryIconName = category.iconName
            parentTodoCategoryId = category.parentTodoCategoryId
        }
    }

    private fun setParentTodoCategoryForAdding(todoCategoryId: Int) {
        parentTodoCategoryId =
            if (todoCategoryId <= 0)
                null
            else
                todoCategoryId
    }

    private fun onShowColorPicker() {
        showColorPicker = true
        showIconPicker = false
    }

    private fun onShowIconPicker() {
        showIconPicker = true
        showColorPicker = false
    }

    private fun onTodoCategoryIconChanged(newIconName: String) {
        todoCategoryIconName = newIconName
        showIconPicker = false
    }

    private fun onTodoCategoryColorChanged(newColor: Long) {
        todoCategoryColor = newColor
        showColorPicker = false
    }

    private fun onTodoCategoryNameChanged(newName: String) {
        todoCategoryName = newName.replace("\n", "").trimStart().take(maxNameLength)
        showInvalidTodoCategoryNameError = false
    }

    private fun onMarkTodoCategoryForDeletion() {
        if (!inEditingMode)
            return

        viewModelScope.launch {
            markAllSubCategoriesAndTodosForDeletion(mutableListOf(todoCategoryId))
            _closeAddEditTodoCategoryScreen.value = true
        }
    }

    private suspend fun markAllSubCategoriesAndTodosForDeletion(todoCategoryIds: MutableList<Int>) {
        if (todoCategoryIds.isEmpty())
            return

        val todoCategoryId = todoCategoryIds.removeLast()
        todoCategoryRepository.markTodoCategoryForDeletion(todoCategoryId)
        todoRepository.markAllTodosOfCategoryForDeletion(todoCategoryId)

        val subCategoryIds = todoCategoryRepository.getChildTodoCategoriesOf(todoCategoryId).first()
            .map { it.categoryId }

        markAllSubCategoriesAndTodosForDeletion((todoCategoryIds + subCategoryIds).toMutableList())
    }

    private fun saveTodoCategory() {
        if (!validNameEntered()) {
            showInvalidTodoCategoryNameError = true
            return
        }

        viewModelScope.launch {
            val todoCategory = TodoCategory(
                todoCategoryId,
                todoCategoryName.trim(),
                todoCategoryColor,
                parentTodoCategoryId,
                todoCategoryIconName
            )
            todoCategoryRepository.insertOrUpdateTodoCategory(todoCategory)
            _closeAddEditTodoCategoryScreen.value = true
        }
    }

    private fun validNameEntered(): Boolean {
        return todoCategoryName.trim() != ""
    }
}