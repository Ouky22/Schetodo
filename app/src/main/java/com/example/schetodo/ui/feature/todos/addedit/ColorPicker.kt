package com.example.schetodo.ui.feature.todos.addedit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.schetodo.ui.feature.todos.todoCategoryColors
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    onSelectColor: (Color) -> Unit
) {
    Dialog(
        onDismissRequest = { /*TODO*/ }
    ) {
        Column(
            modifier = modifier
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 40.dp)
            ) {
                items(todoCategoryColors) { color ->
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .aspectRatio(1f)
                            .background(color)
                            .clickable { onSelectColor(color) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorPickerPreview() {
    SchetodoTheme {
        ColorPicker(
            modifier = Modifier.fillMaxSize(),
            onSelectColor = {}
        )
    }
}