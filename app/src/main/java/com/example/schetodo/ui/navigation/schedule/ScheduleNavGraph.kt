package com.example.schetodo.ui.navigation.schedule

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.feature.schedule.list.ScheduleScreen
import com.example.schetodo.ui.feature.schedule.list.ScheduleViewModel
import com.example.schetodo.ui.navigation.Graph

fun NavGraphBuilder.scheduleNavGraph(navController: NavController) {
    navigation(
        startDestination = Schedule.route,
        route = Graph.SCHEDULE
    ) {
        composable(route = Schedule.route) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Graph.SCHEDULE)
            }

            val viewModel = hiltViewModel<ScheduleViewModel>(parentEntry)
            ScheduleScreen(viewModel = viewModel)
        }
    }
}