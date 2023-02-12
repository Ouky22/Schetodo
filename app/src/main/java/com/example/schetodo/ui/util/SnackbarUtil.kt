package com.example.schetodo.ui.util

import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult

suspend fun SnackbarHostState.showSnackbarWithActionHandler(
    message: String,
    actionLabel: String,
    onActionPerformed: () -> Unit = {},
    onDismissed: () -> Unit = {}
) {
    val result = showSnackbar(
        message = message,
        actionLabel = actionLabel
    )

    when (result) {
        SnackbarResult.ActionPerformed -> onActionPerformed()
        SnackbarResult.Dismissed -> onDismissed()
    }
}