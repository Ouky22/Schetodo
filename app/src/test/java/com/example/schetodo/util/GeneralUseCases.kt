package com.example.schetodo.util

import com.example.schetodo.feature.use_case.ConvertScheduleBlocksToScheduleListItemsUseCase
import com.example.schetodo.feature.use_case.FormatDateUseCase
import com.example.schetodo.feature.use_case.FormatTimeUseCase
import com.example.schetodo.feature.use_case.GeneralUseCases

val generalUseCases = GeneralUseCases(
    FormatDateUseCase(),
    FormatTimeUseCase(),
    ConvertScheduleBlocksToScheduleListItemsUseCase(FormatTimeUseCase())
)