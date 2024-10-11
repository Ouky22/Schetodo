package com.example.schetodo.data.user_preferences

import android.net.Uri
import com.example.schetodo.data.todo.TodoFilterSettings
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val todoFilterSettingsPreferences: Flow<TodoFilterSettings>

    suspend fun setTodoFilterSettings(todoFilterSettings: TodoFilterSettings)
    val showScheduleBlockNotificationAtBeginning: Flow<Boolean>
    val showScheduleBlockNotificationAtEnd: Flow<Boolean>
    suspend fun setShowScheduleBlockNotificationAtBeginning(show: Boolean)
    suspend fun setShowScheduleBlockNotificationAtEnd(show: Boolean)
    val showDatabaseBackupDirectoryPath: Flow<Uri>
    suspend fun setDatabaseBackupDirectoryPath(uri: Uri)
}