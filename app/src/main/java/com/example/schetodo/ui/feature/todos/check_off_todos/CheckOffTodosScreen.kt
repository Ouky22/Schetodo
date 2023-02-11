package com.example.schetodo.ui.feature.todos.check_off_todos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schetodo.R
import com.example.schetodo.data.entity.Todo
import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.feature.todos.list.CategoryItem
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme


@ExperimentalMaterial3Api
@Composable
fun CheckOffTodosScreen(
    modifier: Modifier = Modifier
) {
    CheckOffTodosScreen(
        todos = emptyList(),
        modifier = modifier
    )
}


@ExperimentalMaterial3Api
@Composable
fun CheckOffTodosScreen(
    modifier: Modifier = Modifier,
    todos: List<Todo>
) {
    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = stringResource(R.string.check_off_done_todos),
                showBackButton = true,
                onBackButtonClick = { /*TODO*/ }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = modifier.padding(contentPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = modifier.weight(1f)) {
                items(todos) { todo ->
                    CheckOffTodoItem(
                        modifier = Modifier
                            .height(175.dp)
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        todoDescription = todo.description,
                        parentTodoCategoryName = "Household",
                        parentTodoCategoryIcon = Icons.Filled.House,
                        parentTodoCategoryColor = todoCategoryColors[0]
                    )
                }
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 16.dp)
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
fun CheckOffTodoItem(
    modifier: Modifier = Modifier,
    todoDescription: String,
    parentTodoCategoryName: String,
    parentTodoCategoryIcon: ImageVector,
    parentTodoCategoryColor: Color
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
                    .width(1.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Checkbox(
                    checked = false,
                    onCheckedChange = {}
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

    SchetodoTheme {
        CheckOffTodosScreen(
            modifier = Modifier.fillMaxSize(),
            todos = testTodos
        )
    }
}