package com.example.schetodo.ui.navigation.statistics

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.feature.statistics.StatisticsScreen
import com.example.schetodo.ui.navigation.Graph

fun NavGraphBuilder.statisticsNavGraph(schetodoAppState: SchetodoAppState) {
    navigation(
        startDestination = Statistics.route,
        route = Graph.STATISTICS
    ) {
        composable(route = Statistics.route) {
            StatisticsScreen()
        }
    }
}