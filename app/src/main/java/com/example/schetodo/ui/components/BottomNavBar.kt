package com.example.schetodo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.schetodo.ui.navigation.SchetodoDestination

@Composable
fun BottomNavBar(
    destinations: List<SchetodoDestination>,
    currentDestination: SchetodoDestination,
    onItemClick: (SchetodoDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        modifier = modifier,
        elevation = 5.dp,
    ) {
        destinations.forEach { destination ->
            val selected = destination == currentDestination

            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(destination) },
                icon = { BottomNavIconWithLabel(destination = destination, selected = selected) }
            )
        }
    }
}

@Composable
fun BottomNavIconWithLabel(
    modifier: Modifier = Modifier,
    destination: SchetodoDestination,
    selected: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = stringResource(id = destination.titleResourceId)
        )
        if (selected)
            Text(text = stringResource(id = destination.titleResourceId))
    }
}