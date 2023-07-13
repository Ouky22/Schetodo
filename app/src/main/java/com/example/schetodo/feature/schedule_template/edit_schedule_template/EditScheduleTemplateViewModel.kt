package com.example.schetodo.feature.schedule_template.edit_schedule_template

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.feature.schedule_template.edit_schedule_template.EditScheduleTemplateEvent.*
import com.example.schetodo.feature.schedule_template.use_case.ApplyScheduleConflictStrategy
import com.example.schetodo.feature.schedule_template.use_case.ApplyScheduleTemplateUseCase
import com.example.schetodo.feature.schedule_template.use_case.ScheduleTemplateUseCases
import com.example.schetodo.feature.use_case.GeneralUseCases
import com.example.schetodo.ui.navigation.schedule.EditScheduleTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditScheduleTemplateViewModel @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository,
    private val generalUseCases: GeneralUseCases,
    private val scheduleTemplateUseCases: ScheduleTemplateUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(EditScheduleTemplateState())
    val state: StateFlow<EditScheduleTemplateState>
        get() = _state.asStateFlow()

    val templateId: Int

    private lateinit var scheduleTemplateApplyDate: LocalDate

    init {
        templateId = savedStateHandle[EditScheduleTemplate.scheduleTemplateIdArg]
            ?: throw Exception("No schedule template id provided")

        viewModelScope.launch {
            scheduleBlockRepository.getScheduleBlocksOfScheduleTemplate(templateId).collect {
                val scheduleListItems = generalUseCases.convertScheduleBlocksToScheduleListItems(it)
                _state.value = _state.value.copy(scheduleItems = scheduleListItems)
            }
        }

        updateScheduleTemplateApplyDate(LocalDate.now())
    }

    fun onEvent(event: EditScheduleTemplateEvent) {
        when (event) {
            is DeleteScheduleTemplate -> deleteScheduleTemplate()
            is SelectScheduleTemplateApplyDate -> updateScheduleTemplateApplyDate(event.date)
            is ApplyScheduleTemplateToDate -> applyScheduleTemplateToDate()
        }
    }

    private fun applyScheduleTemplateToDate() {
        viewModelScope.launch {
            scheduleTemplateUseCases.applyScheduleTemplate(
                scheduleTemplateId = templateId,
                applyDate = scheduleTemplateApplyDate,
                applyScheduleConflictStrategy = ApplyScheduleConflictStrategy.REPLACE
            )
        }
    }

    private fun updateScheduleTemplateApplyDate(date: LocalDate) {
        scheduleTemplateApplyDate = date
        _state.value = _state.value.copy(
            scheduleTemplateApplyDate = generalUseCases.formatDate(date)
        )
    }

    private fun deleteScheduleTemplate() {
        viewModelScope.launch {
            scheduleTemplateUseCases.deleteScheduleTemplate(templateId)
        }
    }
}