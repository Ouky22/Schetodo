package com.example.schetodo.ui

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberSchetodoAppState(context: Context): SchetodoAppState {
    return remember(context) {
        SchetodoAppState(context)
    }
}

class SchetodoAppState(private val context: Context) {
    fun allowedToScheduleExactAlarms(): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
    }
}