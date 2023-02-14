package com.example.schetodo.ui.navigation.schedule

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import com.example.schetodo.R
import com.example.schetodo.ui.navigation.MainSchetodoDestination


object Schedule : MainSchetodoDestination {
    override val icon = Icons.Outlined.Schedule
    override val route = "schedule"
    override val titleResourceId = R.string.schedule
}