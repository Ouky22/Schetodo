package com.example.schetodo.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.feature.schedule.ScheduleScreen
import com.example.schetodo.ui.feature.statistics.StatisticsScreen
import com.example.schetodo.ui.feature.todos.TodosScreen
import com.example.schetodo.ui.feature.todos.TodosViewModel


@Composable
@ExperimentalLifecycleComposeApi
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

@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
fun NavGraphBuilder.todosNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Todos.route,
        route = Graph.TODOS
    ) {
        composable(route = Todos.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Graph.TODOS)
            }
            val todosViewModel = hiltViewModel<TodosViewModel>(parentEntry)
            TodosScreen(viewModel = todosViewModel)
        }
    }
}

object Graph {
    const val TODOS = "todos_graph"
}