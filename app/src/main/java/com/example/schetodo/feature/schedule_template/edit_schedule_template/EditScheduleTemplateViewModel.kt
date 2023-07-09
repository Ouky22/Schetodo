package com.example.schetodo.feature.schedule_template.edit_schedule_template

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.feature.schedule_template.edit_schedule_template.EditScheduleTemplateEvent.*
import com.example.schetodo.feature.schedule_template.use_case.DeleteScheduleTemplateUseCase
import com.example.schetodo.feature.use_case.GeneralUseCases
import com.example.schetodo.ui.navigation.schedule.EditScheduleTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditScheduleTemplateViewModel @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository,
    private val generalUseCases: GeneralUseCases,
    private val deleteScheduleTemplateUseCase: DeleteScheduleTemplateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(EditScheduleTemplateState())
    val state: StateFlow<EditScheduleTemplateState>
        get() = _state.asStateFlow()

    private val templateId: Int

    init {
        templateId = savedStateHandle[EditScheduleTemplate.scheduleTemplateIdArg]
            ?: throw Exception("No schedule template id provided")

        viewModelScope.launch {
            scheduleBlockRepository.getScheduleBlocksOfScheduleTemplate(templateId).collect {
                val scheduleListItems = generalUseCases.convertScheduleBlocksToScheduleListItems(it)
                _state.value = _state.value.copy(scheduleItems = scheduleListItems)
            }
        }
    }

    fun onEvent(editScheduleTemplateEvent: EditScheduleTemplateEvent) {
        when (editScheduleTemplateEvent) {
            is DeleteScheduleTemplate -> deleteScheduleTemplate()
        }
    }

    private fun deleteScheduleTemplate() {
        viewModelScope.launch {
            deleteScheduleTemplateUseCase(templateId)
        }
    }
}