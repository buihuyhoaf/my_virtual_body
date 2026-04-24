package com.hoabui.virtualbody3d.ui.exerciselibrary.state.model

import java.time.LocalTime

/**
 * Hoisted callbacks for the Exercise Library flow so screens and organisms
 * pass a single stable object instead of many lambda parameters.
 *
 * Not marked `Immutable`: lambda fields are unstable for Compose skippability; this type is for
 * wiring only, not as part of immutable UI state snapshots.
 */
data class ExerciseLibraryActions(
    val onQueryChange: (String) -> Unit,
    val onExerciseClick: (String) -> Unit,
    val onLibraryListToggle: (String) -> Unit,
    val onDetailAddToCart: (String) -> Unit,
    val onSelectCartItem: (String) -> Unit,
    val onRemoveCartItem: (String) -> Unit,
    val onClearCart: () -> Unit,
    val onActiveDraftChange: (sets: String, reps: String) -> Unit,
    val onAddToSession: () -> Unit,
    /** Navigate to combined rep/set/min + booking screen (replaces opening bottom sheet from library). */
    val onNavigateToSessionBookingEditor: () -> Unit,
    val onDismissSessionBooking: () -> Unit,
    val onBookingDateSelected: (Long) -> Unit,
    val onBookingLocationSelected: (String) -> Unit,
    val onBookingSlotToggled: (LocalTime) -> Unit,
    val onBookingClearTimeSelection: () -> Unit,
    val onConfirmSessionBooking: () -> Unit,
    val onLongSessionEdit: () -> Unit,
    val onLongSessionProceedAnyway: () -> Unit,
    val onClearExerciseDetail: () -> Unit,
    val onDismissAddExerciseSuccess: () -> Unit,
    val onNavigateToWorkoutCalendar: () -> Unit,
    /** Stepper [+]/[-] tap: step a specific field of a specific set row by [delta] units. */
    val onStepCartField: (exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) -> Unit,
    /** Tap on the [+] side of the Sets stepper: add a new cloned row for [exerciseId]. */
    val onAddCartSetRow: (exerciseId: String) -> Unit,
    /** Manual numeric entry for a specific field of a specific set row. */
    val onSetCartFieldManual: (exerciseId: String, setIndex: Int, field: CartSetField, value: String) -> Unit,
    /** Toggle the cart panel between collapsed and expanded states. */
    val onToggleCartExpanded: () -> Unit,
    /** Toggle RegionGroup selection by strip quadrant tap (0..3). */
    val onFocusStripQuadrantTap: (Int) -> Unit,
    /** Confirm selection-bar edits (persist to Room) and dismiss edit chrome. */
    val onConfirmSelectionBarEdit: () -> Unit,
    /** Discard edits, restore baseline cart, dismiss edit chrome. */
    val onCancelSelectionBarEdit: () -> Unit,
)
