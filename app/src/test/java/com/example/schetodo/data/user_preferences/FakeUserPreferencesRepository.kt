package com.example.schetodo.data.user_preferences

import android.net.Uri
import com.example.schetodo.data.todo.TodoFilterSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private var _todoFilterSettingsPreferences = TodoFilterSettings()
    private var _showScheduleBlockNotificationAtBeginning = false
    private var _showScheduleBlockNotificationAtEnd = false

    override val todoFilterSettingsPreferences: Flow<TodoFilterSettings>
        get() = flow { emit(_todoFilterSettingsPreferences) }

    override suspend fun setTodoFilterSettings(todoFilterSettings: TodoFilterSettings) {
        _todoFilterSettingsPreferences = todoFilterSettings
    }

    override val showScheduleBlockNotificationAtBeginning: Flow<Boolean>
        get() = flow {
            emit(_showScheduleBlockNotificationAtBeginning)
        }

    override val showScheduleBlockNotificationAtEnd: Flow<Boolean>
        get() = flow {
            emit(_showScheduleBlockNotificationAtEnd)
        }

    override suspend fun setShowScheduleBlockNotificationAtBeginning(show: Boolean) {
        _showScheduleBlockNotificationAtBeginning = show
    }

    override suspend fun setShowScheduleBlockNotificationAtEnd(show: Boolean) {
        _showScheduleBlockNotificationAtEnd = show
    }

    override val showDatabaseBackupDirectoryPath: Flow<Uri>
        get() = TODO("Not yet implemented")

    override suspend fun setDatabaseBackupDirectoryPath(uri: Uri) {
        TODO("Not yet implemented")
    }
}