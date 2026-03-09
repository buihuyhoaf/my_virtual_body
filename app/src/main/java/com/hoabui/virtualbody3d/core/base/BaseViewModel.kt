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
 * Generic base ViewModel with state (S) and one-off events (E).
 *
 * Subclasses provide an initial state via [initialState] and can use [updateState] or [setState]
 * to mutate it in an immutable way (`copy(...)` for data classes).
 */
abstract class BaseViewModel<S : Any, E : Any> : ViewModel() {

    /**
     * Initial value for [state]. Each subclass must provide its own immutable initial state.
     */
    protected abstract fun initialState(): S

    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState())
    val state: StateFlow<S> = _state.asStateFlow()

    private val _events = Channel<E>(Channel.BUFFERED)
    val events: Flow<E> = _events.receiveAsFlow()

    /**
     * Updates state using [block]. Use `copy(...)` inside the block for immutable updates.
     * Example: `updateState { copy(isLoading = true) }`
     *
     * NOTE: For simple state replacement (no need for previous value), prefer [setState]
     * to avoid relying on the current state's receiver.
     */
    protected fun updateState(block: S.() -> S) {
        _state.value = _state.value.block()
    }

    /**
     * Directly replace the current state with [newState].
     * Useful when your new state does not depend on the previous one.
     */
    protected fun setState(newState: S) {
        _state.value = newState
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
