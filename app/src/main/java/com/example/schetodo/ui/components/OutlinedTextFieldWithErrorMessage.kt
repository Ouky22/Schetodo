package com.example.schetodo.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTextFieldWithErrorMessage(
    nameInput: String,
    onInputValueChange: (newValue: String) -> Unit,
    labelText: String,
    errorText: String,
    showError: Boolean,
    singleLine: Boolean
) {
    OutlinedTextField(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
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