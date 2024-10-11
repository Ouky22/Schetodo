package com.example.schetodo.data.user_preferences

import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.schetodo.data.todo.TodoFilterSettings
import com.example.schetodo.di.CoroutineScopeModule.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope
) : UserPreferencesRepository {
    private val TAG = "UserPreferencesRepositoryImpl"

    companion object {
        val SHOW_RECURRING_TODOS = booleanPreferencesKey("show_recurring_todos")
        val SHOW_UNDONE_TODOS = booleanPreferencesKey("show_undone_todos")
        val SHOW_IN_PROGRESS_TODOS = booleanPreferencesKey("show_in_progress_todos")
        val SHOW_DONE_TODOS = booleanPreferencesKey("show_done_todos")

        val SHOW_SCHEDULE_BLOCK_NOTIFICATION_AT_BEGINNING = booleanPreferencesKey(
            "show_schedule_block_notification_at_beginning"
        )
        val SHOW_SCHEDULE_BLOCK_NOTIFICATION_AT_END = booleanPreferencesKey(
            "show_schedule_block_notification_at_start"
        )

        val SHOW_DATABASE_BACKUP_DIRECTORY_PATH = stringPreferencesKey(
            "show_database_backup_directory_path"
        )
    }

    override val showDatabaseBackupDirectoryPath = dataStore.data
        .catch { exception ->
            if (exception is IOException) Log.e(
                TAG, "Error while reading database-backup-directory-path",
                exception
            )
            else throw exception
        }
        .map { preferences ->

            try {
                preferences[SHOW_DATABASE_BACKUP_DIRECTORY_PATH]?.let { Uri.parse(it) } ?: Uri.EMPTY
            } catch (e: Exception) {
                Log.e(TAG, "Error while parsing database-backup-directory-path", e)
                Uri.EMPTY
            }
        }

    override val showScheduleBlockNotificationAtBeginning = dataStore.data
        .catch { exception ->
            if (exception is IOException) Log.e(
                TAG, "Error while reading show-schedule-block-notification-at-beginning flag",
                exception
            )
            else throw exception
        }
        .map { preferences ->
            preferences[SHOW_SCHEDULE_BLOCK_NOTIFICATION_AT_BEGINNING] ?: false
        }

    override val showScheduleBlockNotificationAtEnd = dataStore.data
        .catch { exception ->
            if (exception is IOException) Log.e(
                TAG, "Error while reading show-schedule-block-notification-at-end flag",
                exception
            )
            else throw exception
        }
        .map { preferences ->
            preferences[SHOW_SCHEDULE_BLOCK_NOTIFICATION_AT_END] ?: false
        }

    override suspend fun setShowScheduleBlockNotificationAtBeginning(show: Boolean) {
        applicationCoroutineScope.launch {
            dataStore.edit { preferences ->
                preferences[SHOW_SCHEDULE_BLOCK_NOTIFICATION_AT_BEGINNING] = show
            }
        }.join()
    }

    override suspend fun setShowScheduleBlockNotificationAtEnd(show: Boolean) {
        applicationCoroutineScope.launch {
            dataStore.edit { preferences ->
                preferences[SHOW_SCHEDULE_BLOCK_NOTIFICATION_AT_END] = show
            }
        }.join()
    }

    override suspend fun setDatabaseBackupDirectoryPath(uri: Uri) {
        applicationCoroutineScope.launch {
            dataStore.edit { preferences ->
                preferences[SHOW_DATABASE_BACKUP_DIRECTORY_PATH] = uri.toString()
            }
        }.join()
    }

    override val todoFilterSettingsPreferences = dataStore.data
        .catch { exception ->
            if (exception is IOException) Log.e(
                TAG, "Error while reading todo filter preferences",
                exception
            )
            else throw exception
        }
        .map { mapTodoFilterSettingsPreferences(it) }

    override suspend fun setTodoFilterSettings(todoFilterSettings: TodoFilterSettings) {
        applicationCoroutineScope.launch {
            dataStore.edit { preferences ->
                preferences[SHOW_RECURRING_TODOS] = todoFilterSettings.showRecurringTodos
                preferences[SHOW_UNDONE_TODOS] = todoFilterSettings.showUndoneTodos
                preferences[SHOW_IN_PROGRESS_TODOS] = todoFilterSettings.showInProgressTodos
                preferences[SHOW_DONE_TODOS] = todoFilterSettings.showDoneTodos
            }
        }.join()
    }

    private fun mapTodoFilterSettingsPreferences(preferences: Preferences) =
        TodoFilterSettings(
            showRecurringTodos = preferences[SHOW_RECURRING_TODOS] ?: true,
            showUndoneTodos = preferences[SHOW_UNDONE_TODOS] ?: true,
            showInProgressTodos = preferences[SHOW_IN_PROGRESS_TODOS] ?: true,
            showDoneTodos = preferences[SHOW_DONE_TODOS] ?: false
        )
}