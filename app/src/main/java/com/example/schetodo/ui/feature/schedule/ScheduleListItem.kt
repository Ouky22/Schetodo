package com.example.schetodo.ui.feature.schedule

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schetodo.data.entity.Todo
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.feature.todos.list.CategoryItem
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleListItem(
    modifier: Modifier = Modifier,
    todoCategories: List<TodoCategory>,
    todoDescriptions: List<String>,
    todoBlocKNotes: String,
    startTimeString: String,
    endTimeString: String,
    durationString: String
) {
    Card(
        modifier = modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onBackground,
            shape = RoundedCornerShape(5.dp)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
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
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = buildAnnotatedString {
                    todoDescriptions.forEach {
                        withStyle(style = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))) {
                            append("\u2022")
                            append("\t\t")
                            append(it)
                        }
                    }
                }
            )
            Text(
                text = todoBlocKNotes,
                modifier = Modifier.padding(8.dp)
            )
            Divider(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "$startTimeString - $endTimeString")
                Text(text = durationString)
            }
        }
    }
}

@Preview
@Composable
fun ScheduleListItemPreview() {
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

    SchetodoTheme {
        ScheduleListItem(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            todoCategories = todoCategories,
            todoDescriptions = todos.map { it.description },
            todoBlocKNotes = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam",
            startTimeString = "14.00",
            endTimeString = "16.30",
            durationString = "2 Std 30 min"
        )
    }
}