package com.example.schetodo.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.feature.todos.TodosScreen
import com.example.schetodo.ui.feature.todos.TodosViewModel
import com.example.schetodo.ui.feature.todos.addedit.AddEditTodoCategoryScreen
import com.example.schetodo.ui.feature.todos.addedit.AddEditTodoCategoryViewModel

@ExperimentalFoundationApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
fun NavGraphBuilder.todosNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Todos.route,
        route = Graph.TODOS
    ) {
        composable(route = Todos.route) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Graph.TODOS)
            }
            val todosViewModel = hiltViewModel<TodosViewModel>(parentEntry)
            TodosScreen(
                viewModel = todosViewModel,
                onCheckOffCompletedTodos = {},
                onAddTodoCategory = { parentCategory ->
                    navController.navigateToAddTodoCategoryScreen(parentCategory)
                },
                onEditTodoCategory = { todoCategoryId ->
                    navController.navigateToEditTodoCategoryScreen(todoCategoryId)
                }
            )
        }
        composable(
            route = AddTodoCategory.routeWithArgs,
            arguments = AddTodoCategory.args
        ) { navBackStackEntry ->
            val parentTodoCategoryId = navBackStackEntry.arguments
                ?.getInt(AddTodoCategory.parentTodoCategoryIdArg) ?: -1

            val viewModel = hiltViewModel<AddEditTodoCategoryViewModel>()
            viewModel.setParentTodoCategoryForAdding(parentTodoCategoryId)
            AddEditTodoCategoryScreen(
                viewModel = viewModel,
                onCancelClicked = { navController.popBackStack() }
            )
        }
        composable(
            route = EditTodoCategory.routeWithArgs,
            arguments = EditTodoCategory.args
        ) { navBackStackEntry ->
            val todoCategoryId = navBackStackEntry.arguments
                ?.getInt(EditTodoCategory.todoCategoryIdArg) ?: return@composable

            val viewModel = hiltViewModel<AddEditTodoCategoryViewModel>()
            viewModel.setTodoCategoryForEditing(todoCategoryId)
            AddEditTodoCategoryScreen(
                viewModel = viewModel,
                onCancelClicked = { navController.popBackStack() }
            )
        }
    }
}

fun NavHostController.navigateToAddTodoCategoryScreen(parentTodoCategory: Int) {
    navigate("${AddTodoCategory.route}/$parentTodoCategory")
}

fun NavHostController.navigateToEditTodoCategoryScreen(todoCategoryId: Int) {
    navigate("${EditTodoCategory.route}/$todoCategoryId")
}