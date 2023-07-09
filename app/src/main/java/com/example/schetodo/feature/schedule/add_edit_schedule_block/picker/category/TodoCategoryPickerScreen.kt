package com.example.schetodo.feature.schedule.add_edit_schedule_block.picker.category

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.schetodo.R
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.feature.schedule.add_edit_schedule_block.picker.PickerScreen
import com.example.schetodo.feature.todos.getIconByName
import com.example.schetodo.feature.todos.todoCategoryColors
import com.example.schetodo.ui.components.CategoryItem
import com.example.schetodo.ui.theme.SchetodoTheme
import kotlinx.coroutines.launch


const val TODO_CATEGORY_PICKER_RESULT = "todo_category_picker_result"


@Composable
fun TodoCategoryPickerScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoCategoryPickerViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.currentCategoryIsChildCategory)
        BackHandler { viewModel.navigateToPreviousCategory() }

    PickerScreen(
        modifier = modifier,
        topAppBarTitle = state.currentCategory?.name
            ?: stringResource(R.string.select_todo_category),
        showTopBarBackButton = state.showTopBarBackButton,
        onTopBarBackButtonClick = { viewModel.navigateToPreviousCategory() },
        selectedItemCount = state.selectedItems.count(),
        onAdd = {
            navController.previousBackStackEntry?.savedStateHandle?.apply {
                set(TODO_CATEGORY_PICKER_RESULT, state.selectedItems.map { it.categoryId })
            }
            navController.popBackStack()
        },
        onCancel = { navController.popBackStack() }
    ) {
        TodoCategoryPickerList(
            todoCategories = state.childCategories,
            selectedTodoCategories = state.selectedItems,
            onMarkTodoCategoryForSelection = { viewModel.markItemForSelection(it) },
            onUndoMarkTodoCategoryForSelection = { viewModel.undoMarkItemForSelection(it) },
            onClickOnTodoCategory = { viewModel.navigateToTodoCategory(it.categoryId) }
        )
    }
}

@Composable
fun TodoCategoryPickerList(
    todoCategories: List<TodoCategory>,
    selectedTodoCategories: List<TodoCategory>,
    onMarkTodoCategoryForSelection: (TodoCategory) -> Unit,
    onUndoMarkTodoCategoryForSelection: (TodoCategory) -> Unit,
    onClickOnTodoCategory: (TodoCategory) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(state = listState) {
        items(todoCategories) { todoCategory ->
            CategoryItem(
                todoCategoryName = todoCategory.name,
                todoCategoryColor = Color(todoCategory.color),
                todoCategoryIcon = getIconByName(todoCategory.iconName) ?: Icons.Filled.Category,
                modifier = Modifier
                    .height(125.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .clickable {
                        onClickOnTodoCategory(todoCategory)
                        coroutineScope.launch { listState.scrollToItem(0) }
                    },
                endSideContent = {
                    Checkbox(
                        checked = todoCategory in selectedTodoCategories,
                        onCheckedChange = { selected ->
                            if (selected) onMarkTodoCategoryForSelection(todoCategory)
                            else onUndoMarkTodoCategoryForSelection(todoCategory)
                        }
                    )
                },
                alignEndSideContentToEnd = true
            )
        }
    }
}

@Preview
@Composable
fun TodoCategoryPickerScreen() {
    val categories = listOf(
        TodoCategory(
            2, "Study", todoCategoryColors[8].toArgb().toLong(),
            null, Icons.Filled.School.name
        ),
        TodoCategory(
            1, "Sport", todoCategoryColors[2].toArgb().toLong(),
            null, Icons.Filled.House.name
        )
    )

    SchetodoTheme {
        PickerScreen(
            topAppBarTitle = "Category Name",
            showTopBarBackButton = true,
            onTopBarBackButtonClick = {},
            selectedItemCount = 1,
            onAdd = {},
            onCancel = {}
        ) {
            TodoCategoryPickerList(
                todoCategories = categories,
                selectedTodoCategories = categories.subList(0, 1),
                onMarkTodoCategoryForSelection = {},
                onUndoMarkTodoCategoryForSelection = {},
                onClickOnTodoCategory = {}
            )
        }
    }
}