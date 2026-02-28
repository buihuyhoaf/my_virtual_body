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
 * Production-ready base ViewModel with typed state (S), one-off events (E),
 * safe coroutine launch, and overridable error handling.
 *
 * @param S UiState type (must be immutable; use data class + copy).
 * @param E One-off event type (e.g. navigation, snackbar, dialog).
 * @param initialState Initial value for [state].
 */
abstract class BaseViewModel<S : Any, E : Any>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _events = Channel<E>(Channel.BUFFERED)
    val events: Flow<E> = _events.receiveAsFlow()

    /**
     * Updates state using [block]. Use `copy(...)` inside the block for immutable updates.
     * Example: `updateState { copy(isLoading = true) }`
     */
    protected fun updateState(block: S.() -> S) {
        _state.value = _state.value.block()
    }

    /**
     * Sends a one-off event. Events are delivered to a single collector (e.g. UI).
     * Use for navigation, snackbars, dialogs. Non-blocking; uses [Channel.trySend].
     */
    protected fun sendEvent(event: E) {
        _events.trySend(event)
    }

    /**
     * Launches [block] in [viewModelScope]. Catches any [Throwable] and delegates to [defaultError].
     * Use for async work that should not crash the app and may need centralised error handling.
     */
    protected fun launchSafely(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Throwable) {
                defaultError(e)
            }
        }
    }

    /**
     * Called when [launchSafely] catches an exception. Override to log, send an error event, or track.
     * Default is no-op so subclasses can choose their strategy.
     */
    protected open fun defaultError(throwable: Throwable) {
        // Override: e.g. log, sendEvent(ErrorEvent(throwable)), or report to crash reporting
    }
}
