package com.hoabui.virtualbody3d.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel for screens that expose a UiState<T> with
 * Loading / Success(data) / Error(message) semantics.
 */
abstract class UiStateViewModel<T, E : Any> : ViewModel() {

    private val _state = MutableStateFlow<UiState<T>>(UiState.Loading)
    val state: StateFlow<UiState<T>> = _state.asStateFlow()

    private val _events = Channel<E>(Channel.BUFFERED)
    val events: Flow<E> = _events.receiveAsFlow()

    protected fun setLoading() {
        _state.value = UiState.Loading
    }

    protected fun setSuccess(data: T) {
        _state.value = UiState.Success(data)
    }

    protected fun setError(message: String) {
        _state.value = UiState.Error(message)
    }

    protected fun sendEvent(event: E) {
        _events.trySend(event)
    }

    /**
     * Launches [block] in [viewModelScope], routing any exception to [onError].
     */
    protected fun launchSafely(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    /**
     * Called when [launchSafely] catches an exception.
     * Default implementation maps it to an Error UiState.
     */
    protected open fun onError(throwable: Throwable) {
        setError(throwable.message ?: "Unknown error")
    }
}

