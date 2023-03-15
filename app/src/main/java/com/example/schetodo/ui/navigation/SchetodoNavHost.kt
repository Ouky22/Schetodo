package com.example.schetodo.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.ui.navigation.schedule.scheduleNavGraph
import com.example.schetodo.ui.navigation.statistics.statisticsNavGraph
import com.example.schetodo.ui.navigation.todos.todosNavGraph


@ExperimentalFoundationApi
@Composable
@ExperimentalMaterial3Api
fun SchetodoNavHost(
    schetodoAppState: SchetodoAppState,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Graph.SCHEDULE,
        modifier = modifier
    ) {
        scheduleNavGraph(schetodoAppState)
        todosNavGraph(schetodoAppState)
        statisticsNavGraph(schetodoAppState)
    }
}

object Graph {
    const val TODOS = "todos_graph"
    const val SCHEDULE = "schedule_graph"
    const val STATISTICS = "statistics_graph"
}