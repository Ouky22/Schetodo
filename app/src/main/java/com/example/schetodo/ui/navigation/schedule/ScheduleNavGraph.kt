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
import com.example.schetodo.ui.feature.schedule.schedule_template.add_edit_schedule_template.AddEditScheduleTemplateScreen
import com.example.schetodo.ui.feature.schedule.schedule_template.add_edit_schedule_template.AddEditScheduleTemplateViewModel
import com.example.schetodo.ui.feature.schedule.schedule_template.list.ScheduleTemplatesScreen
import com.example.schetodo.ui.feature.schedule.schedule_template.list.ScheduleTemplatesViewModel
import com.example.schetodo.ui.navigation.Graph

fun NavGraphBuilder.scheduleNavGraph(
    schetodoAppState: SchetodoAppState
) {
    navigation(
        startDestination = Schedule.route,
        route = Graph.SCHEDULE
    ) {
        composable(route = Schedule.route) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                schetodoAppState.navController.getBackStackEntry(Graph.SCHEDULE)
            }

            val viewModel = hiltViewModel<ScheduleViewModel>(parentEntry)
            ScheduleScreen(
                viewModel = viewModel,
                onAddScheduleBlockNavigation = { dateStamp ->
                    schetodoAppState.navController.navigateToAddScheduleBlockScreen(dateStamp)
                },
                onEditScheduleBlockNavigation = { todoBlockId ->
                    schetodoAppState.navController.navigateToEditScheduleBlockScreen(todoBlockId)
                },
                onAddScheduleBlockInGapNavigation = { dateStamp, startTimeStamp, endTimeStamp ->
                    schetodoAppState.navController.navigateToAddScheduleBlockScreen(
                        dateStamp,
                        startTimeStamp,
                        endTimeStamp
                    )
                },
                onScheduleTemplatesScreenNavigation = {
                    schetodoAppState.navController.navigate(ScheduleTemplates.route)
                },
                schetodoAppState = schetodoAppState
            )
        }
        composable(
            route = AddScheduleBlock.routeWithArgs,
            arguments = AddScheduleBlock.args
        ) {
            val viewModel = hiltViewModel<AddEditScheduleBlockViewModel>()
            AddEditScheduleBlockScreen(
                viewModel = viewModel,
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
                schetodoAppState = schetodoAppState
            )
        }
        composable(route = TodoPicker.route) {
            val viewModel = hiltViewModel<TodoPickerViewModel>()
            TodoPickerScreen(viewModel = viewModel, navController = schetodoAppState.navController)
        }
        composable(route = TodoCategoryPicker.route) {
            val viewModel = hiltViewModel<TodoCategoryPickerViewModel>()
            TodoCategoryPickerScreen(
                viewModel = viewModel,
                navController = schetodoAppState.navController
            )
        }
        composable(route = ScheduleTemplates.route) {
            val viewModel = hiltViewModel<ScheduleTemplatesViewModel>()
            ScheduleTemplatesScreen(
                viewModel = viewModel,
                schetodoAppState = schetodoAppState,
                onEditScheduleTemplate = { templateId ->
                    schetodoAppState.navController.navigateToEditScheduleTemplateScreen(templateId)
                }
            )
        }
        composable(
            route = EditScheduleTemplate.routeWithArgs,
            arguments = EditScheduleTemplate.args
        ) {
            val viewModel = hiltViewModel<AddEditScheduleTemplateViewModel>()
            AddEditScheduleTemplateScreen(viewModel = viewModel)
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

fun NavHostController.navigateToEditScheduleTemplateScreen(scheduleTemplateId: Int) {
    navigate("${EditScheduleTemplate.route}/$scheduleTemplateId")
}