package com.example.schetodo.ui.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import java.time.LocalDate
import java.time.LocalTime


fun showTimePicker(
    context: Context,
    startHourOfDay: Int = (LocalTime.now().hour + 1).coerceAtMost(23),
    startMinute: Int = 0,
    onTimeSetListener: (LocalTime) -> Unit
) {
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val localTime = LocalTime.of(hourOfDay, minute)
            onTimeSetListener(localTime)
        },
        startHourOfDay,
        startMinute,
        DateFormat.is24HourFormat(context)
    ).show()
}

fun showDatePicker(
    context: Context,
    startYear: Int = LocalDate.now().year,
    startMonth: Int = LocalDate.now().month.value - 1,
    startDayOfMonth: Int = LocalDate.now().dayOfMonth,
    onDateSetListener: (LocalDate) -> Unit
) {
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val localDate = LocalDate.of(year, month + 1, dayOfMonth)
            onDateSetListener(localDate)
        },
        startYear,
        startMonth,
        startDayOfMonth
    ).show()
}