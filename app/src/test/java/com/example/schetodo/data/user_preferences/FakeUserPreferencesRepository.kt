package com.example.schetodo.data.user_preferences

import com.example.schetodo.data.todo.TodoFilterSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private var _todoFilterSettingsPreferences = TodoFilterSettings()

    override val todoFilterSettingsPreferences: Flow<TodoFilterSettings>
        get() = flow { emit(_todoFilterSettingsPreferences) }

    override suspend fun setTodoFilterSettings(todoFilterSettings: TodoFilterSettings) {
        _todoFilterSettingsPreferences = todoFilterSettings
    }
}