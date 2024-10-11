package com.example.schetodo.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.feature.dbbackup.DatabaseBackupExporter
import com.example.schetodo.feature.dbbackup.DatabaseBackupImporter
import com.example.schetodo.feature.settings.SettingsEvent.SetOfflineBackupUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                databaseBackupExporter.exportDatabaseToDirectory(uri)
            }
        }
    }

    private fun setOfflineBackupUri(uri: Uri) {
        _settingsState.value = _settingsState.value.copy(selectedUri = uri)
    }

    private fun importBackupFile(uri: Uri) {
        viewModelScope.launch {
            databaseBackupImporter.importDatabase(uri)
        }
    }
}
