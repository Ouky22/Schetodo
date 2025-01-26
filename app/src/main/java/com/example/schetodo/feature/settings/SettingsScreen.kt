package com.example.schetodo.feature.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.schetodo.R
import com.example.schetodo.data.BACKUP_FILE_MIME_TYPE
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
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.snackBarMessages.collect { uiText ->
            snackbarHostState.showSnackbar(uiText.asString(context))
        }
    }

    SettingsScreen(
        modifier = modifier,
        selectedOfflineBackupUri = state.selectedUri ?: Uri.EMPTY,
        onBackButtonClick = {
            schetodoAppState.navController.popBackStack()
        },
        onOfflineBackupUriSelected = { uri ->
            viewModel.onEvent(SettingsEvent.SetOfflineBackupUri(uri))
        },
        onTriggerOfflineBackup = {
            viewModel.onEvent(SettingsEvent.TriggerOfflineBackup)
        },
        onImportBackupFile = { uri ->
            viewModel.onEvent(SettingsEvent.ImportBackupFile(uri))
        },
        onSignInWithGoogleClick = {
            viewModel.onEvent(SettingsEvent.SignInWithGoogle)
        },
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    selectedOfflineBackupUri: Uri,
    onBackButtonClick: () -> Unit,
    onOfflineBackupUriSelected: (uri: Uri) -> Unit,
    onTriggerOfflineBackup: () -> Unit,
    onImportBackupFile: (uri: Uri) -> Unit,
    onSignInWithGoogleClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val selectBackupDirectoryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) {
                onOfflineBackupUriSelected(uri)
            }
        }

    val importBackupFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                onImportBackupFile(uri)
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier = modifier.padding(contentPadding),
        ) {
            SettingsSectionTitle(
                text = stringResource(R.string.manual_offline_backup),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            SettingsButton(
                onClick = { selectBackupDirectoryLauncher.launch(null) },
                settingTitle = stringResource(R.string.select_backup_directory),
                settingDescription = if (selectedOfflineBackupUri != Uri.EMPTY) {
                    stringResource(
                        R.string.offline_backups_will_be_saved_to,
                        selectedOfflineBackupUri
                    )
                } else {
                    stringResource(R.string.currently_no_backup_directory_selected)
                },
            )

            if (selectedOfflineBackupUri != Uri.EMPTY) {
                SettingsButton(
                    onClick = onTriggerOfflineBackup,
                    settingTitle = stringResource(R.string.trigger_backup),
                    settingDescription = stringResource(R.string.create_backup_file_description),
                )
            }

            SettingsButton(
                settingTitle = stringResource(R.string.restore_backup),
                settingDescription = stringResource(R.string.restore_backup_description),
                onClick = {
                    importBackupFileLauncher.launch(arrayOf(BACKUP_FILE_MIME_TYPE))
                },
            )

            SettingsSectionTitle(
                text = stringResource(R.string.online_synchronization),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 24.dp)
            )

            GoogleSignInButton(
                onSignInWithGoogleClick = onSignInWithGoogleClick,
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier.padding(start = 8.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun SettingsButton(
    modifier: Modifier = Modifier,
    settingTitle: String,
    settingDescription: String,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        onClick = onClick,
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = settingTitle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = settingDescription,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}


@Composable
fun GoogleSignInButton(
    onSignInWithGoogleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        onClick = onSignInWithGoogleClick,
        shape = RectangleShape,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google_g),
                contentDescription = null,
                tint = Color.Unspecified,
            )
            Text(
                text = "Sign in with Google",
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SchetodoTheme {
        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            selectedOfflineBackupUri = Uri.parse(
                "content://com.android.externalstorage.documents/tree/primary"
            ),
            onBackButtonClick = {},
            onOfflineBackupUriSelected = {},
            onTriggerOfflineBackup = {},
            onImportBackupFile = {},
            onSignInWithGoogleClick = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
