package com.example.schetodo.feature.schedule_template.use_case

import com.example.schetodo.data.schedule_template.ScheduleTemplateRepository
import com.example.schetodo.data.todo_block.TodoBlockRepository
import com.example.schetodo.di.CoroutineScopeModule.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MarkScheduleTemplateForDeletionUseCase @Inject constructor(
    private val templateRepository: ScheduleTemplateRepository,
    private val todoBlockRepository: TodoBlockRepository,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope
) {
    suspend operator fun invoke(templateId: Int) {
        applicationCoroutineScope.launch {
            todoBlockRepository.markTodoBlocksOfScheduleTemplateForDeletion(templateId)
            templateRepository.markForDeletion(templateId)
        }.join()
    }
}