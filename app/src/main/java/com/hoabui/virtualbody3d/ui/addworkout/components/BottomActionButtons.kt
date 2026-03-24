package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Main actions for the screen: Cancel (Ghost) and primary action (Primary).
 * Row with buttons aligned end; spacing from [GymTheme.token].
 */
@Composable
fun BottomActionButtons(
    onCancel: () -> Unit,
    onPrimaryAction: () -> Unit,
    primaryLabel: String = stringResource(R.string.add_workout_add),
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingValues(token.spacing.md)),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.md, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GButton(
            text = stringResource(R.string.add_workout_cancel),
            onClick = onCancel,
            variant = GButtonVariant.Ghost,
        )
        GButton(
            text = primaryLabel,
            onClick = onPrimaryAction,
        )
    }
}
