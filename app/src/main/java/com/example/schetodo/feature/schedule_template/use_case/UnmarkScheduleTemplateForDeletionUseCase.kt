package com.example.schetodo.feature.schedule_template.use_case

import com.example.schetodo.data.schedule_template.ScheduleTemplateRepository
import com.example.schetodo.data.todo_block.TodoBlockRepository
import javax.inject.Inject

class UnmarkScheduleTemplateForDeletionUseCase @Inject constructor(
    private val templateRepository: ScheduleTemplateRepository,
    private val todoBlockRepository: TodoBlockRepository
) {
    suspend operator fun invoke(templateId: Int) {
        todoBlockRepository.unmarkTodoBlocksOfScheduleTemplateForDeletion(templateId)
        templateRepository.unmarkForDeletion(templateId)
    }
}