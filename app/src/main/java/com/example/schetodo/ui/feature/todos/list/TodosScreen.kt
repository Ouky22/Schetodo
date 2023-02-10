package com.example.schetodo.ui.feature.todos.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.ui.components.SchetodoTopAppBar
import com.example.schetodo.ui.feature.todos.getIconByName

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@ExperimentalLifecycleComposeApi
@Composable
fun TodosScreen(
    modifier: Modifier = Modifier,
    onCheckOffCompletedTodos: () -> Unit,
    onAddTodoCategory: (parentCategory: Int) -> Unit,
    onEditTodoCategory: (categoryToEdit: Int) -> Unit,
    viewModel: TodosViewModel
) {
    val state by viewModel.todosState.collectAsStateWithLifecycle()

    if (state.currentCategoryIsChildCategory)
        BackHandler(onBack = { viewModel.onEvent(TodosEvent.NavigateToPreviousTodoCategory) })

    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = state.currentCategory?.name ?: stringResource(id = R.string.todos),
                showBackButton = state.currentCategoryIsChildCategory,
                onBackButtonClick = {
                    viewModel.onEvent(TodosEvent.NavigateToPreviousTodoCategory)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddTodoCategory(state.currentCategory?.categoryId ?: -1) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_todo_category)
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.childCategories) { todoCategory ->
                    CategoryItem(
                        modifier = Modifier
                            .height(125.dp)
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .combinedClickable(
                                onClick = {
                                    viewModel.onEvent(
                                        TodosEvent.NavigateToNewTodoCategory(todoCategory.categoryId)
                                    )
                                },
                                onLongClick = {
                                    onEditTodoCategory(todoCategory.categoryId)
                                }
                            ),
                        todoCategoryName = todoCategory.name,
                        todoCategoryColor = Color(todoCategory.color),
                        todoCategoryIcon = getIconByName(todoCategory.iconName)
                            ?: Icons.Filled.Category
                    )
                }
                items(state.todos) { todo ->
                    TodoItem(
                        modifier = Modifier
                            .height(125.dp)
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        todo = todo
                    )
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
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
}