package com.example.schetodo.ui.feature.schedule.schedule_template.add_edit_schedule_template

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddEditScheduleTemplateViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AddEditScheduleTemplateState())
    val state: StateFlow<AddEditScheduleTemplateState>
        get() = _state.asStateFlow()
}