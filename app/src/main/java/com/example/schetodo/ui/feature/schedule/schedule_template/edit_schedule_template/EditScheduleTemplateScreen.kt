package com.example.schetodo.ui.feature.schedule.schedule_template.edit_schedule_template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.feature.schedule.components.ScheduleList
import com.example.schetodo.ui.feature.schedule.components.ScheduleListItem
import com.example.schetodo.ui.feature.schedule.components.createTodoBlocksForPreview
import com.example.schetodo.ui.theme.SchetodoTheme
import java.time.LocalTime

@Composable
fun EditScheduleTemplateScreen(
    modifier: Modifier = Modifier,
    viewModel: EditScheduleTemplateViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditScheduleTemplateScreen(
        modifier = modifier,
        scheduleListItems = state.scheduleItems,
        onScheduleBlockItemClick = {},
        onScheduleGapClick = { startTime, endTime -> },
        scheduleTemplateName = state.scheduleTemplateName,
        onBackButtonClick = {},
        onFabClick = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduleTemplateScreen(
    modifier: Modifier = Modifier,
    scheduleListItems: List<ScheduleListItem>,
    onScheduleBlockItemClick: (todoBlockId: Int) -> Unit,
    onScheduleGapClick: (startTime: LocalTime, endTime: LocalTime) -> Unit,
    scheduleTemplateName: String,
    onBackButtonClick: () -> Unit,
    onFabClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = scheduleTemplateName,
                showBackButton = true,
                onBackButtonClick = onBackButtonClick
            ) {
                ScheduleTemplateOverflowMenu(
                    onApplyScheduleTemplate = {},
                    onDeleteScheduleTemplate = {}
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

@Preview
@Composable
fun EditScheduleTemplateScreenPreview() {
    SchetodoTheme {
        EditScheduleTemplateScreen(
            modifier = Modifier.fillMaxSize(),
            scheduleListItems = createTodoBlocksForPreview(),
            onScheduleBlockItemClick = {},
            onScheduleGapClick = { _, _ -> },
            scheduleTemplateName = "This is the name for the schedule template",
            onBackButtonClick = {},
            onFabClick = {}
        )
    }
}