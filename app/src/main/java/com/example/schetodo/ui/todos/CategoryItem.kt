package com.example.schetodo.ui.todos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    color: Color,
    text: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.7f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(color)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(0.6f)
                            .align(Alignment.Center),
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            }

            Column(
                modifier = Modifier.weight(3f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun CategoryItemPreview() {
    SchetodoTheme {
        CategoryItem(
            modifier = Modifier.height(100.dp),
            color = Color.Cyan,
            text = "Household",
            icon = Icons.Filled.House
        )
    }
}