package com.example.schetodo.feature.settings

import android.net.Uri

sealed class SettingsEvent {
    data class SetOfflineBackupUri(val uri: Uri) : SettingsEvent()
    data class ImportBackupFile(val uri: Uri) : SettingsEvent()
    data object TriggerOfflineBackup : SettingsEvent()
    data object SignInWithGoogle : SettingsEvent()
}
