package com.example.schetodo.feature.schedule_template.edit_schedule_template

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditScheduleTemplateViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(EditScheduleTemplateState())
    val state: StateFlow<EditScheduleTemplateState>
        get() = _state.asStateFlow()
}