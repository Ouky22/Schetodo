package com.example.schetodo.feature.schedule_template.edit_schedule_template

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.feature.schedule.components.ScheduleList
import com.example.schetodo.feature.schedule.components.ScheduleListItem
import com.example.schetodo.feature.schedule.components.createTodoBlocksForPreview
import com.example.schetodo.feature.schedule_template.edit_schedule_template.EditScheduleTemplateEvent.*
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.ui.components.ClickableReadOnlyOutlinedTextField
import com.example.schetodo.ui.components.PositiveNegativeButtonRow
import com.example.schetodo.ui.components.SubDestinationTopAppBar
import com.example.schetodo.ui.theme.SchetodoTheme
import com.example.schetodo.ui.util.pushOntoPreviousBackStackEntry
import com.example.schetodo.ui.util.showDatePicker
import java.time.LocalDate
import java.time.LocalTime

const val ID_OF_SCHEDULE_TEMPLATE_MARKED_FOR_DELETION = "deleted_schedule_block_id"

@Composable
fun EditScheduleTemplateScreen(
    modifier: Modifier = Modifier,
    viewModel: EditScheduleTemplateViewModel,
    schetodoAppState: SchetodoAppState,
    onEditScheduleBlockNavigation: (todoBlockId: Int) -> Unit,
    onAddScheduleBlockNavigation: (templateId: Int) -> Unit,
    onAddScheduleBlockInGapNavigation: (templateId: Int, startTimeStamp: Int, endTimeStamp: Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditScheduleTemplateScreen(
        modifier = modifier,
        scheduleListItems = state.scheduleItems,
        onScheduleBlockItemClick = { todoBlockId ->
            onEditScheduleBlockNavigation(todoBlockId)
        },
        onDeleteScheduleTemplate = {
            schetodoAppState.navController.pushOntoPreviousBackStackEntry(
                ID_OF_SCHEDULE_TEMPLATE_MARKED_FOR_DELETION, viewModel.templateId
            )
            viewModel.onEvent(DeleteScheduleTemplate)
            schetodoAppState.navController.popBackStack()
        },
        scheduleTemplateName = state.scheduleTemplateName,
        scheduleTemplateApplyDate = state.scheduleTemplateApplyDate,
        onScheduleTemplateApplyDateSelected = { selectedDate ->
            viewModel.onEvent(SelectScheduleTemplateApplyDate(selectedDate))
        },
        onApplyTemplateToSelectedDate = {
            viewModel.onEvent(ApplyScheduleTemplateToDate)
        },
        onBackButtonClick = { schetodoAppState.navController.popBackStack() },
        onFabClick = {
            onAddScheduleBlockNavigation(viewModel.templateId)
        },
        onScheduleGapClick = { startTime, endTime ->
            onAddScheduleBlockInGapNavigation(
                viewModel.templateId,
                startTime.toSecondOfDay(),
                endTime.toSecondOfDay()
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduleTemplateScreen(
    modifier: Modifier = Modifier,
    scheduleListItems: List<ScheduleListItem>,
    onScheduleBlockItemClick: (todoBlockId: Int) -> Unit,
    onScheduleGapClick: (startTime: LocalTime, endTime: LocalTime) -> Unit,
    onDeleteScheduleTemplate: () -> Unit,
    scheduleTemplateName: String,
    scheduleTemplateApplyDate: String,
    onScheduleTemplateApplyDateSelected: (LocalDate) -> Unit,
    onApplyTemplateToSelectedDate: () -> Unit,
    onBackButtonClick: () -> Unit,
    onFabClick: () -> Unit
) {
    var openSelectScheduleTemplateApplyDate by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SubDestinationTopAppBar(
                title = scheduleTemplateName,
                showBackButton = true,
                onBackButtonClick = onBackButtonClick
            ) {
                ScheduleTemplateOverflowMenu(
                    onApplyScheduleTemplate = { openSelectScheduleTemplateApplyDate = true },
                    onDeleteScheduleTemplate = onDeleteScheduleTemplate
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_schedule_block)
                )
            }
        }
    ) { contentPadding ->
        ScheduleList(
            modifier = modifier.padding(contentPadding),
            scheduleListItems = scheduleListItems,
            onScheduleBlockItemClick = onScheduleBlockItemClick,
            onScheduleGapClick = onScheduleGapClick
        )
    }

    if (openSelectScheduleTemplateApplyDate)
        SelectScheduleTemplateApplyDateDialog(
            onDismiss = { openSelectScheduleTemplateApplyDate = false },
            selectedDate = scheduleTemplateApplyDate,
            onDateSelected = onScheduleTemplateApplyDateSelected,
            onApplyTemplateToSelectedDate = {
                onApplyTemplateToSelectedDate()
                openSelectScheduleTemplateApplyDate = false
            }
        )
}

@Composable
fun ScheduleTemplateOverflowMenu(
    modifier: Modifier = Modifier,
    onDeleteScheduleTemplate: () -> Unit,
    onApplyScheduleTemplate: () -> Unit
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
                text = { Text(stringResource(R.string.apply)) },
                onClick = {
                    expandOverflowMenu = false
                    onApplyScheduleTemplate()
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Schedule, contentDescription = null)
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.delete)) },
                onClick = {
                    expandOverflowMenu = false
                    onDeleteScheduleTemplate()
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                }
            )
        }
    }
}

@Composable
fun SelectScheduleTemplateApplyDateDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    selectedDate: String,
    onDateSelected: (date: LocalDate) -> Unit,
    onApplyTemplateToSelectedDate: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = modifier) {
            Text(
                text = stringResource(R.string.apply_template),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            ClickableReadOnlyOutlinedTextField(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                value = selectedDate,
                labelText = stringResource(R.string.date),
                onClick = {
                    showDatePicker(
                        context = context,
                        onDateSetListener = { date -> onDateSelected(date) }
                    )
                }
            )

            PositiveNegativeButtonRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                positiveButtonText = stringResource(id = R.string.apply),
                negativeButtonText = stringResource(id = R.string.cancel),
                onPositiveClick = onApplyTemplateToSelectedDate,
                onNegativeClick = onDismiss
            )
        }
    }
}

@Preview
@Composable
fun EditScheduleTemplateScreenPreview() {
    SchetodoTheme {
        EditScheduleTemplateScreen(
            modifier = Modifier.fillMaxSize(),
            scheduleListItems = createTodoBlocksForPreview(),
            onScheduleBlockItemClick = {},
            onDeleteScheduleTemplate = {},
            onScheduleGapClick = { _, _ -> },
            onApplyTemplateToSelectedDate = {},
            scheduleTemplateName = "This is the name for the schedule template",
            scheduleTemplateApplyDate = "2023-02-02",
            onScheduleTemplateApplyDateSelected = {},
            onBackButtonClick = {},
            onFabClick = {}
        )
    }
}