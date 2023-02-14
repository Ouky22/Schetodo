package com.example.schetodo.ui.navigation.todos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.schetodo.R
import com.example.schetodo.ui.navigation.MainSchetodoDestination
import com.example.schetodo.ui.navigation.SchetodoDestination


object Todos : MainSchetodoDestination {
    override val icon = Icons.Outlined.TaskAlt
    override val route = "todos"
    override val titleResourceId = R.string.todos
}

object CheckOffTodos : SchetodoDestination {
    override val route = "check_off_todos"
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
