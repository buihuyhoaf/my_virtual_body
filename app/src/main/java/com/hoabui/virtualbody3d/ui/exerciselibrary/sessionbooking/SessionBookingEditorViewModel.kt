package com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.usecase.ClearCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.DismissSessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseCatalogUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryUiStateUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveSessionBookingEditorUiStateUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingClearTimeSelectionUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingDateSelectedUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingLocationSelectedUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnBookingSlotToggledUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnLongSessionEditUseCase
import com.hoabui.virtualbody3d.domain.usecase.OnLongSessionProceedAnywayUseCase
import com.hoabui.virtualbody3d.domain.usecase.OpenSessionBookingUseCase
import com.hoabui.virtualbody3d.domain.usecase.RemoveCartItemUseCase
import com.hoabui.virtualbody3d.domain.usecase.RunExerciseLibraryBookingConfirmationUseCase
import com.hoabui.virtualbody3d.domain.usecase.SelectCartItemUseCase
import com.hoabui.virtualbody3d.domain.usecase.SetCartFieldManualUseCase
import com.hoabui.virtualbody3d.domain.usecase.StepCartFieldUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class SessionBookingEditorViewModel @Inject constructor(
    private val observeExerciseCatalogUseCase: ObserveExerciseCatalogUseCase,
    private val observeExerciseLibraryUiStateUseCase: ObserveExerciseLibraryUiStateUseCase,
    private val observeSessionBookingEditorUiStateUseCase: ObserveSessionBookingEditorUiStateUseCase,
    private val openSessionBookingUseCase: OpenSessionBookingUseCase,
    private val dismissSessionBookingUseCase: DismissSessionBookingUseCase,
    private val onBookingDateSelectedUseCase: OnBookingDateSelectedUseCase,
    private val onBookingLocationSelectedUseCase: OnBookingLocationSelectedUseCase,
    private val onBookingSlotToggledUseCase: OnBookingSlotToggledUseCase,
    private val onBookingClearTimeSelectionUseCase: OnBookingClearTimeSelectionUseCase,
    private val runExerciseLibraryBookingConfirmationUseCase: RunExerciseLibraryBookingConfirmationUseCase,
    private val onLongSessionEditUseCase: OnLongSessionEditUseCase,
    private val onLongSessionProceedAnywayUseCase: OnLongSessionProceedAnywayUseCase,
    private val selectCartItemUseCase: SelectCartItemUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val stepCartFieldUseCase: StepCartFieldUseCase,
    private val setCartFieldManualUseCase: SetCartFieldManualUseCase,
) : UiStateViewModel<SessionBookingEditorPresentationState, SessionBookingEvent>() {

    init {
        observeExerciseCatalogUseCase.startCollection(viewModelScope) {
            setError(it.message ?: "Unknown error")
        }
        observeSessionBookingEditorUiStateUseCase.observe(viewModelScope)
            .onEach { setSuccess(it) }
            .launchIn(viewModelScope)
    }

    private suspend fun runBookingConfirmationWorkflow() {
        runExerciseLibraryBookingConfirmationUseCase { effect ->
            when (effect) {
                is ExerciseLibraryUiEffect.ShowAddExerciseSuccess -> {
                    sendEvent(SessionBookingEvent.ShowAddExerciseSuccess(effect.summary))
                }
            }
        }
    }

    fun selectCartItem(exerciseId: String) {
        sendEvent(SessionBookingEvent.CartItemSelected(exerciseId))
        selectCartItemUseCase(observeExerciseLibraryUiStateUseCase.snapshotForCartActions(), exerciseId)
    }

    fun removeCartItem(exerciseId: String) {
        sendEvent(SessionBookingEvent.CartItemRemoved(exerciseId))
        removeCartItemUseCase(observeExerciseLibraryUiStateUseCase.snapshotForCartActions(), exerciseId)
    }

    fun clearCart() {
        sendEvent(SessionBookingEvent.CartCleared)
        clearCartUseCase()
    }

    fun stepCartField(exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) {
        sendEvent(SessionBookingEvent.CartFieldStepped(exerciseId, setIndex, field, delta))
        stepCartFieldUseCase(exerciseId, setIndex, field, delta)
    }

    fun setCartFieldManual(exerciseId: String, setIndex: Int, field: CartSetField, value: String) {
        sendEvent(SessionBookingEvent.CartFieldManualSet(exerciseId, setIndex, field, value))
        setCartFieldManualUseCase(exerciseId, setIndex, field, value)
    }

    fun openSessionBooking() {
        sendEvent(SessionBookingEvent.SessionBookingOpened)
        openSessionBookingUseCase()
    }

    fun dismissSessionBooking() {
        sendEvent(SessionBookingEvent.SessionBookingDismissed)
        dismissSessionBookingUseCase()
    }

    fun onBookingDateSelected(dateMillis: Long) {
        sendEvent(SessionBookingEvent.BookingDateSelected(dateMillis))
        onBookingDateSelectedUseCase(dateMillis)
    }

    fun onBookingLocationSelected(locationId: String) {
        sendEvent(SessionBookingEvent.BookingLocationSelected(locationId))
        onBookingLocationSelectedUseCase(locationId)
    }

    fun onBookingSlotToggled(slotStart: LocalTime) {
        sendEvent(SessionBookingEvent.BookingSlotToggled(slotStart))
        onBookingSlotToggledUseCase(slotStart)
    }

    fun onBookingClearTimeSelection() {
        sendEvent(SessionBookingEvent.BookingTimeSelectionCleared)
        onBookingClearTimeSelectionUseCase()
    }

    fun confirmSessionBooking() {
        sendEvent(SessionBookingEvent.SessionBookingConfirmed)
        Log.d(BOOKING_LOG_TAG, "confirmSessionBooking: enqueue RunBookingConfirmation")
        launchSafely {
            runBookingConfirmationWorkflow()
        }
    }

    fun onLongSessionEdit() {
        sendEvent(SessionBookingEvent.LongSessionEditStarted)
        onLongSessionEditUseCase()
    }

    fun onLongSessionProceedAnyway() {
        sendEvent(SessionBookingEvent.LongSessionProceedAnyway)
        onLongSessionProceedAnywayUseCase()
        launchSafely {
            runBookingConfirmationWorkflow()
        }
    }

    private companion object {
        const val BOOKING_LOG_TAG = "ExerciseLibraryBooking"
    }
}
