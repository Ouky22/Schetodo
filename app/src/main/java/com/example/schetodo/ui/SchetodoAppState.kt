package com.example.schetodo.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.schetodo.ui.navigation.MainSchetodoDestination
import com.example.schetodo.ui.navigation.bottomNavDestinations

@Composable
fun rememberSchetodoAppState(
    context: Context = LocalContext.current,
    navController: NavHostController = rememberNavController(),
): SchetodoAppState {
    return remember(context) {
        SchetodoAppState(context, navController)
    }
}

class SchetodoAppState(
    private val context: Context,
    val navController: NavHostController,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val allowedToScheduleExactAlarms: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    val allowedToShowNotifications: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

    fun getCurrentMainDestination(currentNavBackStackEntry: NavBackStackEntry?): MainSchetodoDestination? =
        bottomNavDestinations.find { schetodoDestination ->
            currentNavBackStackEntry?.destination?.route == schetodoDestination.route
        }

    fun shouldShowBottomNavigation(currentNavBackStackEntry: NavBackStackEntry?): Boolean =
        getCurrentMainDestination(currentNavBackStackEntry)?.route in bottomNavDestinations.map { it.route }
}