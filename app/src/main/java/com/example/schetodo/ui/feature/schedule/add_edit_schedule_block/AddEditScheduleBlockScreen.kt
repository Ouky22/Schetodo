package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.schetodo.R
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.components.AddEditTopBar
import com.example.schetodo.ui.components.CategoryItem
import com.example.schetodo.ui.components.PositiveNegativeButtonRow
import com.example.schetodo.ui.components.TodoItem
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockEvent.*
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker.category.TODO_CATEGORY_PICKER_RESULT
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker.todo.TODO_PICKER_RESULT
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.navigation.schedule.TodoCategoryPicker
import com.example.schetodo.ui.navigation.schedule.TodoPicker
import com.example.schetodo.ui.theme.SchetodoTheme
import com.example.schetodo.ui.util.showDatePicker
import com.example.schetodo.ui.util.showTimePicker
import kotlinx.coroutines.launch


@Composable
fun AddEditScheduleBlockScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditScheduleBlockViewModel,
    navController: NavController
) {
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(true) {
        launch {
            viewModel.closeAddEditScheduleBlockScreen.collect { closeScreen ->
                if (closeScreen)
                    navController.popBackStack()
            }
        }
        launch {
            viewModel.errorMessages.collect {
                snackbarHostState.showSnackbar(message = it.asString(context))
            }
        }
        launch {
            navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<List<Int>>(
                TODO_PICKER_RESULT, emptyList()
            )?.collect {
                viewModel.onEvent(SelectTodos(it))
                navController.currentBackStackEntry?.savedStateHandle
                    ?.remove<List<Int>>(TODO_PICKER_RESULT)
            }
        }
        launch {
            navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<List<Int>>(
                TODO_CATEGORY_PICKER_RESULT, emptyList()
            )?.collect {
                viewModel.onEvent(SelectTodoCategories(it))
                navController.currentBackStackEntry?.savedStateHandle
                    ?.remove<List<Int>>(TODO_CATEGORY_PICKER_RESULT)
            }
        }
    }

    AddEditScheduleBlockScreen(
        modifier = modifier,
        todoCategories = state.todoCategories,
        snackbarHostState = snackbarHostState,
        todos = state.todos,
        notes = state.notes,
        date = state.date,
        startTime = state.startTime,
        endTime = state.endTime,
        inEditingMode = state.inEditingMode,
        onDateClick = {
            showDatePicker(context) { selectedDate ->
                viewModel.onEvent(ChangeDate(selectedDate))
            }
        },
        onStartTimeButtonClick = {
            showTimePicker(context) { selectedTime ->
                viewModel.onEvent(ChangeStartTime(selectedTime))
            }
        },
        onEndTimeButtonClick = {
            showTimePicker(context) { selectedTime ->
                viewModel.onEvent(ChangeEndTime(selectedTime))
            }
        },
        onNotesChanged = { viewModel.onEvent(ChangeTodoBlockNotes(it)) },
        onAddTodoButtonClick = { navController.navigate(TodoPicker.route) },
        onAddTodoCategoryButtonClick = { navController.navigate(TodoCategoryPicker.route) },
        onRemoveTodo = { viewModel.onEvent(RemoveSelectedTodo(it)) },
        onRemoveCategory = { viewModel.onEvent(RemoveSelectedTodoCategory(it)) },
        onSave = { viewModel.onEvent(SaveScheduleBlock) },
        onDelete = { viewModel.onEvent(DeleteScheduleBlock) },
        onClose = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScheduleBlockScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    todoCategories: List<TodoCategory>,
    todos: List<Todo>,
    notes: String,
    date: String,
    startTime: String,
    endTime: String,
    inEditingMode: Boolean,
    onDateClick: () -> Unit,
    onStartTimeButtonClick: () -> Unit,
    onEndTimeButtonClick: () -> Unit,
    onNotesChanged: (String) -> Unit,
    onAddTodoButtonClick: () -> Unit,
    onAddTodoCategoryButtonClick: () -> Unit,
    onRemoveCategory: (TodoCategory) -> Unit,
    onRemoveTodo: (Todo) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AddEditTopBar(
                title = if (inEditingMode) stringResource(R.string.edit_schedule_block)
                else stringResource(R.string.add_schedule_block),
                showDeleteIconButton = inEditingMode,
                onDeleteClick = onDelete,
                onCloseDialog = onClose
            )
        }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState(), reverseScrolling = true)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.clickable { onDateClick() },
                    text = date, style = MaterialTheme.typography.headlineMedium
                )
                SelectTimeButtonRow(
                    startTimeButtonText = startTime,
                    endTimeButtonText = endTime,
                    onClickStartTimeButton = onStartTimeButtonClick,
                    onClickEndTimeButton = onEndTimeButtonClick
                )
                HorizontalDividerWithText(
                    text = stringResource(R.string.todo_categories),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                CategoriesFlowRow(
                    todoCategories = todoCategories,
                    onAddCategoryButtonClick = onAddTodoCategoryButtonClick,
                    modifier = Modifier.fillMaxWidth(),
                    onRemoveCategoryIconClick = onRemoveCategory
                )
                HorizontalDividerWithText(
                    text = stringResource(id = R.string.todos),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                TodosColumn(
                    todos = todos,
                    onAddTodoButtonClick = onAddTodoButtonClick,
                    onRemoveTodoIconClick = onRemoveTodo
                )
                HorizontalDividerWithText(
                    text = stringResource(R.string.notes),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChanged,
                    modifier = Modifier.fillMaxWidth()
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
                    .padding(top = 16.dp, bottom = 24.dp)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun SelectTimeButtonRow(
    startTimeButtonText: String,
    endTimeButtonText: String,
    onClickStartTimeButton: () -> Unit,
    onClickEndTimeButton: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onClickStartTimeButton) {
            Text(text = startTimeButtonText)
        }
        Button(onClick = onClickEndTimeButton) {
            Text(text = endTimeButtonText)
        }
    }
}

@Composable
fun TodosColumn(
    modifier: Modifier = Modifier,
    todos: List<Todo>,
    onAddTodoButtonClick: () -> Unit,
    onRemoveTodoIconClick: (Todo) -> Unit
) {
    Column(modifier = modifier) {
        todos.forEach {
            TodoItem(
                todo = it,
                modifier = Modifier
                    .height(75.dp)
                    .padding(4.dp)
                    .fillMaxWidth(),
                endSideContent = {
                    RemoveIcon(
                        onRemoveIconClick = { onRemoveTodoIconClick(it) }
                    )
                },
                alignEndSideContentToEnd = true
            )
        }
        AddCircle(
            onClick = onAddTodoButtonClick,
            contentDescription = stringResource(id = R.string.add_todo),
            modifier = Modifier
                .height(60.dp)
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesFlowRow(
    modifier: Modifier = Modifier,
    todoCategories: List<TodoCategory>,
    onAddCategoryButtonClick: () -> Unit,
    onRemoveCategoryIconClick: (TodoCategory) -> Unit
) {
    if (todoCategories.isNotEmpty())
        FlowRow(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            todoCategories.forEach { todoCategory ->
                CategoryItem(
                    modifier = Modifier
                        .padding(4.dp)
                        .height(75.dp),
                    todoCategoryName = todoCategory.name,
                    todoCategoryColor = Color(todoCategory.color),
                    todoCategoryIcon = getIconByName(todoCategory.iconName)
                        ?: Icons.Filled.Category,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    endSideContent = {
                        RemoveIcon(
                            onRemoveIconClick = { onRemoveCategoryIconClick(todoCategory) }
                        )
                    }
                )
            }
            AddCircle(
                onClick = onAddCategoryButtonClick,
                contentDescription = stringResource(id = R.string.add_todo_category),
                modifier = Modifier
                    .height(60.dp)
                    .padding(start = 10.dp)
            )
        }
    else
        Row(horizontalArrangement = Arrangement.Center) {
            AddCircle(
                onClick = onAddCategoryButtonClick,
                contentDescription = stringResource(id = R.string.add_todo_category),
                modifier = Modifier.height(60.dp)
            )
        }
}

@Composable
fun RemoveIcon(onRemoveIconClick: () -> Unit) {
    Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = stringResource(R.string.remove),
        modifier = Modifier.clickable { onRemoveIconClick() }
    )
}

@Composable
fun AddCircle(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    contentDescription: String
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .align(Alignment.Center)
                .clickable { onClick() },
            imageVector = Icons.Filled.Add,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun HorizontalDividerWithText(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddScheduleBlockScreenPreview() {
    SchetodoTheme {
        AddEditScheduleBlockScreen(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            todoCategories = emptyList(),
            todos = emptyList(),
            notes = "",
            date = "Mo, 2023-02-ß1",
            onDateClick = {},
            startTime = "13.00",
            endTime = "15.30",
            inEditingMode = false,
            onStartTimeButtonClick = {},
            onEndTimeButtonClick = {},
            onNotesChanged = {},
            onAddTodoButtonClick = {},
            onAddTodoCategoryButtonClick = {},
            onClose = {},
            onSave = {},
            onDelete = {},
            onRemoveCategory = {},
            onRemoveTodo = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditScheduleBlockScreenPreview() {
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
    val todos = listOf(
        Todo(
            1,
            "Lorem ipsum dolor sit at,  voluptua. A Lorem ipsum dolor sit at,  voluptua. At vero eos et et justo duo",
            TodoPriority.LOW, TodoFlag.DONE, 1
        ),
        Todo(1, "Lorem ipsum dolor", TodoPriority.HIGH, TodoFlag.UNDONE, 1)
    )

    SchetodoTheme {
        AddEditScheduleBlockScreen(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            todoCategories = categories,
            todos = todos,
            notes = "Lorem ipsum dolor sit at, usto duo",
            date = "Mo, 2023-02-ß1",
            onDateClick = {},
            startTime = "13.00",
            endTime = "15.30",
            inEditingMode = true,
            onStartTimeButtonClick = {},
            onEndTimeButtonClick = {},
            onNotesChanged = {},
            onAddTodoButtonClick = {},
            onAddTodoCategoryButtonClick = {},
            onClose = {},
            onSave = {},
            onDelete = {},
            onRemoveCategory = {},
            onRemoveTodo = {}
        )
    }
}