package com.example.schetodo.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.schetodo.feature.settings.SettingsEvent.SetOfflineBackupUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState>
        get() = _settingsState.asStateFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SetOfflineBackupUri -> setOfflineBackupUri(event.uri)
        }
    }

    private fun setOfflineBackupUri(uri: Uri) {
        _settingsState.value = _settingsState.value.copy(selectedUri = uri)
    }
}
