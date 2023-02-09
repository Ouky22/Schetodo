package com.example.schetodo.ui.feature.todos.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.repository.TodoCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoCategoryViewModel @Inject constructor(
    private val todoCategoryRepository: TodoCategoryRepository
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

    var todoCategorySuccessfullySaved = MutableStateFlow(false)

    private var todoCategoryId: Int = 0

    private var parentTodoCategoryId: Int? = null


    fun onEvent(event: AddEditTodoCategoryEvent) {
        when (event) {
            is AddEditTodoCategoryEvent.ChangeTodoCategoryName -> onTodoCategoryNameChanged(event.name)
            is AddEditTodoCategoryEvent.ChangeTodoCategoryColor -> todoCategoryColor = event.color
            is AddEditTodoCategoryEvent.ChangeTodoCategoryIcon -> todoCategoryIconName = event.name
            is AddEditTodoCategoryEvent.SaveTodoCategory -> saveTodoCategory()
        }
    }

    fun setTodoCategoryForEditing(todoCategoryId: Int) {
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

    fun setParentTodoCategoryForAdding(todoCategoryId: Int) {
        parentTodoCategoryId =
            if (todoCategoryId <= 0)
                null
            else
                todoCategoryId
    }

    private fun onTodoCategoryNameChanged(newName: String) {
        todoCategoryName = newName.replace("\n", "").trimStart()
        showInvalidTodoCategoryNameError = false
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
            todoCategorySuccessfullySaved.value = true
        }
    }

    private fun validNameEntered(): Boolean {
        return todoCategoryName.trim() != ""
    }
}