package com.hoabui.virtualbody3d.core.base

/**
 * Generic UI state wrapper for common Loading / Success(data) / Error patterns.
 *
 * Use as: [UiState]<T> where T is your screen's content model.
 */
sealed interface UiState<out T> {

    /**
     * Initial or in-progress state while loading data.
     */
    data object Loading : UiState<Nothing>

    /**
     * Successful state with non-null [data].
     */
    data class Success<T>(val data: T) : UiState<T>

    /**
     * Error state with a human-readable [message].
     */
    data class Error(val message: String) : UiState<Nothing>
}

