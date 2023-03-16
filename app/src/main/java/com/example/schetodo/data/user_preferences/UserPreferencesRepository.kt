package com.example.schetodo.data.user_preferences

import com.example.schetodo.data.todo.TodoFilterSettings
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val todoFilterSettingsPreferences: Flow<TodoFilterSettings>

    suspend fun setTodoFilterSettings(todoFilterSettings: TodoFilterSettings)
}