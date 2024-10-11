package com.example.schetodo.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.R
import com.example.schetodo.feature.dbbackup.DatabaseBackupExporter
import com.example.schetodo.feature.dbbackup.DatabaseBackupImporter
import com.example.schetodo.feature.settings.SettingsEvent.SetOfflineBackupUri
import com.example.schetodo.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val databaseBackupExporter: DatabaseBackupExporter,
    private val databaseBackupImporter: DatabaseBackupImporter,
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState>
        get() = _settingsState.asStateFlow()

    private val _snackBarMessages = MutableSharedFlow<UiText>()
    val snackBarMessages: SharedFlow<UiText>
        get() = _snackBarMessages.asSharedFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SetOfflineBackupUri -> setOfflineBackupUri(event.uri)
            is SettingsEvent.TriggerOfflineBackup -> triggerOfflineBackup()
            is SettingsEvent.ImportBackupFile -> importBackupFile(event.uri)
        }
    }

    private fun triggerOfflineBackup() {
        viewModelScope.launch {
            _settingsState.value.selectedUri?.let { uri ->
                try {
                    databaseBackupExporter.exportDatabaseToDirectory(uri)
                    _snackBarMessages.emit(
                        UiText.StringResource(R.string.database_exported_successfully)
                    )
                } catch (ex: Exception) {
                    _snackBarMessages.emit(UiText.StringResource(R.string.database_export_failed))
                }
            }
        }
    }

    private fun setOfflineBackupUri(uri: Uri) {
        _settingsState.value = _settingsState.value.copy(selectedUri = uri)
    }

    private fun importBackupFile(uri: Uri) {
        viewModelScope.launch {
            try {
                databaseBackupImporter.importDatabase(uri)
            } catch (ex: Exception) {
                _snackBarMessages.emit(UiText.StringResource(R.string.database_import_failed))
            }
        }
    }
}
