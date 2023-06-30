package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker.todo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.schetodo.R
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.components.CategoryItem
import com.example.schetodo.ui.components.TodoItem
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker.PickerScreen
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme
import com.example.schetodo.ui.util.pushOntoPreviousBackStackEntry
import kotlinx.coroutines.launch


const val TODO_PICKER_RESULT = "todo_picker_result"


@Composable
fun TodoPickerScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoPickerViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.currentCategoryIsChildCategory)
        BackHandler { viewModel.navigateToPreviousCategory() }

    PickerScreen(
        modifier = modifier,
        topAppBarTitle = state.currentCategory?.name ?: stringResource(R.string.select_todo),
        showTopBarBackButton = state.showTopBarBackButton,
        onTopBarBackButtonClick = { viewModel.navigateToPreviousCategory() },
        selectedItemCount = state.selectedItems.count(),
        onAdd = {
            navController.pushOntoPreviousBackStackEntry(
                TODO_PICKER_RESULT,
                state.selectedItems.map { it.todoId }
            )
            navController.popBackStack()
        },
        onCancel = { navController.popBackStack() }
    ) {
        TodoPickerList(
            todoCategories = state.childCategories,
            todos = state.todos,
            selectedTodos = state.selectedItems,
            onMarkTodoForSelection = { viewModel.markItemForSelection(it) },
            onUndoMarkTodoForSelection = { viewModel.undoMarkItemForSelection(it) },
            onClickOnTodoCategory = { viewModel.navigateToTodoCategory(it.categoryId) }
        )
    }
}

@Composable
fun TodoPickerList(
    modifier: Modifier = Modifier,
    todos: List<Todo>,
    selectedTodos: List<Todo>,
    todoCategories: List<TodoCategory>,
    onMarkTodoForSelection: (Todo) -> Unit,
    onUndoMarkTodoForSelection: (Todo) -> Unit,
    onClickOnTodoCategory: (TodoCategory) -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(modifier = modifier, state = listState) {
        items(todoCategories) { todoCategory ->
            CategoryItem(
                todoCategoryName = todoCategory.name,
                todoCategoryColor = Color(todoCategory.color),
                todoCategoryIcon = getIconByName(todoCategory.iconName) ?: Icons.Filled.Category,
                modifier = Modifier
                    .height(125.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .clickable {
                        onClickOnTodoCategory(todoCategory)
                        coroutineScope.launch { listState.scrollToItem(0) }
                    }
            )
        }
        items(todos) { todo ->
            TodoItem(
                todo = todo,
                endSideContent = {
                    Checkbox(
                        checked = todo in selectedTodos,
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

    val categories = listOf(
        TodoCategory(
            2, "Study", todoCategoryColors[8].toArgb().toLong(),
            null, Icons.Filled.School.name
        ),
        TodoCategory(
            1, "Sport", todoCategoryColors[2].toArgb().toLong(),
            null, Icons.Filled.House.name
        )
    )

    SchetodoTheme {
        PickerScreen(
            topAppBarTitle = "Category Name",
            showTopBarBackButton = true,
            onTopBarBackButtonClick = {},
            selectedItemCount = 1,
            onAdd = {},
            onCancel = {}
        ) {
            TodoPickerList(
                todos = listOf(todo1, todo2),
                selectedTodos = listOf(todo2),
                todoCategories = categories,
                onMarkTodoForSelection = {},
                onUndoMarkTodoForSelection = {},
                onClickOnTodoCategory = {}
            )
        }
    }
}