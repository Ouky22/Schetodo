package com.example.schetodo.feature.schedule.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.feature.schedule.add_edit_schedule_block.ID_OF_TODO_BLOCK_MARKED_FOR_DELETION
import com.example.schetodo.feature.schedule.components.*
import com.example.schetodo.feature.schedule.list.ScheduleEvent.*
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.ui.components.PositiveNegativeButtonRow
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.theme.SchetodoTheme
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
        maxDate = state.maxDate,
        scheduleTemplateName = state.scheduleTemplateName,
        showInvalidScheduleTemplateNameError = state.showInvalidScheduleTemplateNameError,
        showEnterScheduleTemplateNameDialog = state.showEnterScheduleTemplateNameDialog,
        onChangeScheduleTemplateName = { viewModel.onEvent(ChangeScheduleTemplateName(it)) },
        onOpenEnterScheduleTemplateNameDialog = {
            viewModel.onEvent(OpenEnterScheduleTemplateNameDialog)
        },
        onCloseEnterScheduleTemplateNameDialog = {
            viewModel.onEvent(CloseEnterScheduleTemplateNameDialog)
        },
        canNavigateToPreviousDate = state.canNavigateToPreviousDate,
        canNavigateToNextDate = state.canNavigateToNextDate,
        onNavigateToPreviousDate = { viewModel.onEvent(GoToPreviousDate) },
        onNavigateToNextDate = { viewModel.onEvent(GoToNextDate) },
        onNavigateToAnyDate = { date -> viewModel.onEvent(GoToAnyDate(date)) },
        onGoToCurrentDateButtonClick = { viewModel.onEvent(GoToCurrentDate) },
        onScheduleTemplatesButtonClick = onScheduleTemplatesScreenNavigation,
        onSaveScheduleAsTemplateButtonClick = { viewModel.onEvent(SaveCurrentScheduleAsTemplate) },
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
    maxDate: LocalDate,
    scheduleTemplateName: String,
    showInvalidScheduleTemplateNameError: Boolean,
    showEnterScheduleTemplateNameDialog: Boolean,
    onOpenEnterScheduleTemplateNameDialog: () -> Unit,
    onCloseEnterScheduleTemplateNameDialog: () -> Unit,
    onChangeScheduleTemplateName: (templateName: String) -> Unit,
    canNavigateToNextDate: Boolean,
    canNavigateToPreviousDate: Boolean,
    onNavigateToPreviousDate: () -> Unit,
    onNavigateToNextDate: () -> Unit,
    onNavigateToAnyDate: (LocalDate) -> Unit,
    onGoToCurrentDateButtonClick: () -> Unit,
    onScheduleTemplatesButtonClick: () -> Unit,
    onSaveScheduleAsTemplateButtonClick: () -> Unit,
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

                    ScheduleOverflowMenu(
                        onScheduleTemplatesOptionClick = onScheduleTemplatesButtonClick,
                        onSaveAsTemplateOptionClick = onOpenEnterScheduleTemplateNameDialog
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
                onPreviousDateButtonEnabled = canNavigateToPreviousDate,
                onNextDateButtonEnabled = canNavigateToNextDate,
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
                maxDate = maxDate,
                onNavigateToNextDate = onNavigateToNextDate,
                onNavigateToPreviousDate = onNavigateToPreviousDate,
                key = { page -> page } // use page as key because it's the date stamp of the schedule
            ) { page ->
                ScheduleList(
                    modifier = Modifier.fillMaxSize(),
                    scheduleListItems = schedules[page.toLong()] ?: emptyList(),
                    onScheduleBlockItemClick = onEditScheduleBlock,
                    onScheduleGapClick = onAddScheduleGapButtonClick
                )
            }
        }
    }

    if (showEnterScheduleTemplateNameDialog)
        EnterScheduleTemplateNameDialog(
            onDismiss = onCloseEnterScheduleTemplateNameDialog,
            onSaveName = onSaveScheduleAsTemplateButtonClick,
            scheduleTemplateName = scheduleTemplateName,
            onChangeScheduleTemplateName = onChangeScheduleTemplateName,
            showInvalidScheduleTemplateNameError = showInvalidScheduleTemplateNameError
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterScheduleTemplateNameDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSaveName: () -> Unit,
    scheduleTemplateName: String,
    onChangeScheduleTemplateName: (templateName: String) -> Unit,
    showInvalidScheduleTemplateNameError: Boolean
) {
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
                value = scheduleTemplateName,
                onValueChange = onChangeScheduleTemplateName,
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
                onPositiveClick = onSaveName,
                onNegativeClick = onDismiss
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SchedulePager(
    currentDate: LocalDate,
    maxDate: LocalDate,
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
        pageCount = maxDate.toEpochDay().toInt() + 1,
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
    onCurrentDateButtonClick: () -> Unit,
    onNextDateButtonEnabled: Boolean,
    onPreviousDateButtonEnabled: Boolean
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onPreviousDateButtonClick,
            enabled = onPreviousDateButtonEnabled
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIos,
                contentDescription = stringResource(R.string.go_to_previous_date)
            )
        }
        OutlinedButton(onClick = onCurrentDateButtonClick) {
            Text(text = currentDate)
        }
        IconButton(
            onClick = onNextDateButtonClick,
            enabled = onNextDateButtonEnabled
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = stringResource(R.string.go_to_next_date)
            )
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
            onSaveName = {},
            scheduleTemplateName = "",
            onChangeScheduleTemplateName = {},
            showInvalidScheduleTemplateNameError = false
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
            schedules = mapOf(LocalDate.now().toEpochDay() to createTodoBlocksForPreview()),
            currentDateString = "2023-02-01",
            currentDate = LocalDate.now(),
            maxDate = LocalDate.now(),
            scheduleTemplateName = "",
            showInvalidScheduleTemplateNameError = false,
            onChangeScheduleTemplateName = {},
            onOpenEnterScheduleTemplateNameDialog = {},
            onCloseEnterScheduleTemplateNameDialog = {},
            showEnterScheduleTemplateNameDialog = false,
            canNavigateToPreviousDate = true,
            canNavigateToNextDate = true,
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