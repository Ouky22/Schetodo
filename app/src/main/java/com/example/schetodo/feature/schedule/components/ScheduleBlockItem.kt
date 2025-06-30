package com.example.schetodo.feature.schedule.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.feature.todos.getIconByName
import com.example.schetodo.feature.todos.todoCategoryColors
import com.example.schetodo.ui.components.CategoryItem
import com.example.schetodo.ui.theme.SchetodoTheme
import com.example.schetodo.ui.util.appendDotsToStrings

@Composable
fun ScheduleBlockItem(
    modifier: Modifier = Modifier,
    todoCategories: List<TodoCategory> = emptyList(),
    todoDescriptions: List<String> = emptyList(),
    todoBlockNotes: String? = null,
    startTimeString: String? = null,
    endTimeString: String? = null,
    durationString: String,
    elevate: Boolean = false,
    onClick: () -> Unit
) {
    TimeStampsWrapper(
        startTime = startTimeString,
        endTime = endTimeString,
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors =
                if (elevate) CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
                else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                if (todoCategories.isNotEmpty())
                    CategoriesFlowRow(
                        todoCategories = todoCategories,
                        modifier = Modifier.wrapContentHeight()
                    )

                if (todoDescriptions.isNotEmpty())
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = appendDotsToStrings(todoDescriptions, separator = "\n")
                    )

                if (todoBlockNotes != null && todoBlockNotes.isNotEmpty())
                    Text(
                        text = todoBlockNotes,
                        modifier = Modifier.padding(8.dp)
                    )

                Text(
                    text = durationString,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun TimeStampsWrapper(
    modifier: Modifier = Modifier,
    startTime: String?,
    endTime: String?,
    content: @Composable () -> Unit
) {
    ConstraintLayout(modifier = modifier) {
        val (startTimeTextRef, endTimeRef, contentRef) = createRefs()

        Row(modifier = Modifier.constrainAs(contentRef) {
            start.linkTo(startTimeTextRef.end, margin = 8.dp)
            end.linkTo(parent.end)
            top.linkTo(startTimeTextRef.bottom)
            bottom.linkTo(endTimeRef.top)
            width = Dimension.fillToConstraints
        }) {
            content()
        }

        if (startTime != null)
            Text(
                modifier = Modifier.constrainAs(startTimeTextRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
                text = startTime
            )

        if (endTime == null) {
            HorizontalDivider(
                modifier = Modifier
                    .size(0.dp)
                    .constrainAs(endTimeRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        } else {
            Text(
                modifier = Modifier.constrainAs(endTimeRef) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                },
                text = endTime
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesFlowRow(
    modifier: Modifier = Modifier,
    todoCategories: List<TodoCategory>
) {
    FlowRow(modifier = modifier) {
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
            todoBlockNotes = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam",
            startTimeString = "14.00",
            endTimeString = "16.30",
            durationString = "2 Std 30 min",
            onClick = {}
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
            todoBlockNotes = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam",
            startTimeString = "14.00",
            endTimeString = "16.30",
            durationString = "2 Std 30 min",
            onClick = {}
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
            todoBlockNotes = "",
            startTimeString = "14.00",
            endTimeString = "16.30",
            durationString = "2 Std 30 min",
            onClick = {}
        )
    }
}

@Preview
@Composable
fun ScheduleBlockItemPreviewWithoutEndTime() {
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
            todoBlockNotes = "",
            startTimeString = "14.00",
            endTimeString = null,
            durationString = "2 Std 30 min",
            onClick = {}
        )
    }
}
