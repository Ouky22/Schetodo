package com.example.schetodo.feature.schedule_template.use_case

import javax.inject.Inject

data class ScheduleTemplateUseCases @Inject constructor(
    val applyScheduleTemplate: ApplyScheduleTemplateUseCase,
    val markScheduleTemplateForDeletion: MarkScheduleTemplateForDeletionUseCase,
    val unmarkScheduleTemplateForDeletion: UnmarkScheduleTemplateForDeletionUseCase
)
