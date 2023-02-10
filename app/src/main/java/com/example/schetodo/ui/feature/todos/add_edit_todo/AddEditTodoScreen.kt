package com.example.schetodo.ui.feature.todos.add_edit_todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.schetodo.R
import com.example.schetodo.data.entity.TodoPriority
import com.example.schetodo.ui.components.PositiveNegativeButtonRow
import com.example.schetodo.ui.feature.todos.components.AddEditTopBar
import com.example.schetodo.ui.feature.todos.getTodoPriorityColorOf
import com.example.schetodo.ui.feature.todos.list.CategoryItem
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
@ExperimentalMaterial3Api
fun AddEditTodoScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditTodoViewModel,
    navController: NavController
) {
    AddEditTodoScreen(
        todoCategoryName = "Household",
        todoCategoryIcon = Icons.Filled.House,
        todoCategoryColor = Color(0xff85586F),
        todoDescription = "Clean the Bathroom",
        todoPriority = TodoPriority.MEDIUM,
        isRecurringTodo = false,
        inEditingMode = false,
        onTodoNameChanged = {},
        onTodoPriorityChanged = {},
        onTodoIsRecurringChanged = {}
    )
}

@Composable
@ExperimentalMaterial3Api
fun AddEditTodoScreen(
    modifier: Modifier = Modifier,
    inEditingMode: Boolean,
    todoCategoryName: String,
    todoCategoryColor: Color,
    todoCategoryIcon: ImageVector,
    todoDescription: String,
    todoPriority: TodoPriority,
    isRecurringTodo: Boolean,
    onTodoNameChanged: (String) -> Unit,
    onTodoPriorityChanged: (TodoPriority) -> Unit,
    onTodoIsRecurringChanged: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            AddEditTopBar(
                title = "TODO",
                showDeleteIconButton = inEditingMode,
                onDeleteClick = {},
                onCloseDialog = {}
            )
        }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .padding(contentPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                CategoryItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    todoCategoryName = todoCategoryName,
                    todoCategoryIcon = todoCategoryIcon,
                    todoCategoryColor = todoCategoryColor
                )
                Spacer(modifier = Modifier.size(64.dp))

                TodoPrioritySlider(
                    onTodoPriorityChanged = {},
                    todoPriority = todoPriority
                )
                Spacer(modifier = Modifier.size(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.recurring))
                    Spacer(modifier = Modifier.size(4.dp))
                    Checkbox(
                        checked = isRecurringTodo,
                        onCheckedChange = onTodoIsRecurringChanged
                    )
                }
                Spacer(modifier = Modifier.size(32.dp))

                OutlinedTextField(
                    value = todoDescription,
                    onValueChange = onTodoNameChanged,
                    label = { stringResource(R.string.todo_name) },
                    modifier = Modifier.fillMaxWidth()
                )
            }


            PositiveNegativeButtonRow(
                positiveButtonText =
                if (inEditingMode) stringResource(id = R.string.save)
                else stringResource(id = R.string.add),
                negativeButtonText = stringResource(id = R.string.cancel),
                onPositiveClick = { /*TODO*/ },
                onNegativeClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun TodoPrioritySlider(
    modifier: Modifier = Modifier,
    onTodoPriorityChanged: (TodoPriority) -> Unit,
    todoPriority: TodoPriority
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = todoPriority.priorityNumber.toFloat(),
            steps = 1,
            valueRange = 1f..TodoPriority.values().size.toFloat(),
            onValueChange = { todoPriorityNumber ->
                onTodoPriorityChanged(TodoPriority.getByPriorityNumber(todoPriorityNumber.toInt()))
            },
            colors = SliderDefaults.colors(
                thumbColor = getTodoPriorityColorOf(todoPriority),
                activeTrackColor = getTodoPriorityColorOf(todoPriority)
            )
        )

        val priorityText = when (todoPriority) {
            TodoPriority.LOW -> stringResource(R.string.low_priority)
            TodoPriority.MEDIUM -> stringResource(R.string.medium_priority)
            TodoPriority.HIGH -> stringResource(R.string.high_priority)
            TodoPriority.VERY_HIGH -> stringResource(R.string.very_high_priority)
        }
        Text(text = priorityText)
    }
}

@Preview(showBackground = true)
@Composable
@ExperimentalMaterial3Api
fun AddEditTodoScreenPreview() {
    SchetodoTheme {
        val todoDescription =
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam"

        AddEditTodoScreen(
            modifier = Modifier.fillMaxSize(),
            todoCategoryName = "Household",
            todoCategoryIcon = Icons.Filled.House,
            todoCategoryColor = Color(0xff85586F),
            todoDescription = todoDescription,
            todoPriority = TodoPriority.HIGH,
            isRecurringTodo = false,
            inEditingMode = true,
            onTodoNameChanged = {},
            onTodoPriorityChanged = {},
            onTodoIsRecurringChanged = {}
        )
    }
}