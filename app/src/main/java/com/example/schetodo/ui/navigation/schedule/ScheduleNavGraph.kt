package com.example.schetodo.ui.navigation.schedule

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockScreen
import com.example.schetodo.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockViewModel
import com.example.schetodo.feature.schedule.add_edit_schedule_block.picker.category.TodoCategoryPickerScreen
import com.example.schetodo.feature.schedule.add_edit_schedule_block.picker.category.TodoCategoryPickerViewModel
import com.example.schetodo.feature.schedule.add_edit_schedule_block.picker.todo.TodoPickerScreen
import com.example.schetodo.feature.schedule.add_edit_schedule_block.picker.todo.TodoPickerViewModel
import com.example.schetodo.feature.schedule.list.ScheduleScreen
import com.example.schetodo.feature.schedule.list.ScheduleViewModel
import com.example.schetodo.feature.schedule_template.edit_schedule_template.EditScheduleTemplateScreen
import com.example.schetodo.feature.schedule_template.edit_schedule_template.EditScheduleTemplateViewModel
import com.example.schetodo.feature.schedule_template.list.ScheduleTemplatesScreen
import com.example.schetodo.feature.schedule_template.list.ScheduleTemplatesViewModel
import com.example.schetodo.feature.settings.SettingsScreen
import com.example.schetodo.feature.settings.SettingsViewModel
import com.example.schetodo.ui.navigation.Graph
import com.example.schetodo.ui.navigation.settings.Settings

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
                onSettingsScreenNavigation = {
                    schetodoAppState.navController.navigate(Settings.route)
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
            route = AddScheduleBlockForTemplate.routeWithArgs,
            arguments = AddScheduleBlockForTemplate.args
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
            val viewModel = hiltViewModel<EditScheduleTemplateViewModel>()
            EditScheduleTemplateScreen(
                viewModel = viewModel,
                schetodoAppState = schetodoAppState,
                onEditScheduleBlockNavigation = { todoBlockId ->
                    schetodoAppState.navController.navigateToEditScheduleBlockScreen(todoBlockId)
                },
                onAddScheduleBlockNavigation = { templateId ->
                    schetodoAppState.navController.navigateToAddScheduleBlockForTemplateScreen(
                        templateId = templateId
                    )
                },
                onAddScheduleBlockInGapNavigation = { templateId, startTimeStamp, endTimeStamp ->
                    schetodoAppState.navController.navigateToAddScheduleBlockForTemplateScreen(
                        templateId = templateId,
                        startTimeStamp = startTimeStamp,
                        endTimeStamp = endTimeStamp
                    )
                }
            )
        }
        composable(route = Settings.route) {
            val viewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(
                viewModel = viewModel,
                schetodoAppState = schetodoAppState
            )
        }
    }
}

fun NavHostController.navigateToAddScheduleBlockForTemplateScreen(templateId: Int) {
    navigate("${AddScheduleBlockForTemplate.route}/$templateId")
}

fun NavHostController.navigateToAddScheduleBlockForTemplateScreen(
    templateId: Int,
    startTimeStamp: Int,
    endTimeStamp: Int
) {
    navigate(
        "${AddScheduleBlockForTemplate.route}/$templateId" +
                "?${AddScheduleBlockForTemplate.startTimeStampArg}=$startTimeStamp" +
                "&${AddScheduleBlockForTemplate.endTimeStampArg}=$endTimeStamp"
    )
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