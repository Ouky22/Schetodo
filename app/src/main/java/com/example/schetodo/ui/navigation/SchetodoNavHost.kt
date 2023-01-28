package com.example.schetodo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.schetodo.ui.feature.schedule.ScheduleScreen
import com.example.schetodo.ui.feature.statistics.StatisticsScreen
import com.example.schetodo.ui.feature.todos.TodosScreen

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
            TodosScreen()
        }
        composable(route = SchetodoDestination.Statistics.route) {
            StatisticsScreen()
        }
    }
}