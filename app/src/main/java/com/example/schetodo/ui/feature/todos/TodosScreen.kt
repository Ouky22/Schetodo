package com.example.schetodo.ui.feature.todos

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.ui.components.TopAppBar

@ExperimentalMaterial3Api
@ExperimentalLifecycleComposeApi
@Composable
fun TodosScreen(
    modifier: Modifier = Modifier,
    onCheckOffCompletedTodos: () -> Unit = {},
    viewModel: TodosViewModel
) {
    val state by viewModel.todosState.collectAsStateWithLifecycle()

    if (state.currentCategoryIsChildCategory)
        BackHandler(onBack = { viewModel.onEvent(TodosEvent.NavigateToPreviousTodoCategory) })

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = state.currentCategory?.name ?: stringResource(id = R.string.todos),
            showBackButton = state.currentCategoryIsChildCategory,
            onBackButtonClick = {
                viewModel.onEvent(TodosEvent.NavigateToPreviousTodoCategory)
            }
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.childCategories) { todoCategory ->
                CategoryItem(
                    modifier = Modifier
                        .height(125.dp)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    todoCategory = todoCategory,
                    onItemClick = {
                        viewModel.onEvent(
                            TodosEvent.NavigateToNewTodoCategory(todoCategory.categoryId)
                        )
                    }
                )
            }
            items(state.todos) { todo ->
                TodoItem(
                    modifier = Modifier
                        .height(125.dp)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    todo = todo,
                    onItemClick = {}
                )
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .padding(16.dp),
            onClick = onCheckOffCompletedTodos,
        ) {
            Icon(
                imageVector = Icons.Outlined.Checklist,
                contentDescription = null,
                Modifier.padding(end = 10.dp)
            )
            Text(text = stringResource(id = R.string.check_off_todos))
        }
    }
}