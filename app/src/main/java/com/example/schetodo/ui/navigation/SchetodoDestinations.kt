package com.example.schetodo.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.schetodo.R

sealed class SchetodoDestination(
    val icon: ImageVector,
    val route: String,
    @StringRes val titleResourceId: Int
) {
    object Schedule : SchetodoDestination(
        icon = Icons.Outlined.Schedule,
        route = "schedule",
        titleResourceId = R.string.schedule
    )

    object Todos : SchetodoDestination(
        icon = Icons.Outlined.Done,
        route = "todos",
        titleResourceId = R.string.todos
    )

    object Statistics : SchetodoDestination(
        icon = Icons.Outlined.PieChart,
        route = "statistics",
        titleResourceId = R.string.statistics
    )
}

val bottomNavDestinations = listOf(
    SchetodoDestination.Schedule, SchetodoDestination.Todos, SchetodoDestination.Statistics
)