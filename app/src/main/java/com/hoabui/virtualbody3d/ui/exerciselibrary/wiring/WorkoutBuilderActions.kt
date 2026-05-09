package com.hoabui.virtualbody3d.ui.exerciselibrary.wiring

import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField

data class WorkoutBuilderActions(
    val onSelectCartItem: (String) -> Unit,
    val onRemoveCartItem: (String) -> Unit,
    val onClearCart: () -> Unit,
    val onNavigateToSessionBooking: () -> Unit,
    val onNavigateToWorkoutCalendar: () -> Unit,
    val onStepCartField: (exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) -> Unit,
    val onSetCartFieldManual: (exerciseId: String, setIndex: Int, field: CartSetField, value: String) -> Unit,
    val onToggleCartExpanded: () -> Unit,
    val onConfirmSelectionBarEdit: () -> Unit,
    val onCancelSelectionBarEdit: () -> Unit,
)
