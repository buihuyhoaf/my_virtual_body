package com.hoabui.virtualbody3d.ui.exerciselibrary.components.internal

import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField

internal data class SelectionBarInteractionCallbacks(
    val onSelectCartItem: (String) -> Unit,
    val onRemoveCartItem: (String) -> Unit,
    val onClearCart: () -> Unit,
    val onToggleCartExpanded: () -> Unit,
    val onStepCartField: (exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) -> Unit,
    val onSetCartFieldManual: (exerciseId: String, setIndex: Int, field: CartSetField, value: String) -> Unit,
    val onNavigateToSessionBooking: () -> Unit,
    val onConfirmSelectionBarEdit: () -> Unit,
    val onCancelSelectionBarEdit: () -> Unit,
)
