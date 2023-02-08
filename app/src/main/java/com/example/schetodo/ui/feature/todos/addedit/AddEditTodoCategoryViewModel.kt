package com.example.schetodo.ui.feature.todos.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditTodoCategoryViewModel @Inject constructor() : ViewModel() {

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
            is AddEditTodoCategoryEvent.ChangeTodoCategoryName -> todoCategoryName = event.name
            is AddEditTodoCategoryEvent.ChangeTodoCategoryColor -> todoCategoryColor = event.color
            is AddEditTodoCategoryEvent.ChangeTodoCategoryIcon -> todoCategoryIconName = event.name
        }
    }

    fun setTodoCategoryForEditing(todoCategoryId: Int) {
        this.todoCategoryId = todoCategoryId
        // TODO load TodoCategory from repo
    }

    fun setParentTodoCategoryForAdding(todoCategoryId: Int) {
        parentTodoCategoryId =
            if (todoCategoryId <= 0)
                null
            else
                todoCategoryId
    }
}