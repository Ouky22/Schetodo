package com.example.schetodo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
fun PositiveNegativeButtonRow(
    positiveButtonText: String,
    negativeButtonText: String,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onClick = { onNegativeClick() }
        ) {
            Text(text = negativeButtonText)
        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onClick = { onPositiveClick() }
        ) {
            Text(text = positiveButtonText)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PositiveNegativeButtonRowPreview() {
    SchetodoTheme {
        PositiveNegativeButtonRow(
            positiveButtonText = "Save",
            negativeButtonText = "Cancel",
            onPositiveClick = { },
            onNegativeClick = {})
    }
}