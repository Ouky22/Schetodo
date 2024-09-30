package com.example.schetodo.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.schetodo.R
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.ui.components.SubDestinationTopAppBar
import com.example.schetodo.ui.theme.SchetodoTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    schetodoAppState: SchetodoAppState,
) {
    SettingsScreen(
        modifier = modifier,
        onBackButtonClick = {
            schetodoAppState.navController.popBackStack()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            SubDestinationTopAppBar(
                title = stringResource(id = R.string.settings),
                showBackButton = true,
                onBackButtonClick = onBackButtonClick,
            )
        },
    ) { contentPadding ->
        Column(
            modifier = modifier.padding(contentPadding),
        ) {
            Text(
                text = "Manual backup",
            )

            Button(
                onClick = {
                    // TODO
                },
            ) {
                Column {
                    Text(
                        text = "Backup directory",
                    )
                    Text(
                        text = "Backups will be saved to: ", // TODO
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun SettingsScreenPreview() {
    SchetodoTheme {
        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            onBackButtonClick = {},
        )
    }
}
