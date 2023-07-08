package com.example.schetodo.ui.feature.schedule.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.ui.components.OverflowMenu
import com.example.schetodo.ui.components.PositiveNegativeButtonRow
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.ID_OF_TODO_BLOCK_MARKED_FOR_DELETION
import com.example.schetodo.ui.feature.schedule.list.ScheduleEvent.*
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme
import com.example.schetodo.ui.util.UiText
import com.example.schetodo.ui.util.popFromCurrentBackStackEntry
import com.example.schetodo.ui.util.showDatePicker
import com.example.schetodo.ui.util.showSnackbarWithActionHandler
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel,
    onAddScheduleBlockNavigation: (dateStamp: Long) -> Unit,
    onEditScheduleBlockNavigation: (todoBlockId: Int) -> Unit,
    onAddScheduleBlockInGapNavigation: (dateStamp: Long, startTimeStamp: Int, endTimeStamp: Int) -> Unit,
    onScheduleTemplatesScreenNavigation: () -> Unit,
    schetodoAppState: SchetodoAppState
) {
    val state by viewModel.scheduleState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(true) {
        launch {
            schetodoAppState.navController.popFromCurrentBackStackEntry<Int>(
                key = ID_OF_TODO_BLOCK_MARKED_FOR_DELETION,
                onPop = { todoBlockId ->
                    snackbarHostState.showSnackbarWithActionHandler(
                        message = context.getString(R.string.deleted_schedule_block),
                        actionLabel = context.getString(R.string.undo),
                        onActionPerformed = {
                            viewModel.onEvent(UnmarkTodoBlockForDeletion(todoBlockId))
                        }
                    )
                }
            )
        }
    }

    ScheduleScreen(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        schedules = state.schedules,
        currentDateString = state.currentDateString,
        currentDate = state.currentDate,
        onNavigateToPreviousDate = { viewModel.onEvent(GoToPreviousDate) },
        onNavigateToNextDate = { viewModel.onEvent(GoToNextDate) },
        onNavigateToAnyDate = { date -> viewModel.onEvent(GoToAnyDate(date)) },
        onGoToCurrentDateButtonClick = { viewModel.onEvent(GoToCurrentDate) },
        onScheduleTemplatesButtonClick = onScheduleTemplatesScreenNavigation,
        onSaveScheduleAsTemplateButtonClick = { viewModel.onEvent(SaveCurrentScheduleAsTemplate(it)) },
        onFabClick = { onAddScheduleBlockNavigation(state.currentDate.toEpochDay()) },
        onEditScheduleBlock = { todoBlockId -> onEditScheduleBlockNavigation(todoBlockId) },
        onAddScheduleGapButtonClick = { startTime, endTime ->
            onAddScheduleBlockInGapNavigation(
                state.currentDate.toEpochDay(),
                startTime.toSecondOfDay(),
                endTime.toSecondOfDay()
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    schedules: Map<Long, List<ScheduleListItem>>,
    currentDateString: String,
    currentDate: LocalDate,
    onNavigateToPreviousDate: () -> Unit,
    onNavigateToNextDate: () -> Unit,
    onNavigateToAnyDate: (LocalDate) -> Unit,
    onGoToCurrentDateButtonClick: () -> Unit,
    onScheduleTemplatesButtonClick: () -> Unit,
    onSaveScheduleAsTemplateButtonClick: (templateName: String) -> Unit,
    onFabClick: () -> Unit,
    onEditScheduleBlock: (todoBlockId: Int) -> Unit,
    onAddScheduleGapButtonClick: (startTime: LocalTime, endTime: LocalTime) -> Unit,
) {
    var showEnterScheduleTemplateNameDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = stringResource(R.string.schedule),
                showBackButton = false,
                onBackButtonClick = { },
                actions = {
                    IconButton(onClick = onGoToCurrentDateButtonClick) {
                        Icon(
                            imageVector = Icons.Filled.Today,
                            contentDescription = stringResource(R.string.go_to_current_date)
                        )
                    }

                    ScheduleOverflowMenu(
                        onScheduleTemplatesOptionClick = onScheduleTemplatesButtonClick,
                        onSaveAsTemplateOptionClick = { showEnterScheduleTemplateNameDialog = true }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_schedule_block)
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->
        Column(
            modifier = modifier.padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current

            DateNavigator(
                currentDate = currentDateString,
                onPreviousDateButtonClick = onNavigateToPreviousDate,
                onNextDateButtonClick = onNavigateToNextDate,
                onCurrentDateButtonClick = {
                    showDatePicker(context) { selectedDate -> onNavigateToAnyDate(selectedDate) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 8.dp)
            )

            SchedulePager(
                currentDate = currentDate,
                onNavigateToNextDate = onNavigateToNextDate,
                onNavigateToPreviousDate = onNavigateToPreviousDate,
                key = { page -> page } // use page as key because it's the date stamp of the schedule
            ) { page ->
                ScheduleList(
                    modifier = Modifier.fillMaxSize(),
                    scheduleListItems = schedules[page.toLong()] ?: emptyList(),
                    onListItemClick = onEditScheduleBlock,
                    onAddScheduleGapButtonClick = onAddScheduleGapButtonClick
                )
            }
        }
    }

    if (showEnterScheduleTemplateNameDialog)
        EnterScheduleTemplateNameDialog(
            onDismiss = { showEnterScheduleTemplateNameDialog = false },
            onSaveName = { templateName ->
                showEnterScheduleTemplateNameDialog = false
                onSaveScheduleAsTemplateButtonClick(templateName)
            }
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterScheduleTemplateNameDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSaveName: (name: String) -> Unit
) {
    var showInvalidScheduleTemplateNameError by rememberSaveable { mutableStateOf(false) }
    var nameInput by rememberSaveable { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = modifier
        ) {
            Text(
                text = stringResource(R.string.save_schedule_as_template),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    showInvalidScheduleTemplateNameError = false
                },
                label = { Text(stringResource(R.string.name)) },
                singleLine = true,
                isError = showInvalidScheduleTemplateNameError,
                trailingIcon = {
                    if (showInvalidScheduleTemplateNameError)
                        Icon(
                            Icons.Filled.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                },
                supportingText = {
                    if (showInvalidScheduleTemplateNameError)
                        Text(
                            stringResource(R.string.please_enter_name),
                            color = MaterialTheme.colorScheme.error
                        )
                }
            )

            PositiveNegativeButtonRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                positiveButtonText = stringResource(id = R.string.save),
                negativeButtonText = stringResource(id = R.string.cancel),
                onPositiveClick = {
                    val trimmedNameInput = nameInput.trim()
                    if (trimmedNameInput == "")
                        showInvalidScheduleTemplateNameError = true
                    else
                        onSaveName(trimmedNameInput)
                },
                onNegativeClick = onDismiss
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SchedulePager(
    currentDate: LocalDate,
    onNavigateToNextDate: () -> Unit,
    onNavigateToPreviousDate: () -> Unit,
    key: ((index: Int) -> Any)? = null,
    pageContent: @Composable (Int) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = currentDate.toEpochDay().toInt())

    var previousPage by remember { mutableStateOf(pagerState.currentPage) }
    var scrollingAnimatedBySystem by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { currentPage ->
            if (scrollingAnimatedBySystem) return@collect
            when {
                currentPage > previousPage -> onNavigateToNextDate()
                currentPage < previousPage -> onNavigateToPreviousDate()
            }
            previousPage = currentPage
        }
    }

    LaunchedEffect(currentDate) {
        if (scrollingAnimatedBySystem) return@LaunchedEffect

        try {
            scrollingAnimatedBySystem = true
            val targetPage = currentDate.toEpochDay().toInt()
            pagerState.animateScrollToPage(targetPage)
        } finally {
            scrollingAnimatedBySystem = false
            previousPage = pagerState.currentPage
        }
    }

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
        pageCount = Int.MAX_VALUE,
        key = key
    ) { page ->
        pageContent(page)
    }
}

@Composable
fun DateNavigator(
    modifier: Modifier = Modifier,
    currentDate: String,
    onPreviousDateButtonClick: () -> Unit,
    onNextDateButtonClick: () -> Unit,
    onCurrentDateButtonClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousDateButtonClick) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIos,
                contentDescription = stringResource(R.string.go_to_previous_date)
            )
        }
        OutlinedButton(onClick = onCurrentDateButtonClick) {
            Text(text = currentDate)
        }
        IconButton(onClick = onNextDateButtonClick) {
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = stringResource(R.string.go_to_next_date)
            )
        }
    }
}

@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    scheduleListItems: List<ScheduleListItem>,
    onListItemClick: (todoBlockId: Int) -> Unit,
    onAddScheduleGapButtonClick: (startTime: LocalTime, endTime: LocalTime) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = scheduleListItems, key = { it.startTime.toSecondOfDay() }
        ) { scheduleListItem ->
            when (scheduleListItem) {
                is UiScheduleBlock ->
                    ScheduleBlockItem(
                        todoCategories = scheduleListItem.categories,
                        todoDescriptions = scheduleListItem.todoDescriptions,
                        todoBlocKNotes = scheduleListItem.notes,
                        startTimeString = scheduleListItem.startTimeText,
                        endTimeString = scheduleListItem.endTimeText,
                        durationString = "${scheduleListItem.durationHours.asString()} ${scheduleListItem.durationMinutes.asString()}",
                        modifier = Modifier.clickable { onListItemClick(scheduleListItem.todoBlockId) },
                        elevate = scheduleListItem.isCurrentScheduleBlock
                    )
                is ScheduleGap ->
                    OutlinedButton(
                        onClick = {
                            onAddScheduleGapButtonClick(
                                scheduleListItem.startTime,
                                scheduleListItem.endTime
                            )
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "${scheduleListItem.durationHours.asString()} ${scheduleListItem.durationMinutes.asString()}")
                    }
            }
        }
    }
}

@Composable
fun ScheduleOverflowMenu(
    modifier: Modifier = Modifier,
    onScheduleTemplatesOptionClick: () -> Unit,
    onSaveAsTemplateOptionClick: () -> Unit
) {
    Box(modifier = modifier) {
        var expandOverflowMenu by remember { mutableStateOf(false) }

        IconButton(onClick = { expandOverflowMenu = !expandOverflowMenu }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.more)
            )
        }
        DropdownMenu(
            expanded = expandOverflowMenu,
            onDismissRequest = { expandOverflowMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.schedule_templates)) },
                onClick = {
                    expandOverflowMenu = false
                    onScheduleTemplatesOptionClick()
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.TableRows, contentDescription = null)
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.save_as_template)) },
                onClick = {
                    expandOverflowMenu = false
                    onSaveAsTemplateOptionClick()
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Save, contentDescription = null)
                }
            )
        }
    }
}

@Preview
@Composable
fun EnterScheduleTemplateNameDialogPreview() {
    SchetodoTheme {
        EnterScheduleTemplateNameDialog(
            onDismiss = {},
            onSaveName = {}
        )
    }
}

@Preview
@Composable
fun ScheduleScreenPreview() {
    SchetodoTheme {
        ScheduleScreen(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            schedules = createTodoBlocksForPreview(),
            currentDateString = "2023-02-01",
            currentDate = LocalDate.now(),
            onNavigateToPreviousDate = {},
            onNavigateToNextDate = {},
            onNavigateToAnyDate = {},
            onGoToCurrentDateButtonClick = {},
            onScheduleTemplatesButtonClick = {},
            onSaveScheduleAsTemplateButtonClick = {},
            onFabClick = {},
            onEditScheduleBlock = {},
            onAddScheduleGapButtonClick = { _, _ -> }
        )
    }
}

private fun createTodoBlocksForPreview(): Map<Long, List<ScheduleListItem>> {
    val todoCategories = listOf(
        TodoCategory(
            1, "Household", todoCategoryColors[0].toArgb().toLong(), null,
            Icons.Filled.House.name
        ),
        TodoCategory(
            2, "Study", todoCategoryColors[6].toArgb().toLong(), null, Icons.Filled.School.name
        ),
        TodoCategory(
            2, "Sports", todoCategoryColors[4].toArgb().toLong(), null, Icons.Filled.School.name
        ),
        TodoCategory(
            2, "Piano", todoCategoryColors[3].toArgb().toLong(), null, Icons.Filled.School.name
        )
    )
    val todos = listOf(
        Todo(1, "Wash the dishes", TodoPriority.LOW, TodoFlag.UNDONE, 1),
        Todo(2, "Clean the floor", TodoPriority.LOW, TodoFlag.UNDONE, 1),
        Todo(3, "Bake a cake", TodoPriority.LOW, TodoFlag.UNDONE, 1)
    )
    val scheduleListItems = listOf(
        ScheduleGap(
            startTime = LocalTime.of(0, 0),
            endTime = LocalTime.of(12, 0),
            durationHours = UiText.DynamicString("12h")
        ),
        UiScheduleBlock(
            todoBlockId = 1,
            startTime = LocalTime.of(12, 0),
            endTime = LocalTime.of(15, 0),
            startTimeText = "12:00",
            endTimeText = "15:00",
            durationHours = UiText.DynamicString("3h"),
            durationMinutes = UiText.DynamicString("30min"),
            categories = todoCategories.subList(0, 1),
            todoDescriptions = todos.subList(0, 1).map { it.description },
            notes = "",
            isCurrentScheduleBlock = false
        ),
        ScheduleGap(
            startTime = LocalTime.of(15, 0),
            endTime = LocalTime.of(15, 30),
            durationMinutes = UiText.DynamicString("30min")
        ),
        UiScheduleBlock(
            todoBlockId = 3,
            startTime = LocalTime.of(15, 30),
            endTime = LocalTime.of(16, 0),
            startTimeText = "15:30",
            endTimeText = "16:00",
            durationMinutes = UiText.DynamicString("30min"),
            categories = todoCategories,
            todoDescriptions = todos.map { it.description },
            notes = "Lorem ipsum dolor sit",
            isCurrentScheduleBlock = true
        ),
        UiScheduleBlock(
            todoBlockId = 4,
            startTime = LocalTime.of(16, 0),
            endTime = LocalTime.of(17, 0),
            startTimeText = "16:00",
            endTimeText = "17:00",
            durationHours = UiText.DynamicString("1h"),
            notes = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna",
            isCurrentScheduleBlock = false
        ),
        ScheduleGap(
            startTime = LocalTime.of(17, 0),
            endTime = LocalTime.of(23, 59),
            durationHours = UiText.DynamicString("6h"),
            durationMinutes = UiText.DynamicString("59min")
        )
    )

    return mapOf(LocalDate.now().toEpochDay() to scheduleListItems)
}