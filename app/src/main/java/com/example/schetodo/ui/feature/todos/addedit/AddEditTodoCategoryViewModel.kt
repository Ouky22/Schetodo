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

    var todoCategoryIcon by mutableStateOf("")
        private set


}