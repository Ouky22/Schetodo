package com.example.schetodo.ui.feature.todos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schetodo.R
import com.example.schetodo.data.entity.TodoCategory
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
fun TodosScreen(
    modifier: Modifier = Modifier,
    onCheckOffCompletedTodos: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(10) { index ->
                CategoryItem(
                    modifier = Modifier
                        .height(125.dp)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    todoCategory = TodoCategory(index, "Category $index", 0xff799FCB, null),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(0.75f),
            onClick = onCheckOffCompletedTodos,
        ) {
            Icon(
                imageVector = Icons.Outlined.Checklist,
                contentDescription = null,
                Modifier.padding(end = 10.dp)
            )
            Text(text = stringResource(id = R.string.check_off_todos))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodosScreenPreview() {
    SchetodoTheme {
        TodosScreen(modifier = Modifier.fillMaxSize())
    }
}