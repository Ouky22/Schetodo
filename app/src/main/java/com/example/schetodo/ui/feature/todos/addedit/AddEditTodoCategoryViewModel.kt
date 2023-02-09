package com.example.schetodo.ui.feature.todos.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.repository.TodoCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoCategoryViewModel @Inject constructor(
    private val todoCategoryRepository: TodoCategoryRepository
) : ViewModel() {

    var todoCategoryName by mutableStateOf("")
        private set

    var todoCategoryColor by mutableStateOf(0L)
        private set

    var todoCategoryIconName by mutableStateOf("")
        private set

    val inEditingMode: Boolean
        get() = todoCategoryId >= 1

    private var todoCategoryId: Int = 0

    private var parentTodoCategoryId: Int? = null


    fun onEvent(event: AddEditTodoCategoryEvent) {
        when (event) {
            is AddEditTodoCategoryEvent.ChangeTodoCategoryName ->
                todoCategoryName = event.name.replace("\n", "").trimStart()
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

    private fun saveTodoCategory() {
        viewModelScope.launch {
            val todoCategory = TodoCategory(
                todoCategoryId,
                todoCategoryName,
                todoCategoryColor,
                parentTodoCategoryId,
                todoCategoryIconName
            )
            todoCategoryRepository.insertOrUpdateTodoCategory(todoCategory)
        }
    }
}