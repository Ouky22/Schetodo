package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.todo_picker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.components.CategoryItem
import com.example.schetodo.ui.components.PositiveNegativeButtonRow
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.components.TodoItem
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme


@Composable
fun TodoPickerScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoPickerViewModel
) {
    val state by viewModel.todoPickerState.collectAsStateWithLifecycle()

    TodoPickerScreen(
        topAppBarTitle = state.currentCategory?.name ?: stringResource(R.string.select_todo),
        showTopBarBackButton = state.showTopBarBackButton,
        onTopBarBackButtonClick = { viewModel.onEvent(TodoPickerEvent.NavigateToPreviousTodoCategory) },
        todos = state.todos,
        todoCategories = state.childCategories,
        onMarkTodoForSelection = { viewModel.onEvent(TodoPickerEvent.MarkTodoForSelection(it)) },
        onUndoMarkTodoForSelection = { viewModel.onEvent(TodoPickerEvent.UndoMarkTodoForSelection(it)) },
        onClickOnTodoCategory = {
            viewModel.onEvent(TodoPickerEvent.NavigateToNewTodoCategory(it.categoryId))
        },
        onAdd = {},
        onCancel = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoPickerScreen(
    modifier: Modifier = Modifier,
    topAppBarTitle: String,
    showTopBarBackButton: Boolean,
    onTopBarBackButtonClick: () -> Unit,
    todos: List<TodoWithSelector>,
    todoCategories: List<TodoCategory>,
    onMarkTodoForSelection: (Todo) -> Unit,
    onUndoMarkTodoForSelection: (Todo) -> Unit,
    onClickOnTodoCategory: (TodoCategory) -> Unit,
    onAdd: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = topAppBarTitle,
                showBackButton = showTopBarBackButton,
                onBackButtonClick = onTopBarBackButtonClick
            )
        }
    ) { contentPadding ->
        Column(
            modifier = modifier.padding(contentPadding)
        ) {
            TodoPickerList(
                todoCategories = todoCategories,
                todos = todos,
                onMarkTodoForSelection = onMarkTodoForSelection,
                onUndoMarkTodoForSelection = onUndoMarkTodoForSelection,
                onClickOnTodoCategory = onClickOnTodoCategory,
                modifier = Modifier.weight(1f)
            )

            PositiveNegativeButtonRow(
                positiveButtonText = stringResource(id = R.string.add),
                negativeButtonText = stringResource(id = R.string.cancel),
                onPositiveClick = onAdd,
                onNegativeClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun TodoPickerList(
    modifier: Modifier = Modifier,
    todos: List<TodoWithSelector>,
    todoCategories: List<TodoCategory>,
    onMarkTodoForSelection: (Todo) -> Unit,
    onUndoMarkTodoForSelection: (Todo) -> Unit,
    onClickOnTodoCategory: (TodoCategory) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(todoCategories) { todoCategory ->
            CategoryItem(
                todoCategoryName = todoCategory.name,
                todoCategoryColor = Color(todoCategory.color),
                todoCategoryIcon = getIconByName(todoCategory.iconName) ?: Icons.Filled.Category,
                modifier = Modifier
                    .height(125.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .clickable { onClickOnTodoCategory(todoCategory) }
            )
        }
        items(todos) { todoWithSelector ->
            val todo = todoWithSelector.todo
            TodoItem(
                todo = todo,
                endSideContent = {
                    Checkbox(
                        checked = todoWithSelector.selected,
                        onCheckedChange = { selected ->
                            if (selected) onMarkTodoForSelection(todo)
                            else onUndoMarkTodoForSelection(todo)
                        }
                    )
                },
                modifier = Modifier
                    .height(125.dp)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                alignEndSideContentToEnd = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoPickerScreenPreview() {
    val todo1 = Todo(
        1,
        "Lorem ipsum dolor sit at,  voluptua. A Lorem ipsum dolor sit at,  voluptua. At vero eos et et justo duo",
        TodoPriority.LOW, TodoFlag.DONE, 1
    )
    val todo2 = Todo(1, "Lorem ipsum dolor", TodoPriority.HIGH, TodoFlag.UNDONE, 1)

    val todosWithSelectors = listOf(
        TodoWithSelector(todo1, false),
        TodoWithSelector(todo2, true),
        TodoWithSelector(todo1, false),
        TodoWithSelector(todo2, true)
    )
    val categories = listOf(
        TodoCategory(
            2, "Study", todoCategoryColors[8].toArgb().toLong(),
            null, Icons.Filled.School.name
        ),
        TodoCategory(
            1, "Sport", todoCategoryColors[2].toArgb().toLong(),
            null, Icons.Filled.House.name
        ),
        TodoCategory(
            3, "Something", todoCategoryColors[10].toArgb().toLong(),
            null, Icons.Filled.School.name
        )
    )

    SchetodoTheme {
        TodoPickerScreen(
            topAppBarTitle = "Category Name",
            showTopBarBackButton = true,
            onTopBarBackButtonClick = {},
            todos = todosWithSelectors,
            todoCategories = categories,
            onMarkTodoForSelection = {},
            onUndoMarkTodoForSelection = {},
            onClickOnTodoCategory = {},
            onAdd = {},
            onCancel = {}
        )
    }
}