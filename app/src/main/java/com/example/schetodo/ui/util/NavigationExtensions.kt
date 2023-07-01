package com.example.schetodo.ui.util

import androidx.navigation.NavController

fun <T> NavController.pushOntoPreviousBackStackEntry(key: String, value: T) {
    previousBackStackEntry?.savedStateHandle?.apply {
        set(key, value)
    }
}

suspend fun <T> NavController.popFromCurrentBackStackEntry(
    key: String,
    onPop: suspend (T) -> Unit
) {
    currentBackStackEntry?.savedStateHandle?.getStateFlow<T?>(key, null)?.collect { value ->
        value?.let {
            onPop.invoke(it)
            currentBackStackEntry?.savedStateHandle?.remove<T>(key)
        }
    }
}