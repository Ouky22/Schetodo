package com.example.schetodo.feature.use_case

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class FormatDateUseCase @Inject constructor(){
    operator fun invoke(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("EEE dd LLL, yyyy", Locale.getDefault())
        return formatter.format(date)
    }
}