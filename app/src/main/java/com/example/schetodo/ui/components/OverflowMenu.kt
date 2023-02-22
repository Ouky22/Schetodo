package com.example.schetodo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun OverflowMenu(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        var expandOverflowMenu by remember { mutableStateOf(false) }

        IconButton(onClick = { expandOverflowMenu = !expandOverflowMenu }) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription
            )
        }
        DropdownMenu(
            expanded = expandOverflowMenu,
            onDismissRequest = { expandOverflowMenu = false }
        ) {
            content()
        }
    }
}