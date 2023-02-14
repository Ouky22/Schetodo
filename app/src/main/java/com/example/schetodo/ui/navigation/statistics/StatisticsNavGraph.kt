package com.example.schetodo.ui.navigation.statistics

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.feature.statistics.StatisticsScreen
import com.example.schetodo.ui.navigation.Graph

fun NavGraphBuilder.statisticsNavGraph(navController: NavController) {
    navigation(
        startDestination = Statistics.route,
        route = Graph.STATISTICS
    ) {
        composable(route = Statistics.route) {
            StatisticsScreen()
        }
    }
}