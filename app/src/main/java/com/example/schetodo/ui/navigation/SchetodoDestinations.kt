package com.example.schetodo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.schetodo.R

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

object Todos : MainSchetodoDestination {
    override val icon = Icons.Outlined.TaskAlt
    override val route = "todos"
    override val titleResourceId = R.string.todos
}

object Statistics : MainSchetodoDestination {
    override val icon = Icons.Outlined.PieChart
    override val route = "statistics"
    override val titleResourceId = R.string.statistics
}

object AddEditTodoCategory : SchetodoDestination {
    override val route = "add_edit_todo_category"
}

val bottomNavDestinations = listOf(
    Schedule, Todos, Statistics
)