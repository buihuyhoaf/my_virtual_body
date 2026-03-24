package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.Difficulty
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.addworkout.state.AddWorkoutUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.format.DateTimeFormatter

/**
 * Confirmation dialog showing workout summary before saving.
 * Sections: Exercise, Schedule, Workout setup, Notes (optional).
 * Buttons: Cancel (close), Confirm (calls onConfirm).
 * Uses [GymTheme.token] for all spacing, typography, colors, radius.
 */
@Composable
fun ConfirmWorkoutDialog(
    state: AddWorkoutUiState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val token = GymTheme.token
    val exercise = state.exercise ?: return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(token.radius.lg),
            color = token.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(token.spacing.md)
            ) {
                Text(
                    text = stringResource(R.string.add_workout_confirm_title),
                    style = token.typography.titleLarge,
                    color = token.colors.textPrimary,
                    modifier = Modifier.padding(bottom = token.spacing.md)
                )
                Column(
                    modifier = Modifier
                        .heightIn(max = token.spacing.xxxl * 7f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
                ) {
                    ConfirmExerciseSummary(exercise = exercise)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = token.spacing.xs),
                        color = token.colors.borderSubtle
                    )
                    ConfirmScheduleSummary(
                        date = state.selectedDate,
                        time = state.selectedTime
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = token.spacing.xs),
                        color = token.colors.borderSubtle
                    )
                    ConfirmWorkoutSetupSummary(
                        sets = state.sets,
                        reps = state.reps,
                        weightKg = state.weightKg,
                        restSeconds = state.restSeconds
                    )
                    if (state.notes.isNotBlank()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = token.spacing.xs),
                            color = token.colors.borderSubtle
                        )
                        ConfirmNotesSummary(notes = state.notes)
                    }
                }
                Spacer(modifier = Modifier.height(token.spacing.md))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GButton(
                        text = stringResource(R.string.add_workout_cancel),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        variant = GButtonVariant.Outlined,
                    )
                    GButton(
                        text = stringResource(R.string.add_workout_confirm_confirm),
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmExerciseSummary(
    exercise: Exercise,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val imageHeight = token.spacing.xxl + token.spacing.lg
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = token.spacing.xxs),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .height(imageHeight)
                .fillMaxWidth(0.2f),
            shape = RoundedCornerShape(token.radius.sm),
            color = token.colors.surfaceSubtle
        ) {
            Image(
                painter = painterResource(exercise.imageResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                style = token.typography.titleMedium,
                color = token.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(token.spacing.xxs))
            Text(
                text = stringResource(ExerciseDisplayResources.bodyRegionResId(exercise.bodyRegion)),
                style = token.typography.bodySmall,
                color = token.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(token.spacing.xxs))
            Surface(
                shape = RoundedCornerShape(token.radius.sm),
                color = token.colors.surfaceSubtle
            ) {
                val difficultyTextColor = when (exercise.difficulty) {
                    Difficulty.Beginner -> token.colors.difficultyBeginnerText
                    Difficulty.Intermediate -> token.colors.difficultyIntermediateText
                    Difficulty.Advanced -> token.colors.difficultyAdvancedText
                }
                Text(
                    text = stringResource(ExerciseDisplayResources.difficultyResId(exercise.difficulty)),
                    style = token.typography.labelSmall,
                    color = difficultyTextColor,
                    modifier = Modifier.padding(
                        horizontal = token.spacing.xs,
                        vertical = token.spacing.xxs
                    )
                )
            }
        }
    }
}

@Composable
private fun ConfirmScheduleSummary(
    date: java.time.LocalDate,
    time: java.time.LocalTime,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = token.spacing.xxs),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
    ) {
        Text(
            text = stringResource(R.string.add_workout_date) + ": " +
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
        Text(
            text = stringResource(R.string.add_workout_time) + ": " +
                time.format(DateTimeFormatter.ofPattern("HH:mm")),
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
    }
}

@Composable
private fun ConfirmWorkoutSetupSummary(
    sets: Int,
    reps: Int,
    weightKg: Double,
    restSeconds: Int,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = token.spacing.xxs),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
    ) {
        Text(
            text = "${stringResource(R.string.add_workout_sets)}: $sets",
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
        Text(
            text = "${stringResource(R.string.add_workout_reps)}: $reps",
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
        Text(
            text = "${stringResource(R.string.add_workout_weight_kg)}: %.1f".format(weightKg),
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
        Text(
            text = "${stringResource(R.string.add_workout_rest_seconds)}: $restSeconds s",
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
    }
}

@Composable
private fun ConfirmNotesSummary(
    notes: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = token.spacing.xxs)
    ) {
        Text(
            text = stringResource(R.string.add_workout_notes),
            style = token.typography.labelSmall,
            color = token.colors.textSecondary,
            modifier = Modifier.padding(bottom = token.spacing.xxs)
        )
        Text(
            text = notes,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
    }
}
