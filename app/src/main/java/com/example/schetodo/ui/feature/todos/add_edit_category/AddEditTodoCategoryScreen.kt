package com.example.schetodo.ui.feature.todos.add_edit_category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schetodo.R
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.ui.components.ElementPickerDialog
import com.example.schetodo.ui.components.PositiveNegativeButtonRow
import com.example.schetodo.ui.components.AddEditTopBar
import com.example.schetodo.ui.feature.todos.*
import com.example.schetodo.ui.theme.SchetodoTheme
import com.example.schetodo.ui.util.pushOntoPreviousBackStackEntry

const val ID_OF_TODO_CATEGORY_MARKED_FOR_DELETION = "deleted_todo_category_id"

@ExperimentalMaterial3Api
@Composable
fun AddEditTodoCategoryScreen(
    modifier: Modifier = Modifier,
    schetodoAppState: SchetodoAppState,
    viewModel: AddEditTodoCategoryViewModel
) {
    val keyBoardController = LocalFocusManager.current
    LaunchedEffect(key1 = true) {
        viewModel.closeAddEditTodoCategoryScreen.collect { closeScreen ->
            if (closeScreen) {
                keyBoardController.clearFocus()
                schetodoAppState.navController.popBackStack()
            }
        }
    }

    AddEditTodoCategoryScreen(
        modifier = modifier,
        todoCategoryName = viewModel.todoCategoryName,
        todoCategoryColor = Color(viewModel.todoCategoryColor),
        todoCategoryIcon = getIconByName(viewModel.todoCategoryIconName) ?: Icons.Filled.Category,
        inEditingMode = viewModel.inEditingMode,
        showInvalidTodoCategoryNameError = viewModel.showInvalidTodoCategoryNameError,
        showColorPicker = viewModel.showColorPicker,
        showIconPicker = viewModel.showIconPicker,
        onTodoCategoryNameChanged = { newName ->
            viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryName(newName))
        },
        onColorSelected = { newColor ->
            viewModel.onEvent(
                AddEditTodoCategoryEvent.ChangeTodoCategoryColor(newColor.toArgb().toLong())
            )
        },
        onIconSelected = { newIconName ->
            viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryIcon(newIconName.name))
        },
        onCloseDialog = {
            keyBoardController.clearFocus()
            schetodoAppState.navController.popBackStack()
        },
        onSaveClicked = { viewModel.onEvent(AddEditTodoCategoryEvent.SaveTodoCategory) },
        onDeleteClick = {
            schetodoAppState.navController.pushOntoPreviousBackStackEntry(
                ID_OF_TODO_CATEGORY_MARKED_FOR_DELETION, viewModel.todoCategoryId
            )
            viewModel.onEvent(AddEditTodoCategoryEvent.MarkTodoCategoryForDeletion)
        },
        onIconSelectionClick = { viewModel.onEvent(AddEditTodoCategoryEvent.ShowIconPicker) },
        onColorSelectionClick = { viewModel.onEvent(AddEditTodoCategoryEvent.ShowColorPicker) }
    )
}

@ExperimentalMaterial3Api
@Composable
fun AddEditTodoCategoryScreen(
    modifier: Modifier = Modifier,
    todoCategoryName: String,
    todoCategoryColor: Color,
    todoCategoryIcon: ImageVector,
    inEditingMode: Boolean,
    showInvalidTodoCategoryNameError: Boolean,
    showColorPicker: Boolean,
    showIconPicker: Boolean,
    onTodoCategoryNameChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onDeleteClick: () -> Unit,
    onCloseDialog: () -> Unit,
    onColorSelectionClick: () -> Unit,
    onColorSelected: (Color) -> Unit,
    onIconSelectionClick: () -> Unit,
    onIconSelected: (ImageVector) -> Unit
) {
    val topAppBarTitle =
        if (inEditingMode) stringResource(R.string.edit_todo_category)
        else stringResource(R.string.add_todo_category)

    Scaffold(
        topBar = {
            AddEditTopBar(
                title = topAppBarTitle,
                showDeleteIconButton = inEditingMode,
                onDeleteClick = onDeleteClick,
                onCloseDialog = onCloseDialog
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
            OutlinedTextField(
                value = todoCategoryName,
                onValueChange = onTodoCategoryNameChanged,
                label = { Text(stringResource(R.string.todoCategoryName)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = showInvalidTodoCategoryNameError,
                supportingText = {
                    if (showInvalidTodoCategoryNameError)
                        Text(
                            "Please enter a name",
                            color = Color.Red
                        )
                }
            )

            SelectColorAndIconArea(
                todoCategoryIcon = todoCategoryIcon,
                todoCategoryColor = todoCategoryColor,
                onIconSelectionClick = onIconSelectionClick,
                onColorSelectionClick = onColorSelectionClick,
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
            )

            PositiveNegativeButtonRow(
                positiveButtonText = if (inEditingMode) stringResource(id = R.string.save)
                else stringResource(R.string.add),
                negativeButtonText = stringResource(id = R.string.cancel),
                onPositiveClick = { onSaveClicked() },
                onNegativeClick = { onCloseDialog() },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showColorPicker)
            ColorPicker(
                modifier = Modifier.fillMaxHeight(0.8f),
                onSelectColor = { selectedColor -> onColorSelected(selectedColor) },
                onDismiss = { onColorSelected(todoCategoryColor) }
            )
        else if (showIconPicker) {
            IconPicker(
                modifier = Modifier.fillMaxHeight(0.8f),
                onSelectIcon = onIconSelected,
                onDismiss = { onIconSelected(todoCategoryIcon) }
            )
        }
    }
}

@Composable
fun SelectColorAndIconArea(
    todoCategoryIcon: ImageVector,
    todoCategoryColor: Color,
    onIconSelectionClick: () -> Unit,
    onColorSelectionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectorCircle(
            icon = todoCategoryIcon,
            modifier = Modifier.fillMaxHeight(),
            contentDescription = stringResource(R.string.choose_icon),
            onClick = { onIconSelectionClick() }
        )
        SelectorCircle(
            color = todoCategoryColor,
            icon = Icons.Outlined.Palette,
            modifier = Modifier.fillMaxHeight(),
            contentDescription = stringResource(R.string.choose_color),
            onClick = { onColorSelectionClick() }
        )
    }
}

@Composable
fun SelectorCircle(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    icon: ImageVector?,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .aspectRatio(1f)
            .background(color)
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = CircleShape
            ),
    ) {
        if (icon != null)
            Icon(
                modifier = Modifier
                    .fillMaxSize(0.7f)
                    .align(Alignment.Center),
                imageVector = icon,
                contentDescription = contentDescription
            )
    }
}

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    onSelectColor: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    ElementPickerDialog(
        modifier = modifier,
        title = stringResource(id = R.string.todo_category_color),
        elements = todoCategoryColors,
        onDismiss = onDismiss,
    ) { color ->
        SelectorCircle(
            color = color,
            icon = null,
            contentDescription = color.toString(),
            onClick = { onSelectColor(color) }
        )
    }
}

@Composable
fun IconPicker(
    modifier: Modifier = Modifier,
    onSelectIcon: (ImageVector) -> Unit,
    onDismiss: () -> Unit
) {
    ElementPickerDialog(
        modifier = modifier,
        title = stringResource(R.string.todo_category_icon),
        elements = getIconsWithCategorySpacer(),
        onDismiss = onDismiss,
        span = { item ->
            if (item === spacerIcon) GridItemSpan(maxLineSpan)
            else GridItemSpan(1)
        }
    ) { icon ->
        if (icon === spacerIcon)
            Spacer(modifier = Modifier.size(50.dp))
        else
            SelectorCircle(
                icon = icon,
                contentDescription = icon.name,
                onClick = { onSelectIcon(icon) }
            )
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun AddEditTodoCategoryDialogPreview() {
    SchetodoTheme {
        AddEditTodoCategoryScreen(
            modifier = Modifier.height(500.dp),
            todoCategoryName = "My TodoCategory Name",
            todoCategoryColor = Color(0xff6096B4),
            todoCategoryIcon = Icons.Filled.House,
            inEditingMode = true,
            showInvalidTodoCategoryNameError = false,
            showColorPicker = false,
            showIconPicker = false,
            onTodoCategoryNameChanged = {},
            onIconSelected = {},
            onColorSelected = {},
            onCloseDialog = {},
            onSaveClicked = {},
            onDeleteClick = {},
            onIconSelectionClick = {},
            onColorSelectionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IconPickerPreview() {
    SchetodoTheme {
        IconPicker(
            modifier = Modifier.height(400.dp),
            onSelectIcon = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ColorPickerPreview() {
    SchetodoTheme {
        ColorPicker(
            modifier = Modifier.height(400.dp),
            onSelectColor = {},
            onDismiss = {}
        )
    }
}