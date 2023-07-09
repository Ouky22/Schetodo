package com.example.schetodo.feature.use_case

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

class FormatTimeUseCase @Inject constructor() {
    operator fun invoke(time: LocalTime): String {
        val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        return formatter.format(time)
    }
}