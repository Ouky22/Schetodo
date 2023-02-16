package com.example.schetodo.ui.util

import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import java.time.LocalTime


fun showTimePicker(
    context: Context,
    startHourOfDay: Int = 0,
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