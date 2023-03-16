package com.example.schetodo.data.user_preferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.schetodo.data.todo.TodoFilterSettings
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    private val TAG = "UserPreferencesRepositoryImpl"

    companion object {
        val SHOW_RECURRING_TODOS = booleanPreferencesKey("show_recurring_todos")
        val SHOW_UNDONE_TODOS = booleanPreferencesKey("show_undone_todos")
        val SHOW_IN_PROGRESS_TODOS = booleanPreferencesKey("show_in_progress_todos")
        val SHOW_DONE_TODOS = booleanPreferencesKey("show_done_todos")
    }

    override val todoFilterSettingsPreferences = dataStore.data
        .catch { exception ->
            if (exception is IOException) Log.e(
                TAG, "Error reading todo filter preferences",
                exception
            )
            else throw exception
        }
        .map { mapTodoFilterSettingsPreferences(it) }

    override suspend fun setTodoFilterSettings(todoFilterSettings: TodoFilterSettings) {
        dataStore.edit { preferences ->
            preferences[SHOW_RECURRING_TODOS] = todoFilterSettings.showRecurringTodos
            preferences[SHOW_UNDONE_TODOS] = todoFilterSettings.showUndoneTodos
            preferences[SHOW_IN_PROGRESS_TODOS] = todoFilterSettings.showInProgressTodos
            preferences[SHOW_DONE_TODOS] = todoFilterSettings.showDoneTodos
        }
    }

    private fun mapTodoFilterSettingsPreferences(preferences: Preferences) =
        TodoFilterSettings(
            showRecurringTodos = preferences[SHOW_RECURRING_TODOS] ?: false,
            showUndoneTodos = preferences[SHOW_UNDONE_TODOS] ?: false,
            showInProgressTodos = preferences[SHOW_IN_PROGRESS_TODOS] ?: false,
            showDoneTodos = preferences[SHOW_DONE_TODOS] ?: false
        )
}