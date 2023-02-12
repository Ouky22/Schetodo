package com.example.schetodo.ui.feature.todos.add_edit_todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.House
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.schetodo.R
import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.ui.components.PositiveNegativeButtonRow
import com.example.schetodo.ui.feature.todos.components.AddEditTopBar
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.feature.todos.getTodoPriorityColorOf
import com.example.schetodo.ui.feature.todos.list.CategoryItem
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
@ExperimentalMaterial3Api
fun AddEditTodoScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditTodoViewModel,
    navController: NavController
) {
    val state = viewModel.addEditTodoState.value
    val keyBoardController = LocalFocusManager.current

    LaunchedEffect(key1 = true) {
        viewModel.closeAddEditTodoScreen.collect { closeScreen ->
            if (closeScreen) {
                keyBoardController.clearFocus()
                navController.popBackStack()
            }
        }
    }

    AddEditTodoScreen(
        todoCategoryName = state.parentTodoCategoryName,
        todoCategoryIcon = getIconByName(state.parentTodoCategoryName) ?: Icons.Filled.Category,
        todoCategoryColor = Color(state.parentTodoCategoryColor),
        todoDescription = state.todoDescription,
        todoPriority = state.todoPriority,
        todoFlag = state.todoFlag,
        showDescriptionError = state.showInvalidDescriptionError,
        inEditingMode = state.inEditingMode,
        onTodoDescriptionChanged = { viewModel.onEvent(AddEditTodoEvent.ChangeTodoDescription(it)) },
        onTodoPriorityChanged = { viewModel.onEvent(AddEditTodoEvent.ChangeTodoPriority(it)) },
        onTodoFlagChanged = { viewModel.onEvent(AddEditTodoEvent.ChangeTodoFlag(it)) },
        onClose = { viewModel.onEvent(AddEditTodoEvent.CloseScreen) },
        onSave = { viewModel.onEvent(AddEditTodoEvent.SaveTodo) },
        onDelete = { viewModel.onEvent(AddEditTodoEvent.DeleteTodo) },
        modifier = modifier
    )
}

@Composable
@ExperimentalMaterial3Api
fun AddEditTodoScreen(
    modifier: Modifier = Modifier,
    inEditingMode: Boolean,
    todoCategoryName: String,
    todoCategoryColor: Color,
    todoCategoryIcon: ImageVector,
    todoDescription: String,
    todoPriority: TodoPriority,
    todoFlag: TodoFlag,
    showDescriptionError: Boolean,
    onTodoDescriptionChanged: (String) -> Unit,
    onTodoPriorityChanged: (TodoPriority) -> Unit,
    onTodoFlagChanged: (TodoFlag) -> Unit,
    onClose: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Scaffold(
        topBar = {
            AddEditTopBar(
                title = if (inEditingMode) stringResource(R.string.edit_todo) else stringResource(id = R.string.add_todo),
                showDeleteIconButton = inEditingMode,
                onDeleteClick = onDelete,
                onCloseDialog = onClose
            )
        }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .padding(contentPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState(), reverseScrolling = true),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                CategoryItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    todoCategoryName = todoCategoryName,
                    todoCategoryIcon = todoCategoryIcon,
                    todoCategoryColor = todoCategoryColor
                )
                Spacer(modifier = Modifier.size(64.dp))

                TodoPrioritySlider(
                    onTodoPriorityChanged = onTodoPriorityChanged,
                    todoPriority = todoPriority
                )
                Spacer(modifier = Modifier.size(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.recurring))
                    Spacer(modifier = Modifier.size(4.dp))
                    Checkbox(
                        checked = todoFlag == TodoFlag.RECURRING,
                        onCheckedChange = { recurring ->
                            if (recurring) onTodoFlagChanged(TodoFlag.RECURRING)
                            else onTodoFlagChanged(TodoFlag.UNDONE)
                        }
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))

                if (inEditingMode && todoFlag != TodoFlag.RECURRING) {
                    val status = when (todoFlag) {
                        TodoFlag.UNDONE -> stringResource(R.string.undone)
                        TodoFlag.IN_PROGRESS -> stringResource(R.string.todo_in_progress)
                        else -> stringResource(R.string.done)
                    }
                    Text(text = stringResource(R.string.status, status))
                }
                Spacer(modifier = Modifier.size(32.dp))

                OutlinedTextField(
                    value = todoDescription,
                    onValueChange = onTodoDescriptionChanged,
                    label = { Text(text = stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showDescriptionError,
                    supportingText = {
                        if (showDescriptionError)
                            Text(text = stringResource(R.string.please_enter_description))
                    }
                )
            }

            PositiveNegativeButtonRow(
                positiveButtonText =
                if (inEditingMode) stringResource(id = R.string.save)
                else stringResource(id = R.string.add),
                negativeButtonText = stringResource(id = R.string.cancel),
                onPositiveClick = onSave,
                onNegativeClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun TodoPrioritySlider(
    modifier: Modifier = Modifier,
    onTodoPriorityChanged: (TodoPriority) -> Unit,
    todoPriority: TodoPriority
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val priorityText = when (todoPriority) {
            TodoPriority.LOW -> stringResource(R.string.low_priority)
            TodoPriority.MEDIUM -> stringResource(R.string.medium_priority)
            TodoPriority.HIGH -> stringResource(R.string.high_priority)
            TodoPriority.VERY_HIGH -> stringResource(R.string.very_high_priority)
        }
        Text(text = priorityText)

        Slider(
            value = todoPriority.priorityNumber.toFloat(),
            steps = TodoPriority.values().size,
            valueRange = 1f..TodoPriority.values().size.toFloat(),
            onValueChange = { todoPriorityNumber ->
                onTodoPriorityChanged(TodoPriority.getByPriorityNumber(todoPriorityNumber.toInt()))
            },
            colors = SliderDefaults.colors(
                thumbColor = getTodoPriorityColorOf(todoPriority),
                activeTrackColor = getTodoPriorityColorOf(todoPriority)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
@ExperimentalMaterial3Api
fun AddEditTodoScreenPreview() {
    SchetodoTheme {
        val todoDescription =
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam"

        AddEditTodoScreen(
            modifier = Modifier.fillMaxSize(),
            todoCategoryName = "Household",
            todoCategoryIcon = Icons.Filled.House,
            todoCategoryColor = Color(0xff85586F),
            todoDescription = todoDescription,
            todoPriority = TodoPriority.HIGH,
            todoFlag = TodoFlag.IN_PROGRESS,
            inEditingMode = true,
            showDescriptionError = false,
            onTodoDescriptionChanged = {},
            onTodoPriorityChanged = {},
            onTodoFlagChanged = {},
            onClose = {},
            onSave = {},
            onDelete = {}
        )
    }
}