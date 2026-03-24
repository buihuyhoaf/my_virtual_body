package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Section containing workout setup steppers: Sets, Reps, Weight (kg), Rest time (seconds).
 * Uses [WorkoutNumberStepper] and [WorkoutWeightStepper]; [GymTheme.token] for spacing and typography.
 */
@Composable
fun WorkoutSetupSection(
    sets: Int,
    reps: Int,
    weightKg: Double,
    restSeconds: Int,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit,
    onWeightChange: (Double) -> Unit,
    onRestChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(modifier = modifier) {
        GText(
            text = stringResource(R.string.add_workout_workout_setup),
            style = token.typography.titleSmall,
            color = token.colors.textPrimary,
            modifier = Modifier.padding(bottom = token.spacing.xs)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            WorkoutNumberStepper(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.add_workout_sets),
                value = sets,
                onValueChange = onSetsChange,
                minValue = 1,
                maxValue = 20
            )
            WorkoutNumberStepper(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.add_workout_reps),
                value = reps,
                onValueChange = onRepsChange,
                minValue = 1,
                maxValue = 100
            )
            WorkoutWeightStepper(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.add_workout_weight_kg),
                valueKg = weightKg,
                onValueChange = onWeightChange
            )
            WorkoutNumberStepper(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.add_workout_rest_seconds),
                value = restSeconds,
                onValueChange = onRestChange,
                minValue = 0,
                maxValue = 600,
                valueSuffix = " s"
            )
        }
    }
}
