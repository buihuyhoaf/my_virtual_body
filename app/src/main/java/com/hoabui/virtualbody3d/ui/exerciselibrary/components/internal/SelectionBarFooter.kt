package com.hoabui.virtualbody3d.ui.exerciselibrary.components.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
internal fun SelectionBarFooter(
    isSelectionBarEditMode: Boolean,
    bookingEnabled: Boolean,
    isConfirmEnabled: Boolean,
    onAddToSession: () -> Unit,
    onConfirmSelectionBarEdit: () -> Unit,
    onCancelSelectionBarEdit: () -> Unit,
) {
    val token = GymTheme.token
    val horizontal = Modifier
        .fillMaxWidth()
        .padding(token.spacing.sm)
    if (isSelectionBarEditMode) {
        Row(
            modifier = horizontal,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
        ) {
            GButton(
                text = stringResource(R.string.exercise_library_selection_confirm),
                onClick = onConfirmSelectionBarEdit,
                modifier = Modifier.weight(1f),
                enabled = isConfirmEnabled,
            )
            GButton(
                text = stringResource(R.string.exercise_library_selection_cancel),
                onClick = onCancelSelectionBarEdit,
                modifier = Modifier.weight(1f),
                variant = GButtonVariant.Outlined,
            )
        }
    } else {
        GButton(
            text = stringResource(R.string.exercise_library_add_to_session),
            onClick = onAddToSession,
            modifier = horizontal,
            enabled = bookingEnabled,
        )
    }
}
