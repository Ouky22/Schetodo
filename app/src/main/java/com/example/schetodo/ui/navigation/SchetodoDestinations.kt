package com.example.schetodo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
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

object AddTodo : SchetodoDestination {
    override val route = "add_todo"
    const val parentTodoCategoryIdArg = "parent_todo_category_id"

    val routeWithArgs = "$route/{$parentTodoCategoryIdArg}"

    val args = listOf(
        navArgument(parentTodoCategoryIdArg) {
            type = NavType.IntType
        }
    )
}

object EditTodo : SchetodoDestination {
    override val route = "edit_todo"
    const val todoId = "todo_id"

    val routeWithArgs = "$route/{$todoId}"

    val args = listOf(
        navArgument(todoId) {
            type = NavType.IntType
        }
    )
}

object AddTodoCategory : SchetodoDestination {
    override val route = "add_todo_category"
    const val parentTodoCategoryIdArg = "parent_todo_category_id"

    val routeWithArgs = "$route/{$parentTodoCategoryIdArg}"

    val args = listOf(
        navArgument(parentTodoCategoryIdArg) {
            type = NavType.IntType
        }
    )
}

object EditTodoCategory : SchetodoDestination {
    override val route = "edit_todo_category"
    const val todoCategoryIdArg = "todo_category_id"

    val routeWithArgs = "$route/{$todoCategoryIdArg}"

    val args = listOf(
        navArgument(todoCategoryIdArg) {
            type = NavType.IntType
        }
    )
}

val bottomNavDestinations = listOf(
    Schedule, Todos, Statistics
)