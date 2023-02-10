package com.example.schetodo.ui.feature.todos.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.theme.SchetodoTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@ExperimentalLifecycleComposeApi
@Composable
fun TodosScreen(
    modifier: Modifier = Modifier,
    onCheckOffCompletedTodos: () -> Unit,
    onAddTodoCategory: (parentCategory: Int) -> Unit,
    onEditTodoCategory: (categoryToEdit: Int) -> Unit,
    onAddTodo: (parentCategory: Int) -> Unit,
    onEditTodo: (todoToEdit: Int) -> Unit,
    viewModel: TodosViewModel
) {
    val state by viewModel.todosState.collectAsStateWithLifecycle()

    if (state.currentCategoryIsChildCategory)
        BackHandler(onBack = { viewModel.onEvent(TodosEvent.NavigateToPreviousTodoCategory) })

    if (state.showAddCategoryOrTodoDialog)
        AddCategoryOrTodoDialog(
            onDismiss = { viewModel.onEvent(TodosEvent.CloseAddCategoryOrTodoDialog) },
            onAddTodo = { viewModel.onEvent(TodosEvent.NavigateToAddTodoScreen) },
            onAddTodoCategory = { viewModel.onEvent(TodosEvent.NavigateToAddTodoCategoryScreen) },
            modifier = Modifier.fillMaxHeight(0.6f)
        )

    // do not execute the navigation handler right after addTodo/addTodoCategory click, so that
    // the AddCategoryOrTodoDialog first closes and then the navigation happens
    LaunchedEffect(key1 = true) {
        launch {
            viewModel.navigateToAddTodoScreen.collect { navigate ->
                if (navigate) onAddTodo(state.currentCategory?.categoryId ?: -1)
            }
        }
        launch {
            viewModel.navigateToAddTodoCategoryScreen.collect { navigate ->
                if (navigate) onAddTodoCategory(state.currentCategory?.categoryId ?: -1)
            }
        }
    }

    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = state.currentCategory?.name ?: stringResource(id = R.string.todos),
                showBackButton = state.currentCategoryIsChildCategory,
                onBackButtonClick = {
                    viewModel.onEvent(TodosEvent.NavigateToPreviousTodoCategory)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(TodosEvent.ShowAddCategoryOrTodoDialog) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_todo_category)
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.childCategories) { todoCategory ->
                    CategoryItem(
                        modifier = Modifier
                            .height(125.dp)
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .combinedClickable(
                                onClick = {
                                    viewModel.onEvent(
                                        TodosEvent.NavigateToNewTodoCategory(todoCategory.categoryId)
                                    )
                                },
                                onLongClick = {
                                    onEditTodoCategory(todoCategory.categoryId)
                                }
                            ),
                        todoCategoryName = todoCategory.name,
                        todoCategoryColor = Color(todoCategory.color),
                        todoCategoryIcon = getIconByName(todoCategory.iconName)
                            ?: Icons.Filled.Category
                    )
                }
                items(state.todos) { todo ->
                    TodoItem(
                        modifier = Modifier
                            .height(125.dp)
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        todo = todo
                    )
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .padding(16.dp),
                onClick = onCheckOffCompletedTodos,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Checklist,
                    contentDescription = null,
                    Modifier.padding(end = 10.dp)
                )
                Text(text = stringResource(id = R.string.check_off_todos))
            }
        }
    }
}

@Composable
fun AddCategoryOrTodoDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAddTodo: () -> Unit,
    onAddTodoCategory: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            AddCategoryOrTodoDialogButton(
                onClick = onAddTodoCategory,
                icon = Icons.Filled.Category,
                text = stringResource(R.string.todo_category),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(80.dp))
            AddCategoryOrTodoDialogButton(
                onClick = onAddTodo,
                icon = Icons.Filled.TaskAlt,
                text = stringResource(R.string.todo),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AddCategoryOrTodoDialogButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .weight(1f)
                    .padding(8.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddCategoryOrTodoDialogPreview() {
    SchetodoTheme {
        AddCategoryOrTodoDialog(
            onDismiss = { },
            onAddTodo = { },
            onAddTodoCategory = {},
            modifier = Modifier.height(400.dp)
        )
    }
}