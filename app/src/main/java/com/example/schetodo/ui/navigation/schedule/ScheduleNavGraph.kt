package com.example.schetodo.ui.navigation.schedule

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockScreen
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockViewModel
import com.example.schetodo.ui.feature.schedule.list.ScheduleScreen
import com.example.schetodo.ui.feature.schedule.list.ScheduleViewModel
import com.example.schetodo.ui.navigation.Graph

fun NavGraphBuilder.scheduleNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Schedule.route,
        route = Graph.SCHEDULE
    ) {
        composable(route = Schedule.route) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Graph.SCHEDULE)
            }

            val viewModel = hiltViewModel<ScheduleViewModel>(parentEntry)
            ScheduleScreen(
                viewModel = viewModel,
                onAddScheduleBlockNavigation = { dateTimeStamp ->
                    navController.navigateToAddScheduleBlockScreen(dateTimeStamp)
                }
            )
        }
        composable(
            route = AddScheduleBlock.routeWithArgs,
            arguments = AddScheduleBlock.args
        ) {
            val viewModel = hiltViewModel<AddEditScheduleBlockViewModel>()
            AddEditScheduleBlockScreen(
                viewModel = viewModel
            )
        }
    }
}

fun NavHostController.navigateToAddScheduleBlockScreen(dateTimeStamp: Long) {
    navigate("${AddScheduleBlock.route}/$dateTimeStamp")
}