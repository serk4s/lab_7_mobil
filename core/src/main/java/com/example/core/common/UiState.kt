package com.example.core.common

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Empty(val title: String, val subtitle: String) : UiState<Nothing>
    data class Success<T>(val value: T) : UiState<T>
}
