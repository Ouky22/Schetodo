package com.example.schetodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
fun <T> ElementPickerDialog(
    modifier: Modifier = Modifier,
    title: String,
    elements: List<T>,
    onDismiss: () -> Unit,
    span: (LazyGridItemSpanScope.(T) -> GridItemSpan)? = null,
    itemSelector: @Composable (T) -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Card(modifier = modifier) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 50.dp),
                    contentPadding = PaddingValues(32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = elements,
                        span = span
                    ) { element ->
                        itemSelector(element)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ElementPickerDialogPreview() {
    SchetodoTheme {
        ElementPickerDialog(
            title = "Select Element",
            elements = (1..20).toList(),
            onDismiss = { }
        ) { number ->
            Box(
                Modifier
                    .clip(CircleShape)
                    .aspectRatio(1f)
                    .background(Color.Green),
                contentAlignment = Alignment.Center
            ) {
                Text(text = number.toString())
            }
        }
    }
}