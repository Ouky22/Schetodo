package com.example.schetodo.ui.feature.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schetodo.data.entity.*
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier
) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    uiTodoBlocks: List<UiTodoBlock>
) {
    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = "Schedule",
                showBackButton = false,
                onBackButtonClick = { })
        }
    ) { contentPadding ->
        Column(
            modifier = modifier.padding(contentPadding)
        ) {
            ScheduleList(
                uiTodoBlocks = uiTodoBlocks
            )
        }
    }
}

@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    uiTodoBlocks: List<UiTodoBlock>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = uiTodoBlocks, key = { it.id }
        ) { uiTodoBlock ->
            ScheduleListItem(
                todoCategories = uiTodoBlock.categories,
                todoDescriptions = uiTodoBlock.todoDescriptions,
                todoBlocKNotes = uiTodoBlock.notes,
                startTimeString = uiTodoBlock.startTime,
                endTimeString = uiTodoBlock.endTime,
                durationString = uiTodoBlock.duration
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
            uiTodoBlocks = createTodoBlocksForPreview()
        )
    }
}

private fun createTodoBlocksForPreview(): List<UiTodoBlock> {
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
        UiTodoBlock(
            0,
            todoCategories.subList(0, 1),
            todos.subList(0, 1).map { it.description },
            "",
            "13.00",
            "16.30",
            "3h 30min"
        ),
        UiTodoBlock(
            1,
            todoCategories,
            todos.map { it.description },
            "Lorem ipsum dolor sit",
            "10.00",
            "12.00",
            "2h"
        ),
        UiTodoBlock(
            id = 2,
            notes = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna",
            startTime = "20.00",
            endTime = "22.00",
            duration = "2h"
        )
    )
}