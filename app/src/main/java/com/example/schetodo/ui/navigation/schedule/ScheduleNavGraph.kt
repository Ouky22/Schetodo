package com.example.schetodo.ui.navigation.schedule

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockScreen
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockViewModel
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker.category.TodoCategoryPickerScreen
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker.category.TodoCategoryPickerViewModel
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker.todo.TodoPickerScreen
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker.todo.TodoPickerViewModel
import com.example.schetodo.ui.feature.schedule.list.ScheduleScreen
import com.example.schetodo.ui.feature.schedule.list.ScheduleViewModel
import com.example.schetodo.ui.navigation.Graph

fun NavGraphBuilder.scheduleNavGraph(
    schetodoAppState: SchetodoAppState,
    navController: NavHostController
) {
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
                },
                onAddScheduleBlockInGapNavigation = { dateStamp, startTimeStamp, endTimeStamp ->
                    navController.navigateToAddScheduleBlockScreen(
                        dateStamp,
                        startTimeStamp,
                        endTimeStamp
                    )
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
                navController = navController,
                schetodoAppState = schetodoAppState
            )
        }
        composable(
            route = EditScheduleBlock.routeWithArgs,
            arguments = EditScheduleBlock.args
        ) {
            val viewModel = hiltViewModel<AddEditScheduleBlockViewModel>()
            AddEditScheduleBlockScreen(
                viewModel = viewModel,
                navController = navController,
                schetodoAppState = schetodoAppState
            )
        }
        composable(route = TodoPicker.route) {
            val viewModel = hiltViewModel<TodoPickerViewModel>()
            TodoPickerScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = TodoCategoryPicker.route) {
            val viewModel = hiltViewModel<TodoCategoryPickerViewModel>()
            TodoCategoryPickerScreen(viewModel = viewModel, navController = navController)
        }
    }
}

fun NavHostController.navigateToAddScheduleBlockScreen(
    dateStamp: Long,
    startTimeStamp: Int,
    endTimeStamp: Int
) {
    navigate(
        "${AddScheduleBlock.route}/$dateStamp" +
                "?${AddScheduleBlock.startTimeStampArg}=$startTimeStamp" +
                "&${AddScheduleBlock.endTimeStampArg}=$endTimeStamp"
    )
}

fun NavHostController.navigateToAddScheduleBlockScreen(dateStamp: Long) {
    navigate("${AddScheduleBlock.route}/$dateStamp")
}

fun NavHostController.navigateToEditScheduleBlockScreen(todoBlockId: Int) {
    navigate("${EditScheduleBlock.route}/$todoBlockId")
}