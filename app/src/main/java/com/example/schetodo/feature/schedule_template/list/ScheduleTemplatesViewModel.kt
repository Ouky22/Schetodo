package com.example.schetodo.feature.schedule_template.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.schedule_template.ScheduleTemplate
import com.example.schetodo.data.schedule_template.ScheduleTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.schetodo.feature.schedule_template.list.ScheduleTemplatesEvent.*
import com.example.schetodo.feature.schedule_template.use_case.ScheduleTemplateUseCases
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleTemplatesViewModel @Inject constructor(
    scheduleTemplateRepository: ScheduleTemplateRepository,
    private val templateUseCases: ScheduleTemplateUseCases
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

    fun onEvent(event: ScheduleTemplatesEvent) {
        when (event) {
            is UndoDeletionOfScheduleTemplate -> undoMarkOfScheduleTemplate(event.templateId)
        }
    }

    private fun undoMarkOfScheduleTemplate(templateId: Int) {
        viewModelScope.launch {
            templateUseCases.unmarkScheduleTemplateForDeletion(templateId)
        }
    }
}