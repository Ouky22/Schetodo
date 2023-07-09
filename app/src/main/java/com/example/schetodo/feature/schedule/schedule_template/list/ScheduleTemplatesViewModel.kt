package com.example.schetodo.feature.schedule.schedule_template.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.schedule_template.ScheduleTemplate
import com.example.schetodo.data.schedule_template.ScheduleTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleTemplatesViewModel @Inject constructor(
    scheduleTemplateRepository: ScheduleTemplateRepository
) : ViewModel() {

    private val _scheduleTemplates = MutableStateFlow<List<ScheduleTemplate>>(emptyList())
    val scheduleTemplates: StateFlow<List<ScheduleTemplate>>
        get() = _scheduleTemplates.asStateFlow()

    init {
        viewModelScope.launch {
            scheduleTemplateRepository.getAll().collect { scheduleTemplates ->
                _scheduleTemplates.value = scheduleTemplates
            }
        }
    }
}