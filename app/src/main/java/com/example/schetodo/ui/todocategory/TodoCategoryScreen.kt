package com.example.schetodo.ui.todocategory

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
fun TodoCategoryScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(5) { index ->
            CategoryItem(backgroundColor = Color.Cyan, text = "Category $index")
        }
    }
}

@Preview
@Composable
fun TodoCategoryScreenPreview() {
    SchetodoTheme {
        TodoCategoryScreen(modifier = Modifier.fillMaxSize())
    }
}