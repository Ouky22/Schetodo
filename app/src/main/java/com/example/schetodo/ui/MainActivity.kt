package com.example.schetodo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.schetodo.ui.components.BottomNavBar
import com.example.schetodo.ui.navigation.*
import com.example.schetodo.ui.navigation.schedule.Schedule
import com.example.schetodo.ui.theme.SchetodoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SchetodoApp()
        }
    }
}

@ExperimentalFoundationApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchetodoApp() {
    SchetodoTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val schetodoAppState = rememberSchetodoAppState(
            navController = navController
        )

        Scaffold(
            bottomBar = {
                if (schetodoAppState.shouldShowBottomNavigation(currentBackStack))
                    BottomNavBar(
                        destinations = bottomNavDestinations,
                        currentDestination = schetodoAppState.getCurrentMainDestination(
                            currentBackStack
                        ) ?: Schedule,
                        onItemClick = { selectedDestination ->
                            schetodoAppState.navController.navigateSingleTopTo(selectedDestination.route)
                        }
                    )
            }
        ) { innerPadding ->
            SchetodoNavHost(
                schetodoAppState = schetodoAppState,
                navController = schetodoAppState.navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
@ExperimentalMaterial3Api
@ExperimentalFoundationApi
fun SchetodoAppPreview() {
    SchetodoTheme {
        SchetodoApp()
    }
}