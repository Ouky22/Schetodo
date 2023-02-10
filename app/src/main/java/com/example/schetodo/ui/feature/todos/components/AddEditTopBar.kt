package com.example.schetodo.ui.feature.todos.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.schetodo.R
import com.example.schetodo.ui.components.SchetodoTopAppBar


@ExperimentalMaterial3Api
@Composable
fun AddEditTopBar(
    modifier: Modifier = Modifier,
    title: String,
    showDeleteIconButton: Boolean,
    onCloseDialog: () -> Unit,
    onDeleteClick: () -> Unit
) {
    SchetodoTopAppBar(
        modifier = modifier,
        title = title,
        showBackButton = true,
        onBackButtonClick = { onCloseDialog() },
        actions = {
            if (showDeleteIconButton)
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.delete)
                    )
                }
        }
    )
}
