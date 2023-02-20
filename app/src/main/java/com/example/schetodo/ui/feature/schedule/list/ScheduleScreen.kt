package com.example.schetodo.ui.feature.schedule.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel,
    onAddScheduleBlockNavigation: (dateStamp: Long) -> Unit,
    onEditScheduleBlockNavigation: (todoBlockId: Int) -> Unit
) {
    val state by viewModel.scheduleState.collectAsStateWithLifecycle()

    ScheduleScreen(
        modifier = modifier,
        uiScheduleBlocks = state.uiScheduleBlocks,
        currentDate = state.currentDate,
        onPreviousDateButtonClick = {},
        onNextDateButtonClick = {},
        onCurrentDateButtonClick = {},
        onFabClick = { onAddScheduleBlockNavigation(viewModel.currentDateStamp) },
        onEditScheduleBlock = { todoBlockId -> onEditScheduleBlockNavigation(todoBlockId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    uiScheduleBlocks: List<UiScheduleBlock>,
    currentDate: String,
    onPreviousDateButtonClick: () -> Unit,
    onNextDateButtonClick: () -> Unit,
    onCurrentDateButtonClick: () -> Unit,
    onFabClick: () -> Unit,
    onEditScheduleBlock: (todoBlockId: Int) -> Unit
) {
    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = "Schedule",
                showBackButton = false,
                onBackButtonClick = { })
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
        Column(
            modifier = modifier.padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DateNavigator(
                currentDate = currentDate,
                onPreviousDateButtonClick = onPreviousDateButtonClick,
                onNextDateButtonClick = onNextDateButtonClick,
                onCurrentDateButtonClick = onCurrentDateButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 8.dp)
            )

            ScheduleList(
                uiScheduleBlocks = uiScheduleBlocks,
                onListItemClick = onEditScheduleBlock
            )
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
    uiScheduleBlocks: List<UiScheduleBlock>,
    onListItemClick: (todoBlockId: Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = uiScheduleBlocks, key = { it.id }
        ) { uiTodoBlock ->
            ScheduleListItem(
                todoCategories = uiTodoBlock.categories,
                todoDescriptions = uiTodoBlock.todoDescriptions,
                todoBlocKNotes = uiTodoBlock.notes,
                startTimeString = uiTodoBlock.startTime,
                endTimeString = uiTodoBlock.endTime,
                durationString = uiTodoBlock.duration,
                modifier = Modifier.clickable { onListItemClick(uiTodoBlock.id) }
            )
        }
    }
}

@Preview
@Composable
fun ScheduleScreenPreview() {
    SchetodoTheme {
        ScheduleScreen(
            modifier = Modifier.fillMaxSize(),
            uiScheduleBlocks = createTodoBlocksForPreview(),
            currentDate = "2023-02-01",
            onPreviousDateButtonClick = {},
            onNextDateButtonClick = {},
            onCurrentDateButtonClick = {},
            onFabClick = {},
            onEditScheduleBlock = {}
        )
    }
}

private fun createTodoBlocksForPreview(): List<UiScheduleBlock> {
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
        UiScheduleBlock(
            0,
            todoCategories.subList(0, 1),
            todos.subList(0, 1).map { it.description },
            "",
            "13.00",
            "16.30",
            "3h 30min"
        ),
        UiScheduleBlock(
            1,
            todoCategories,
            todos.map { it.description },
            "Lorem ipsum dolor sit",
            "10.00",
            "12.00",
            "2h"
        ),
        UiScheduleBlock(
            id = 2,
            notes = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna",
            startTime = "20.00",
            endTime = "22.00",
            duration = "2h"
        )
    )
}