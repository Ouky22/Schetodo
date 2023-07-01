package com.example.schetodo.ui.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult


suspend fun SnackbarHostState.showSnackbarWithActionHandler(
    message: String,
    actionLabel: String,
    onActionPerformed: () -> Unit = {},
    onDismissed: () -> Unit = {},
    withDismissAction: Boolean = false,
    snackbarDuration: SnackbarDuration = SnackbarDuration.Short
) {
    val result = showSnackbar(
        message = message,
        actionLabel = actionLabel,
        withDismissAction = withDismissAction,
        duration = snackbarDuration
    )

    when (result) {
        SnackbarResult.ActionPerformed -> onActionPerformed()
        SnackbarResult.Dismissed -> onDismissed()
    }
}