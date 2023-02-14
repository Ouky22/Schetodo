package com.example.schetodo.ui.navigation.schedule

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.feature.schedule.ScheduleScreen
import com.example.schetodo.ui.navigation.Graph

fun NavGraphBuilder.scheduleNavGraph(navController: NavController) {
    navigation(
        startDestination = Schedule.route,
        route = Graph.SCHEDULE
    ) {
        composable(route = Schedule.route) {
            ScheduleScreen()
        }
    }
}