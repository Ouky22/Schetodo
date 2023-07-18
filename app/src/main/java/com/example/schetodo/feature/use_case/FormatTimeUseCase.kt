package com.example.schetodo.feature.use_case

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

class FormatTimeUseCase @Inject constructor() {
    operator fun invoke(time: LocalTime): String {
        val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        val timeString = formatter.format(time)

        val is12HourFormat = timeString.length > 5
        return if (is12HourFormat)
            timeString.padStart(8, '0')
        else
            timeString
    }
}