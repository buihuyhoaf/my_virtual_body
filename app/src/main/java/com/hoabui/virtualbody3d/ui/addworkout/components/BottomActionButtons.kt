package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Main actions for the screen: Cancel (TextButton) and primary action (Add Workout).
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
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel) {
            Text(
                text = stringResource(R.string.add_workout_cancel),
                style = token.typography.labelLarge,
                color = token.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.width(token.spacing.md))
        Button(
            onClick = onPrimaryAction,
            shape = RoundedCornerShape(token.button.cornerRadius),
            colors = ButtonDefaults.buttonColors(containerColor = token.colors.primary),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = token.elevation.level0)
        ) {
            Text(
                text = primaryLabel,
                style = token.typography.labelLarge
            )
        }
    }
}
