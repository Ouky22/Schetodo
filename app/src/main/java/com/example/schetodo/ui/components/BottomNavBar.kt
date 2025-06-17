package com.example.schetodo.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.schetodo.ui.navigation.MainSchetodoDestination
import com.example.schetodo.ui.navigation.SchetodoDestination

@Composable
fun BottomNavBar(
    destinations: List<MainSchetodoDestination>,
    currentDestination: MainSchetodoDestination,
    onItemClick: (SchetodoDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            val selected = destination == currentDestination

            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(id = destination.titleResourceId)
                    )
                },
                label = { Text(text = stringResource(id = destination.titleResourceId)) },
                alwaysShowLabel = false
            )
        }
    }
}
