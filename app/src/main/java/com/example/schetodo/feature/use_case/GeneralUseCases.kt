package com.example.schetodo.feature.use_case

import javax.inject.Inject

data class GeneralUseCases @Inject constructor(
    val formatDate: FormatDateUseCase,
    val formatTime: FormatTimeUseCase,
    val convertScheduleBlocksToScheduleListItems: ConvertScheduleBlocksToScheduleListItemsUseCase
)