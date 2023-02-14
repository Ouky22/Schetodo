package com.example.schetodo.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.schetodo.ui.feature.schedule.ScheduleScreen
import com.example.schetodo.ui.feature.statistics.StatisticsScreen
import com.example.schetodo.ui.navigation.todos.todosNavGraph


@ExperimentalFoundationApi
@Composable
@ExperimentalMaterial3Api
fun SchetodoNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Schedule.route,
        modifier = modifier
    ) {
        composable(route = Schedule.route) {
            ScheduleScreen()
        }
        todosNavGraph(navController)
        composable(route = Statistics.route) {
            StatisticsScreen()
        }
    }
}

object Graph {
    const val TODOS = "todos_graph"
}