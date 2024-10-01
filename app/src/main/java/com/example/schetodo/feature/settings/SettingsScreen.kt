package com.example.schetodo.feature.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val state by viewModel.settingsState.collectAsStateWithLifecycle()

    SettingsScreen(
        modifier = modifier,
        selectedOfflineBackupUri = state.selectedUri ?: Uri.EMPTY,
        onBackButtonClick = {
            schetodoAppState.navController.popBackStack()
        },
        onOfflineBackupUriSelected = { uri ->
            viewModel.onEvent(SettingsEvent.SetOfflineBackupUri(uri))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    selectedOfflineBackupUri: Uri,
    onBackButtonClick: () -> Unit,
    onOfflineBackupUriSelected: (uri: Uri) -> Unit,
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) {
                onOfflineBackupUriSelected(uri)
            }
        }

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
                onClick = { launcher.launch(null) },
            ) {
                Column {
                    Text(
                        text = "Backup directory",
                    )
                    Text(
                        text = "Offline backups will be saved to: $selectedOfflineBackupUri",
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
            selectedOfflineBackupUri = Uri.parse("content://com.android.externalstorage.documents/tree/primary"),
            onBackButtonClick = {},
            onOfflineBackupUriSelected = {},
        )
    }
}
