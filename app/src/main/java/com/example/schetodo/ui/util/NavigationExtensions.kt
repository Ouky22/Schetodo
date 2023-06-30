package com.example.schetodo.ui.util

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <T> NavController.pushOntoPreviousBackStackEntry(key: String, value: T) {
    previousBackStackEntry?.savedStateHandle?.apply {
        set(key, value)
    }
}


fun <T> NavController.popFromCurrentBackStackEntry(
    key: String,
    coroutineScope: CoroutineScope,
    onPop: (T) -> Unit
) {
    coroutineScope.launch {
        currentBackStackEntry?.savedStateHandle?.getStateFlow<T?>(key, null)?.collect { value ->
            value?.let {
                onPop.invoke(it)
                currentBackStackEntry?.savedStateHandle?.remove<T>(key)
            }
        }
    }
}