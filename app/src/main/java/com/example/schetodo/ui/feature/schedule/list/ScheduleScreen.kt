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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.ID_OF_TODO_BLOCK_MARKED_FOR_DELETION
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme
import com.example.schetodo.ui.util.UiText
import com.example.schetodo.ui.feature.schedule.list.ScheduleEvent.*
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
        currentDate = state.currentDate,
        onNavigateToPreviousDate = { viewModel.onEvent(GoToPreviousDate) },
        onNavigateToNextDate = { viewModel.onEvent(GoToNextDate) },
        onNavigateToAnyDate = { date -> viewModel.onEvent(GoToAnyDate(date)) },
        onGoToCurrentDateButtonClick = { viewModel.onEvent(GoToCurrentDate) },
        onFabClick = { onAddScheduleBlockNavigation(viewModel.currentDateStamp) },
        onEditScheduleBlock = { todoBlockId -> onEditScheduleBlockNavigation(todoBlockId) },
        onAddScheduleGapButtonClick = { startTime, endTime ->
            onAddScheduleBlockInGapNavigation(
                viewModel.currentDateStamp,
                startTime.toSecondOfDay(),
                endTime.toSecondOfDay()
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    schedules: Array<List<ScheduleListItem>>,
    currentDate: String,
    onNavigateToPreviousDate: () -> Unit,
    onNavigateToNextDate: () -> Unit,
    onNavigateToAnyDate: (LocalDate) -> Unit,
    onGoToCurrentDateButtonClick: () -> Unit,
    onFabClick: () -> Unit,
    onEditScheduleBlock: (todoBlockId: Int) -> Unit,
    onAddScheduleGapButtonClick: (startTime: LocalTime, endTime: LocalTime) -> Unit,
) {
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
            // initial page must be a number that gives the remainder 1 when divided by 3
            // so that the first page gets the second element from the schedules array
            val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2 + 1)
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current

            DateNavigator(
                currentDate = currentDate,
                onPreviousDateButtonClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                },
                onNextDateButtonClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                onCurrentDateButtonClick = {
                    showDatePicker(context) { selectedDate ->
                        onNavigateToAnyDate(selectedDate)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 8.dp)
            )

            var previousPage by remember { mutableStateOf(pagerState.currentPage) }
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }.collect { currentPage ->
                    when {
                        currentPage > previousPage -> onNavigateToNextDate()
                        currentPage < previousPage -> onNavigateToPreviousDate()
                    }
                    previousPage = currentPage
                }
            }

            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                pageCount = Int.MAX_VALUE
            ) { page ->
                ScheduleList(
                    modifier = Modifier.fillMaxSize(),
                    scheduleListItems = schedules[page % schedules.size],
                    onListItemClick = onEditScheduleBlock,
                    onAddScheduleGapButtonClick = onAddScheduleGapButtonClick
                )
            }
        }
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

@Preview
@Composable
fun ScheduleScreenPreview() {
    SchetodoTheme {
        ScheduleScreen(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            schedules = arrayOf(createTodoBlocksForPreview()),
            currentDate = "2023-02-01",
            onNavigateToPreviousDate = {},
            onNavigateToNextDate = {},
            onNavigateToAnyDate = {},
            onGoToCurrentDateButtonClick = {},
            onFabClick = {},
            onEditScheduleBlock = {},
            onAddScheduleGapButtonClick = { _, _ -> }
        )
    }
}

private fun createTodoBlocksForPreview(): List<ScheduleListItem> {
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
    return listOf(
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
}