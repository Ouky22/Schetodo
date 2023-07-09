package com.example.schetodo.ui.feature.schedule.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.components.CategoryItem
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme
import com.example.schetodo.ui.util.appendDotsToStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleBlockItem(
    modifier: Modifier = Modifier,
    todoCategories: List<TodoCategory>,
    todoDescriptions: List<String>,
    todoBlocKNotes: String,
    startTimeString: String,
    endTimeString: String,
    durationString: String,
    elevate: Boolean = false
) {
    OutlinedCard(
        modifier = modifier,
        elevation = if (elevate) CardDefaults.elevatedCardElevation() else CardDefaults.outlinedCardElevation()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            if (todoCategories.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.wrapContentHeight()
                ) {
                    todoCategories.forEach { todoCategory ->
                        CategoryItem(
                            modifier = Modifier
                                .padding(2.dp)
                                .height(50.dp),
                            todoCategoryName = todoCategory.name,
                            todoCategoryColor = Color(todoCategory.color),
                            todoCategoryIcon = getIconByName(todoCategory.iconName)
                                ?: Icons.Filled.Category,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            if (todoDescriptions.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = appendDotsToStrings(todoDescriptions, separator = "\n")
                )
            }
            if (todoBlocKNotes.isNotEmpty()) {
                Text(
                    text = todoBlocKNotes,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Divider(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(CenterHorizontally)
            ) {
                Text(text = "$startTimeString - $endTimeString")
                Text(text = durationString)
            }
        }
    }
}

@Preview
@Composable
fun ScheduleBlockItemPreview() {
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
    SchetodoTheme {
        ScheduleBlockItem(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            todoCategories = todoCategories,
            todoDescriptions = listOf("Wash the dishes", "Clean the floor", "Bake a cake"),
            todoBlocKNotes = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam",
            startTimeString = "14.00",
            endTimeString = "16.30",
            durationString = "2 Std 30 min"
        )
    }
}

@Preview
@Composable
fun ScheduleBlockItemPreviewWithoutCategoriesAndTodos() {
    SchetodoTheme {
        ScheduleBlockItem(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            todoCategories = emptyList(),
            todoDescriptions = emptyList(),
            todoBlocKNotes = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam",
            startTimeString = "14.00",
            endTimeString = "16.30",
            durationString = "2 Std 30 min"
        )
    }
}

@Preview
@Composable
fun ScheduleBlockItemPreviewWithoutNotes() {
    val category = TodoCategory(
        1, "Household", todoCategoryColors[0].toArgb().toLong(), null,
        Icons.Filled.House.name
    )
    SchetodoTheme {
        ScheduleBlockItem(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            todoCategories = listOf(category),
            todoDescriptions = listOf("Wash the dishes"),
            todoBlocKNotes = "",
            startTimeString = "14.00",
            endTimeString = "16.30",
            durationString = "2 Std 30 min"
        )
    }
}