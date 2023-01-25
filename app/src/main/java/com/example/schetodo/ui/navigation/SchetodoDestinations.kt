package com.example.schetodo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.ui.graphics.vector.ImageVector

interface SchetodoDestination {
    val icon: ImageVector
    val route: String
}

object Schedule : SchetodoDestination {
    override val icon = Icons.Outlined.Schedule
    override val route = "schedule"
}

object TodoCategory : SchetodoDestination {
    override val icon = Icons.Outlined.Done
    override val route = "todoCategory"
}

object Statistics : SchetodoDestination {
    override val icon = Icons.Outlined.PieChart
    override val route = "statistics"
}