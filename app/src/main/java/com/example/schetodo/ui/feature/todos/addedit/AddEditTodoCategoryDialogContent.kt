package com.example.schetodo.ui.feature.todos.addedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.schetodo.R
import com.example.schetodo.ui.feature.todos.getIconByName
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
fun AddEditTodoCategoryDialogContent(
    modifier: Modifier = Modifier,
    viewModel: AddEditTodoCategoryViewModel,
    onCloseDialog: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.todoCategorySuccessfullySaved.collect { successfullySaved ->
            if (successfullySaved)
                onCloseDialog()
        }
    }

    AddEditTodoCategoryDialogContent(
        modifier = modifier,
        todoCategoryName = viewModel.todoCategoryName,
        todoCategoryColor = Color(viewModel.todoCategoryColor),
        todoCategoryIcon = getIconByName(viewModel.todoCategoryIconName) ?: Icons.Filled.Category,
        inEditingMode = viewModel.inEditingMode,
        showInvalidTodoCategoryNameError = viewModel.showInvalidTodoCategoryNameError,
        onTodoCategoryNameChanged = { newName ->
            viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryName(newName))
        },
        onTodoCategoryColorChanged = { newColor ->
            viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryColor(newColor))
        },
        onTodoCategoryIconChanged = { newIconName ->
            viewModel.onEvent(AddEditTodoCategoryEvent.ChangeTodoCategoryIcon(newIconName))
        },
        onCloseDialog = { onCloseDialog() },
        onSaveClicked = { viewModel.onEvent(AddEditTodoCategoryEvent.SaveTodoCategory) }
    )
}

@Composable
fun AddEditTodoCategoryDialogContent(
    modifier: Modifier = Modifier,
    todoCategoryName: String,
    todoCategoryColor: Color,
    todoCategoryIcon: ImageVector,
    inEditingMode: Boolean,
    showInvalidTodoCategoryNameError: Boolean,
    onTodoCategoryNameChanged: (String) -> Unit,
    onTodoCategoryColorChanged: (Long) -> Unit,
    onTodoCategoryIconChanged: (String) -> Unit,
    onCloseDialog: () -> Unit,
    onSaveClicked: () -> Unit
) {
    Surface {
        Column(
            modifier = modifier
                .wrapContentHeight()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                OutlinedTextField(
                    value = todoCategoryName,
                    onValueChange = onTodoCategoryNameChanged,
                    label = { Text(stringResource(R.string.todoCategoryName)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = showInvalidTodoCategoryNameError
                )
                if (showInvalidTodoCategoryNameError)
                    Text(
                        "Please enter a name",
                        color = Color.Red
                    )
            }
            Spacer(modifier = Modifier.size(64.dp))
            Row(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectIcon(
                    color = Color.White,
                    icon = todoCategoryIcon,
                    modifier = Modifier
                        .fillMaxHeight()
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = CircleShape
                        ),
                    contentDescription = stringResource(R.string.choose_icon),
                    onClick = {
                        // TODO open icon picker
                        val newIcon = Icons.Filled.House.name
                        onTodoCategoryIconChanged(newIcon)
                    }
                )
                SelectIcon(
                    color = todoCategoryColor,
                    icon = Icons.Outlined.Palette,
                    modifier = Modifier.fillMaxHeight(),
                    contentDescription = stringResource(R.string.choose_color),

                    onClick = {
                        // TODO open color picker
                        val newColor = Color.Red.toArgb()
                        onTodoCategoryColorChanged(newColor.toLong())
                    }
                )
            }
            Spacer(modifier = Modifier.size(64.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onClick = { onCloseDialog() }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onClick = { onSaveClicked() }
                ) {
                    if (inEditingMode)
                        Text(text = stringResource(id = R.string.save))
                    else
                        Text(text = stringResource(R.string.add))
                }
            }
        }
    }
}

@Composable
fun SelectIcon(
    modifier: Modifier = Modifier,
    color: Color,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .aspectRatio(1f)
            .background(color)
            .clickable { onClick() }
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(0.7f)
                .align(Alignment.Center),
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditTodoCategoryDialogPreview() {
    SchetodoTheme {
        AddEditTodoCategoryDialogContent(
            modifier = Modifier.height(500.dp),
            todoCategoryName = "My TodoCategory Name",
            todoCategoryColor = Color(0xff6096B4),
            todoCategoryIcon = Icons.Filled.House,
            inEditingMode = false,
            showInvalidTodoCategoryNameError = false,
            onTodoCategoryNameChanged = {},
            onTodoCategoryIconChanged = {},
            onTodoCategoryColorChanged = {},
            onCloseDialog = {},
            onSaveClicked = {}
        )
    }
}