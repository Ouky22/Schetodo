package com.example.schetodo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.schetodo.R
import com.example.schetodo.ui.navigation.todos.Todos

interface SchetodoDestination {
    val route: String
}

interface MainSchetodoDestination : SchetodoDestination {
    val icon: ImageVector
    val titleResourceId: Int
}

object Schedule : MainSchetodoDestination {
    override val icon = Icons.Outlined.Schedule
    override val route = "schedule"
    override val titleResourceId = R.string.schedule
}

object Statistics : MainSchetodoDestination {
    override val icon = Icons.Outlined.PieChart
    override val route = "statistics"
    override val titleResourceId = R.string.statistics
}

val bottomNavDestinations = listOf(
    Schedule, Todos, Statistics
)