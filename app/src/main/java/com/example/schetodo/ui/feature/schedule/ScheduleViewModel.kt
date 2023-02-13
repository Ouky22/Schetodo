package com.example.schetodo.ui.feature.schedule

import androidx.lifecycle.ViewModel
import com.example.schetodo.data.entity.TodoCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor() : ViewModel() {

    private val _uiTodoBlocks = MutableStateFlow(UiTodoBlock())
    val uiTodoBlocks: StateFlow<UiTodoBlock>
        get() = _uiTodoBlocks.asStateFlow()

}

data class UiTodoBlock(
    val id: Int = 0,
    val categories: List<TodoCategory> = emptyList(),
    val todoDescriptions: List<String> = emptyList(),
    val notes: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val duration: String = ""
)