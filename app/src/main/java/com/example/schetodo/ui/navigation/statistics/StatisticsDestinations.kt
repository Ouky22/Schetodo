package com.example.schetodo.ui.navigation.statistics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import com.example.schetodo.R
import com.example.schetodo.ui.navigation.MainSchetodoDestination


object Statistics : MainSchetodoDestination {
    override val icon = Icons.Outlined.PieChart
    override val route = "statistics"
    override val titleResourceId = R.string.statistics
}