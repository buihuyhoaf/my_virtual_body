package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Reusable stepper for numeric workout values (sets, reps, rest time).
 * Row: [ Label | [-] value [+] ].
 * Uses [GymTheme.token] for spacing, typography, colors.
 */
@Composable
fun WorkoutNumberStepper(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int? = null,
    valueSuffix: String = ""
) {
    val token = GymTheme.token
    val effectiveMax = maxValue ?: Int.MAX_VALUE

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        GText(
            text = label,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xxs)
        ) {
            GIconButton(
                onClick = { if (value > minValue) onValueChange(value - 1) },
                enabled = value > minValue,
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = stringResource(R.string.add_workout_decrease)
                )
            }
            GText(
                text = "$value$valueSuffix",
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
                modifier = Modifier.width(token.spacing.xxl)
            )
            GIconButton(
                onClick = { if (value < effectiveMax) onValueChange(value + 1) },
                enabled = value < effectiveMax,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_workout_increase)
                )
            }
        }
    }
}

/**
 * Stepper for weight (kg). Same layout as [WorkoutNumberStepper] with Double value and step.
 */
@Composable
fun WorkoutWeightStepper(
    label: String,
    valueKg: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Double = 0.0,
    maxValue: Double = 500.0,
    step: Double = 2.5
) {
    val token = GymTheme.token
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        GText(
            text = label,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xxs)
        ) {
            GIconButton(
                onClick = {
                    val next = (valueKg - step).coerceIn(minValue, maxValue)
                    if (next != valueKg) onValueChange(next)
                },
                enabled = valueKg > minValue,
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = stringResource(R.string.add_workout_decrease)
                )
            }
            GText(
                text = "%.1f kg".format(valueKg),
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
                modifier = Modifier.width(token.spacing.xxl)
            )
            GIconButton(
                onClick = {
                    val next = (valueKg + step).coerceIn(minValue, maxValue)
                    if (next != valueKg) onValueChange(next)
                },
                enabled = valueKg < maxValue,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_workout_increase)
                )
            }
        }
    }
}
