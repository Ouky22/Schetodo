package com.example.schetodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.feature.todos.getTodoPriorityColorOf
import com.example.schetodo.ui.theme.SchetodoTheme


@Composable
fun TodoCategoryItem(
    modifier: Modifier = Modifier,
    iconBackgroundColor: Color,
    cardBackgroundColor: Color,
    icon: ImageVector,
    text: @Composable () -> Unit,
    endSideContent: @Composable () -> Unit = {},
    alignEndSideContentToEnd: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(iconBackgroundColor)
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(0.6f)
                            .align(Alignment.Center),
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.scrim
                    )
                }
            }

            Column(modifier = Modifier.weight(weight = 1f, fill = alignEndSideContentToEnd)) {
                text()
            }

            Column(
                modifier = Modifier.padding(start = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                endSideContent()
            }
        }
    }
}

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    todoCategoryName: String,
    todoCategoryColor: Color,
    todoCategoryIcon: ImageVector,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    endSideContent: @Composable () -> Unit = {},
    alignEndSideContentToEnd: Boolean = false
) {
    TodoCategoryItem(
        modifier = modifier,
        cardBackgroundColor = todoCategoryColor.copy(alpha = 0.2f),
        iconBackgroundColor = todoCategoryColor,
        icon = todoCategoryIcon,
        text = {
            Text(
                text = todoCategoryName,
                style = textStyle,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        },
        endSideContent = endSideContent,
        alignEndSideContentToEnd = alignEndSideContentToEnd
    )
}

@Composable
fun TodoItem(
    modifier: Modifier = Modifier,
    todo: Todo,
    endSideContent: @Composable () -> Unit = {},
    alignEndSideContentToEnd: Boolean = false
) {
    TodoCategoryItem(
        modifier = modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onBackground,
            shape = RoundedCornerShape(10.dp)
        ),
        icon = if (todo.flag == TodoFlag.RECURRING) Icons.Outlined.RestartAlt else Icons.Outlined.TaskAlt,
        cardBackgroundColor = MaterialTheme.colorScheme.surface,
        iconBackgroundColor = getTodoPriorityColorOf(todo.priority),
        text = {
            Text(
                text = todo.description,
                textAlign = TextAlign.Start,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 4.dp)
            )
        },
        endSideContent = endSideContent,
        alignEndSideContentToEnd = alignEndSideContentToEnd
    )
}

@Preview
@Composable
fun TodoItem() {
    SchetodoTheme {
        TodoItem(
            modifier = Modifier.height(100.dp),
            todo = Todo(
                0,
                "Lorem ipsum dolor sit at,  voluptua. At vero eos et et justo duo dolores et ea rebum. Stet clita kasd",
                TodoPriority.HIGH,
                TodoFlag.UNDONE,
                0
            )
        )
    }
}

@Preview
@Composable
fun CategoryItemPreview() {
    SchetodoTheme {
        CategoryItem(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            todoCategoryName = "Household",
            todoCategoryColor = Color(0xff799FCB),
            todoCategoryIcon = Icons.Filled.House
        )
    }
}