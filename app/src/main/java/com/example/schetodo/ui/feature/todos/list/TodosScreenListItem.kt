package com.example.schetodo.ui.feature.todos.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schetodo.data.entity.Todo
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.data.entity.TodoFlag
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.theme.SchetodoTheme


@Composable
fun TodosScreenListItem(
    modifier: Modifier = Modifier,
    iconBackgroundColor: Color,
    cardBackgroundColor: Color,
    icon: ImageVector,
    text: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.7f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(iconBackgroundColor)
                        .align(Alignment.CenterHorizontally)
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

            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                text()
            }
        }
    }
}

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    todoCategory: TodoCategory
) {
    TodosScreenListItem(
        modifier = modifier,
        cardBackgroundColor = Color(todoCategory.color).copy(alpha = 0.2f),
        iconBackgroundColor = Color(todoCategory.color),
        icon = getIconByName(todoCategory.iconName) ?: Icons.Filled.Category,
        text = {
            Text(
                text = todoCategory.name,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

@Composable
fun TodoItem(
    modifier: Modifier = Modifier,
    todo: Todo
) {
    val iconBackgroundColor = when (todo.priority) {
        TodoPriority.LOW -> Color(0xFFDAE3D9)
        TodoPriority.MEDIUM -> Color(0xFFFFFAAE)
        TodoPriority.HIGH -> Color(0xFFFFC29F)
        TodoPriority.VERY_HIGH -> Color(0xFFF9665E)
    }

    TodosScreenListItem(
        modifier = modifier,
        icon = if (todo.flag == TodoFlag.RECURRING) Icons.Outlined.RestartAlt else Icons.Outlined.TaskAlt,
        cardBackgroundColor = Color.LightGray,
        iconBackgroundColor = iconBackgroundColor,
        text = {
            Text(
                text = todo.description,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
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
            modifier = Modifier.height(100.dp),
            todoCategory = TodoCategory(0, "Household", 0xff799FCB, null, Icons.Filled.House.name)
        )
    }
}

