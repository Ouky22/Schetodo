package com.example.schetodo.ui.feature.todos.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFilterSettings
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.ui.components.CategoryItem
import com.example.schetodo.ui.components.OverflowMenu
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.components.TodoItem
import com.example.schetodo.ui.feature.todos.add_edit_category.ID_OF_TODO_CATEGORY_MARKED_FOR_DELETION
import com.example.schetodo.ui.feature.todos.add_edit_todo.ID_OF_TODO_MARKED_FOR_DELETION
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.feature.todos.list.TodosEvent.*
import com.example.schetodo.ui.theme.SchetodoTheme
import com.example.schetodo.ui.util.popFromCurrentBackStackEntry
import com.example.schetodo.ui.util.showSnackbarWithActionHandler
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun TodosScreen(
    modifier: Modifier = Modifier,
    onCheckOffCompletedTodos: () -> Unit,
    onAddTodoCategory: (parentCategory: Int) -> Unit,
    onEditTodoCategory: (categoryToEdit: Int) -> Unit,
    onAddTodo: (parentCategory: Int) -> Unit,
    onEditTodo: (todoToEdit: Int) -> Unit,
    viewModel: TodosViewModel,
    schetodoAppSate: SchetodoAppState
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val state by viewModel.todosState.collectAsStateWithLifecycle()

    if (state.currentCategoryIsChildCategory)
        BackHandler(onBack = { viewModel.onEvent(NavigateToPreviousTodoCategory) })

    if (state.showAddCategoryOrTodoDialog)
        AddCategoryOrTodoDialog(
            onDismiss = { viewModel.onEvent(CloseAddCategoryOrTodoDialog) },
            onAddTodo = { viewModel.onEvent(NavigateToAddTodoScreen) },
            onAddTodoCategory = { viewModel.onEvent(NavigateToAddTodoCategoryScreen) },
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
        launch {
            schetodoAppSate.navController.popFromCurrentBackStackEntry<Int>(
                key = ID_OF_TODO_MARKED_FOR_DELETION,
                onPop = { todoId ->
                    snackbarHostState.showSnackbarWithActionHandler(
                        message = context.getString(R.string.deleted_todo),
                        actionLabel = context.getString(R.string.undo),
                        onActionPerformed = { viewModel.onEvent(UnmarkTodoForDeletion(todoId)) }
                    )
                }
            )
        }
        launch {
            schetodoAppSate.navController.popFromCurrentBackStackEntry<Int>(
                key = ID_OF_TODO_CATEGORY_MARKED_FOR_DELETION,
                onPop = { categoryId ->
                    snackbarHostState.showSnackbarWithActionHandler(
                        message = context.getString(R.string.deleted_todo_category),
                        actionLabel = context.getString(R.string.undo),
                        onActionPerformed = {
                            viewModel.onEvent(
                                UnmarkTodoCategoryForDeletion(categoryId)
                            )
                        }
                    )
                }
            )
        }
    }

    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = state.currentCategory?.name ?: stringResource(id = R.string.todos),
                showBackButton = state.currentCategoryIsChildCategory,
                onBackButtonClick = {
                    viewModel.onEvent(NavigateToPreviousTodoCategory)
                },
                actions = {
                    TodosFilterOverflowMenu(
                        todoFilterSettings = state.todoFilterSettings,
                        filterSettingsChanged = { viewModel.onEvent(ChangeTodoFilterSettings(it)) }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(ClickOnAddCategoryOrTodoButton) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_todo_category)
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TodoCategoryTodoList(
                modifier = Modifier.weight(1f),
                categories = state.childCategories,
                todos = state.todos,
                onClickOnTodoCategory = { todoCategory ->
                    viewModel.onEvent(NavigateToNewTodoCategory(todoCategory.categoryId))
                },
                onLongClickOnTodoCategory = { todoCategory -> onEditTodoCategory(todoCategory.categoryId) },
                onClickOnTodo = { todo -> onEditTodo(todo.todoId) }
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .padding(16.dp),
                onClick = onCheckOffCompletedTodos,
                enabled = state.checkOffTodosButtonActivated
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
fun TodosFilterOverflowMenu(
    modifier: Modifier = Modifier,
    todoFilterSettings: TodoFilterSettings,
    filterSettingsChanged: (TodoFilterSettings) -> Unit,
) {
    OverflowMenu(
        modifier = modifier,
        icon = Icons.Filled.FilterList,
        contentDescription = stringResource(R.string.filter_todos)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.filter_todos),
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(bottom = 10.dp)
            )
            Divider()
            FilterDropdownItem(
                text = stringResource(id = R.string.recurring),
                checked = todoFilterSettings.showRecurringTodos,
                onCheckChange = { checked ->
                    filterSettingsChanged(todoFilterSettings.copy(showRecurringTodos = checked))
                }
            )
            FilterDropdownItem(
                text = stringResource(id = R.string.undone),
                checked = todoFilterSettings.showUndoneTodos,
                onCheckChange = { checked ->
                    filterSettingsChanged(todoFilterSettings.copy(showUndoneTodos = checked))
                }
            )
            FilterDropdownItem(
                text = stringResource(id = R.string.todo_in_progress),
                checked = todoFilterSettings.showInProgressTodos,
                onCheckChange = { checked ->
                    filterSettingsChanged(todoFilterSettings.copy(showInProgressTodos = checked))
                }
            )
            FilterDropdownItem(
                text = stringResource(id = R.string.done),
                checked = todoFilterSettings.showDoneTodos,
                onCheckChange = { checked ->
                    filterSettingsChanged(todoFilterSettings.copy(showDoneTodos = checked))
                }
            )
        }
    }
}

@Composable
fun FilterDropdownItem(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier
                .clickable { onCheckChange(!checked) }
                .weight(1f)
        )
        Checkbox(checked = checked, onCheckedChange = onCheckChange)
    }
}

@ExperimentalFoundationApi
@Composable
fun TodoCategoryTodoList(
    modifier: Modifier = Modifier,
    categories: List<TodoCategory>,
    todos: List<Todo>,
    onClickOnTodoCategory: (TodoCategory) -> Unit,
    onLongClickOnTodoCategory: (TodoCategory) -> Unit,
    onClickOnTodo: (Todo) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(modifier = modifier, state = listState) {
        items(categories) { todoCategory ->
            CategoryItem(
                modifier = Modifier
                    .height(125.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .combinedClickable(
                        onClick = {
                            onClickOnTodoCategory(todoCategory)
                            coroutineScope.launch { listState.scrollToItem(0) }
                        },
                        onLongClick = { onLongClickOnTodoCategory(todoCategory) }
                    ),
                todoCategoryName = todoCategory.name,
                todoCategoryColor = Color(todoCategory.color),
                todoCategoryIcon = getIconByName(todoCategory.iconName) ?: Icons.Filled.Category
            )
        }
        items(todos) { todo ->
            TodoItem(
                modifier = Modifier
                    .height(125.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .clickable { onClickOnTodo(todo) },
                todo = todo
            )
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
                text = stringResource(R.string.category),
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