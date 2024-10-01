package com.example.schetodo.feature.settings

import android.net.Uri

sealed class SettingsEvent {
    data class SetOfflineBackupUri(val uri: Uri) : SettingsEvent()
}
