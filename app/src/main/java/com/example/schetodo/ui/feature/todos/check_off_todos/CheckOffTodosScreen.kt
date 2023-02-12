package com.example.schetodo.ui.feature.todos.check_off_todos

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.schetodo.R
import com.example.schetodo.data.entity.Todo
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.feature.todos.list.CategoryItem
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme


@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
@Composable
fun CheckOffTodosScreen(
    viewModel: CheckOffTodosViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val todosInProgress by viewModel.todosInProgress.collectAsStateWithLifecycle()

    CheckOffTodosScreen(
        todoCategoryTodoPairs = todosInProgress,
        modifier = modifier,
        onMarkTodoForCheckOff = { viewModel.onEvent(CheckOffTodosEvent.MarkTodoForCheckOff(it)) },
        onUndoMarkTodoForCheckOff = {
            viewModel.onEvent(CheckOffTodosEvent.UndoMarkTodoForCheckOff(it))
        },
        onCheckOffTodo = { viewModel.onEvent(CheckOffTodosEvent.CheckOffTodo(it)) },
        onCheckOffTodos = { viewModel.onEvent(CheckOffTodosEvent.CheckOffMarkedTodos) },
        onMarkTodoAsUndone = { viewModel.onEvent(CheckOffTodosEvent.MarkTodoAsUndone(it)) },
        onBackButtonClick = { navController.popBackStack() }
    )
}


@ExperimentalMaterial3Api
@Composable
fun CheckOffTodosScreen(
    modifier: Modifier = Modifier,
    todoCategoryTodoPairs: List<TodoCategoryTodoPair>,
    onMarkTodoForCheckOff: (todoId: Int) -> Unit,
    onUndoMarkTodoForCheckOff: (todoId: Int) -> Unit,
    onCheckOffTodo: (todoId: Int) -> Unit,
    onCheckOffTodos: () -> Unit,
    onMarkTodoAsUndone: (todoId: Int) -> Unit,
    onBackButtonClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = stringResource(R.string.check_off_done_todos),
                showBackButton = true,
                onBackButtonClick = onBackButtonClick
            )
        }
    ) { contentPadding ->
        if (todoCategoryTodoPairs.isEmpty()) {
            NoTodosInProgress(modifier = modifier.fillMaxSize())
        } else {
            Column(
                modifier = modifier.padding(contentPadding),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(modifier = modifier.weight(1f)) {
                    items(
                        items = todoCategoryTodoPairs,
                        key = { it.todo.todoId }
                    ) { todoCategoryTodoPair ->
                        SwipeableCheckOffTodoItemContainer(
                            todoCategoryTodoPair = todoCategoryTodoPair,
                            onCheckOffTodo = onCheckOffTodo,
                            onMarkTodoAsUndone = onMarkTodoAsUndone
                        ) {
                            CheckOffTodoItem(
                                modifier = Modifier
                                    .height(175.dp)
                                    .padding(vertical = 8.dp, horizontal = 16.dp),
                                todoDescription = todoCategoryTodoPair.todo.description,
                                parentTodoCategoryName = todoCategoryTodoPair.todoCategory.name,
                                parentTodoCategoryIcon = getIconByName(todoCategoryTodoPair.todoCategory.iconName)
                                    ?: Icons.Filled.Category,
                                parentTodoCategoryColor = Color(todoCategoryTodoPair.todoCategory.color),
                                checkedOff = todoCategoryTodoPair.checkedOff,
                                onCheck = { checkedOff ->
                                    val todoId = todoCategoryTodoPair.todo.todoId
                                    if (checkedOff) onMarkTodoForCheckOff(todoId)
                                    else onUndoMarkTodoForCheckOff(todoId)
                                }
                            )
                        }
                    }
                }
                Button(
                    onClick = onCheckOffTodos,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 16.dp)
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableCheckOffTodoItemContainer(
    modifier: Modifier = Modifier,
    onCheckOffTodo: (todoId: Int) -> Unit,
    onMarkTodoAsUndone: (todoId: Int) -> Unit,
    todoCategoryTodoPair: TodoCategoryTodoPair,
    content: @Composable (RowScope.() -> Unit)
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd)
                onCheckOffTodo(todoCategoryTodoPair.todo.todoId)
            else if (it == DismissValue.DismissedToStart)
                onMarkTodoAsUndone(todoCategoryTodoPair.todo.todoId)
            it != DismissValue.DismissedToEnd
        }
    )

    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val direction =
                dismissState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> MaterialTheme.colorScheme.onBackground
                    DismissValue.DismissedToEnd -> Color(0xFF90A17D)
                    DismissValue.DismissedToStart -> Color(0xFFE97777)
                }
            )
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Filled.Done
                DismissDirection.EndToStart -> Icons.Filled.RemoveDone
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.scale(scale)
                )
            }
        },
        dismissContent = content
    )
}

@Composable
fun NoTodosInProgress(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.RemoveDone,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = "No todos in progress")
    }
}


@Composable
fun CheckOffTodoItem(
    modifier: Modifier = Modifier,
    todoDescription: String,
    parentTodoCategoryName: String,
    parentTodoCategoryIcon: ImageVector,
    parentTodoCategoryColor: Color,
    checkedOff: Boolean,
    onCheck: (checkedOff: Boolean) -> Unit
) {
    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(5f)
                    .padding(8.dp)
            ) {
                CategoryItem(
                    modifier = Modifier.weight(1f),
                    todoCategoryName = parentTodoCategoryName,
                    todoCategoryColor = parentTodoCategoryColor,
                    todoCategoryIcon = parentTodoCategoryIcon
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = todoDescription,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Checkbox(
                    checked = checkedOff,
                    onCheckedChange = onCheck
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun CheckOffTodosScreenPreview() {
    val longText =
        "test test test test test test test test test test test test test test test test test test test test test test test test test test test test "
    val testTodos = listOf(
        Todo(1, longText, TodoPriority.LOW, TodoFlag.DONE, 1),
        Todo(1, "test 2", TodoPriority.LOW, TodoFlag.DONE, 1),
        Todo(1, "test 3", TodoPriority.LOW, TodoFlag.DONE, 1)
    )
    val todoCategoryTodoPairs = testTodos.map { todo ->
        val category = TodoCategory(
            1, "Household", todoCategoryColors[1].toArgb().toLong(), null, Icons.Filled.House.name
        )
        TodoCategoryTodoPair(todo, category)
    }

    SchetodoTheme {
        CheckOffTodosScreen(
            modifier = Modifier.fillMaxSize(),
            todoCategoryTodoPairs = todoCategoryTodoPairs,
            onCheckOffTodos = {},
            onUndoMarkTodoForCheckOff = {},
            onMarkTodoForCheckOff = {},
            onCheckOffTodo = {},
            onMarkTodoAsUndone = {},
            onBackButtonClick = {}
        )
    }
}


@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun CheckOffTodosScreenWhenNoTodosInProgressPreview() {

    SchetodoTheme {
        CheckOffTodosScreen(
            modifier = Modifier.fillMaxSize(),
            todoCategoryTodoPairs = emptyList(),
            onCheckOffTodos = {},
            onUndoMarkTodoForCheckOff = {},
            onMarkTodoForCheckOff = {},
            onCheckOffTodo = {},
            onMarkTodoAsUndone = {},
            onBackButtonClick = {}
        )
    }
}