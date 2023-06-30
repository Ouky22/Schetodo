package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.schetodo.R
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.SchetodoAppState
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
import com.example.schetodo.ui.util.popFromCurrentBackStackEntry
import com.example.schetodo.ui.util.showDatePicker
import com.example.schetodo.ui.util.showTimePicker
import com.google.accompanist.permissions.*
import kotlinx.coroutines.launch

@Composable
fun AddEditScheduleBlockScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditScheduleBlockViewModel,
    schetodoAppState: SchetodoAppState
) {
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val keyBoardController = LocalFocusManager.current

    val showScheduleExactAlarmRationaleDialog = rememberSaveable { mutableStateOf(false) }
    val showNotificationPermissionDialog = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(true) {
        launch {
            viewModel.closeAddEditScheduleBlockScreen.collect { closeScreen ->
                if (closeScreen) {
                    keyBoardController.clearFocus()
                    schetodoAppState.navController.popBackStack()
                }
            }
        }
        launch {
            viewModel.errorMessages.collect {
                snackbarHostState.showSnackbar(
                    message = it.asString(context),
                    withDismissAction = true
                )
            }
        }
        launch {
            schetodoAppState.navController.popFromCurrentBackStackEntry<List<Int>>(
                key = TODO_PICKER_RESULT,
                coroutineScope = this,
                onPop = { todoIds ->
                    viewModel.onEvent((SelectTodos(todoIds)))
                }
            )
        }
        launch {
            schetodoAppState.navController.popFromCurrentBackStackEntry<List<Int>>(
                key = TODO_CATEGORY_PICKER_RESULT,
                coroutineScope = this,
                onPop = { todoCategoryIds ->
                    viewModel.onEvent((SelectTodoCategories(todoCategoryIds)))
                }
            )
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
        showNotificationAtBeginning = state.showNotificationAtBeginning,
        showNotificationAtEnd = state.showNotificationAtEnd,
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
        onChangeShowNotificationAtBeginning = { showNotification ->
            if (showNotification) {
                if (!schetodoAppState.allowedToScheduleExactAlarms)
                    showScheduleExactAlarmRationaleDialog.value = true
                if (!schetodoAppState.allowedToShowNotifications)
                    showNotificationPermissionDialog.value = true
            }
            viewModel.onEvent(ChangeShowNotificationAtBeginning(showNotification))
        },
        onChangeShowNotificationAtEnd = { showNotification ->
            if (showNotification) {
                if (!schetodoAppState.allowedToScheduleExactAlarms)
                    showScheduleExactAlarmRationaleDialog.value = true
                if (!schetodoAppState.allowedToShowNotifications)
                    showNotificationPermissionDialog.value = true
            }
            viewModel.onEvent(ChangeShowNotificationAtEnd(showNotification))
        },
        onAddTodoButtonClick = { schetodoAppState.navController.navigate(TodoPicker.route) },
        onAddTodoCategoryButtonClick = { schetodoAppState.navController.navigate(TodoCategoryPicker.route) },
        onRemoveTodo = { viewModel.onEvent(RemoveSelectedTodo(it)) },
        onRemoveCategory = { viewModel.onEvent(RemoveSelectedTodoCategory(it)) },
        onSave = { viewModel.onEvent(SaveScheduleBlock) },
        onDelete = { viewModel.onEvent(DeleteScheduleBlock) },
        onClose = { schetodoAppState.navController.popBackStack() }
    )

    if (showScheduleExactAlarmRationaleDialog.value)
        ScheduleExactAlarmPermissionRationaleDialog(
            context = context,
            onCloseDialog = { showScheduleExactAlarmRationaleDialog.value = false }
        )
    if (showNotificationPermissionDialog.value)
        ShowNotificationPermissionDialog(
            onCloseDialog = { showNotificationPermissionDialog.value = false }
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
    showNotificationAtBeginning: Boolean,
    showNotificationAtEnd: Boolean,
    inEditingMode: Boolean,
    onDateClick: () -> Unit,
    onStartTimeButtonClick: () -> Unit,
    onEndTimeButtonClick: () -> Unit,
    onNotesChanged: (String) -> Unit,
    onAddTodoButtonClick: () -> Unit,
    onAddTodoCategoryButtonClick: () -> Unit,
    onRemoveCategory: (TodoCategory) -> Unit,
    onRemoveTodo: (Todo) -> Unit,
    onChangeShowNotificationAtBeginning: (Boolean) -> Unit,
    onChangeShowNotificationAtEnd: (Boolean) -> Unit,
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
                    .verticalScroll(rememberScrollState(), reverseScrolling = false)
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
                HorizontalDividerWithText(
                    text = "Notifications",
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                NotificationSection(
                    showNotificationAtBeginning = showNotificationAtBeginning,
                    showNotificationAtEnd = showNotificationAtEnd,
                    onChangeShowNotificationAtBeginning = onChangeShowNotificationAtBeginning,
                    onChangeShowNotificationAtEnd = onChangeShowNotificationAtEnd,
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

@Composable
fun NotificationSection(
    modifier: Modifier = Modifier,
    showNotificationAtBeginning: Boolean,
    showNotificationAtEnd: Boolean,
    onChangeShowNotificationAtBeginning: (Boolean) -> Unit,
    onChangeShowNotificationAtEnd: (Boolean) -> Unit
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = showNotificationAtBeginning,
                onCheckedChange = onChangeShowNotificationAtBeginning
            )
            Text(text = stringResource(R.string.show_notification_at_beginning))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = showNotificationAtEnd,
                onCheckedChange = onChangeShowNotificationAtEnd
            )
            Text(text = stringResource(R.string.show_notification_at_end))
        }
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

@Composable
fun ScheduleExactAlarmPermissionRationaleDialog(
    context: Context,
    onCloseDialog: () -> Unit
) {
    AlertDialog(
        title = { Text(stringResource(R.string.alarms_and_reminders_permission)) },
        text = { Text(stringResource(id = R.string.alarms_and_reminders_permission_rationale)) },
        onDismissRequest = onCloseDialog,
        confirmButton = {
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    ActivityCompat.startActivity(
                        context,
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM),
                        null
                    )
                onCloseDialog()
            }
            ) {
                Text(stringResource(R.string.grant_permission))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCloseDialog) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShowNotificationPermissionDialog(
    onCloseDialog: () -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
        return

    val notificationPermissionState =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    if (notificationPermissionState.status.shouldShowRationale)
        AlertDialog(
            title = { Text(stringResource(R.string.notification_permission)) },
            text = { Text(stringResource(R.string.notification_permission_rationale)) },
            onDismissRequest = onCloseDialog,
            confirmButton = {
                Button(onClick = {
                    notificationPermissionState.launchPermissionRequest()
                    onCloseDialog()
                }
                ) {
                    Text(stringResource(R.string.grant_permission))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onCloseDialog) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    else if (!notificationPermissionState.status.isGranted) {
        LaunchedEffect(key1 = true) {
            notificationPermissionState.launchPermissionRequest()
        }
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
            showNotificationAtBeginning = true,
            showNotificationAtEnd = false,
            onChangeShowNotificationAtBeginning = {},
            onChangeShowNotificationAtEnd = {},
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
            showNotificationAtBeginning = true,
            showNotificationAtEnd = false,
            onChangeShowNotificationAtBeginning = {},
            onChangeShowNotificationAtEnd = {},
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