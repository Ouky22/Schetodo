package com.example.schetodo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.schetodo.ui.navigation.SchetodoNavHost
import com.example.schetodo.ui.theme.SchetodoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchetodoApp()
        }
    }
}

@Composable
@ExperimentalMaterial3Api
fun SchetodoApp() {
    SchetodoTheme {
        val navController = rememberNavController()

        Scaffold { innerPadding ->
            SchetodoNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
@ExperimentalMaterial3Api
fun SchetodoAppPreview() {
    SchetodoTheme {
        SchetodoApp()
    }
}