package com.example.schetodo.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.schetodo.ui.feature.todos.list.TodosScreen
import com.example.schetodo.ui.feature.todos.list.TodosViewModel
import com.example.schetodo.ui.feature.todos.add_edit_category.AddEditTodoCategoryScreen
import com.example.schetodo.ui.feature.todos.add_edit_category.AddEditTodoCategoryViewModel

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
        ) {
            val viewModel = hiltViewModel<AddEditTodoCategoryViewModel>()
            AddEditTodoCategoryScreen(
                viewModel = viewModel,
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = EditTodoCategory.routeWithArgs,
            arguments = EditTodoCategory.args
        ) {
            val viewModel = hiltViewModel<AddEditTodoCategoryViewModel>()
            AddEditTodoCategoryScreen(
                viewModel = viewModel,
                navController = navController,
                modifier = Modifier.fillMaxSize()
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