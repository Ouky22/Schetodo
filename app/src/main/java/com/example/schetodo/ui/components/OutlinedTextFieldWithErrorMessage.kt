package com.example.schetodo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTextFieldWithErrorMessage(
    modifier: Modifier = Modifier,
    nameInput: String,
    onInputValueChange: (newValue: String) -> Unit,
    labelText: String,
    errorText: String,
    showError: Boolean,
    singleLine: Boolean
) {
    OutlinedTextField(
        modifier = modifier,
        value = nameInput,
        onValueChange = onInputValueChange,
        label = { Text(labelText) },
        singleLine = singleLine,
        isError = showError,
        trailingIcon = {
            if (showError)
                Icon(
                    Icons.Filled.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
        },
        supportingText = {
            if (showError)
                Text(
                    errorText,
                    color = MaterialTheme.colorScheme.error
                )
        }
    )
}