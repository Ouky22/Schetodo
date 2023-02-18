package com.example.schetodo.ui.navigation.schedule

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockScreen
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockViewModel
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker.TodoPickerScreen
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker.TodoPickerViewModel
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
                onAddScheduleBlockNavigation = { dateStamp ->
                    navController.navigateToAddScheduleBlockScreen(dateStamp)
                },
                onEditScheduleBlockNavigation = { todoBlockId ->
                    navController.navigateToEditScheduleBlockScreen(todoBlockId)
                }
            )
        }
        composable(
            route = AddScheduleBlock.routeWithArgs,
            arguments = AddScheduleBlock.args
        ) {
            val viewModel = hiltViewModel<AddEditScheduleBlockViewModel>()
            AddEditScheduleBlockScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(
            route = EditScheduleBlock.routeWithArgs,
            arguments = EditScheduleBlock.args
        ) {
            val viewModel = hiltViewModel<AddEditScheduleBlockViewModel>()
            AddEditScheduleBlockScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(route = TodoPicker.route) {
            val viewModel = hiltViewModel<TodoPickerViewModel>()
            TodoPickerScreen(viewModel = viewModel, navController = navController)
        }
    }
}

fun NavHostController.navigateToAddScheduleBlockScreen(dateStamp: Long) {
    navigate("${AddScheduleBlock.route}/$dateStamp")
}

fun NavHostController.navigateToEditScheduleBlockScreen(todoBlockId: Int) {
    navigate("${EditScheduleBlock.route}/$todoBlockId")
}