package com.example.schetodo.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.schetodo.ui.navigation.schedule.Schedule
import com.example.schetodo.ui.navigation.statistics.Statistics
import com.example.schetodo.ui.navigation.todos.Todos

interface SchetodoDestination {
    val route: String
}

interface MainSchetodoDestination : SchetodoDestination {
    val icon: ImageVector
    val titleResourceId: Int
}

val bottomNavDestinations = listOf(
    Schedule, Todos, Statistics
)