package com.example.schetodo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.schetodo.ui.schedule.ScheduleScreen
import com.example.schetodo.ui.statistics.StatisticsScreen
import com.example.schetodo.ui.todocategory.TodoCategoryScreen

@Composable
fun SchetodoNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = SchetodoDestination.Schedule.route,
        modifier = modifier
    ) {
        composable(route = SchetodoDestination.Schedule.route) {
            ScheduleScreen()
        }
        composable(route = SchetodoDestination.Todos.route) {
            TodoCategoryScreen()
        }
        composable(route = SchetodoDestination.Statistics.route) {
            StatisticsScreen()
        }
    }
}