package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.Difficulty
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.addworkout.state.AddWorkoutUiState
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.divider.GDivider
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.toImageModel
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GMediaInfoCard
import com.hoabui.virtualbody3d.ui.common_ui.molecule.info.GInfoRow
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
    val resourceProvider = LocalResourceProvider.current

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
                GText(
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
                    ConfirmExerciseSummary(
                        exercise = exercise,
                        imageModel = exercise.image.toImageModel(resourceProvider)
                    )
                    GDivider(modifier = Modifier.padding(vertical = token.spacing.xs))
                    ConfirmScheduleSummary(
                        date = state.selectedDate,
                        time = state.selectedTime
                    )
                    GDivider(modifier = Modifier.padding(vertical = token.spacing.xs))
                    ConfirmWorkoutSetupSummary(
                        sets = state.sets,
                        reps = state.reps,
                        weightKg = state.weightKg,
                        restSeconds = state.restSeconds
                    )
                    if (state.notes.isNotBlank()) {
                        GDivider(modifier = Modifier.padding(vertical = token.spacing.xs))
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
    imageModel: Any?,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    GMediaInfoCard(
        imageModel = imageModel,
        title = exercise.name,
        subtitle = stringResource(ExerciseDisplayResources.bodyRegionResId(exercise.bodyRegion)),
        badge = {
            val difficultyTextColor = when (exercise.difficulty) {
                Difficulty.Beginner -> token.colors.difficultyBeginnerText
                Difficulty.Intermediate -> token.colors.difficultyIntermediateText
                Difficulty.Advanced -> token.colors.difficultyAdvancedText
            }
            GSurface(shape = RoundedCornerShape(token.radius.sm)) {
                GText(
                    text = stringResource(ExerciseDisplayResources.difficultyResId(exercise.difficulty)),
                    style = token.typography.labelSmall,
                    color = difficultyTextColor,
                    modifier = Modifier.padding(
                        horizontal = token.spacing.xs,
                        vertical = token.spacing.xxs,
                    ),
                )
            }
        },
        elevation = 0.dp,
        modifier = modifier.fillMaxWidth(),
    )
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
        GInfoRow(
            label = stringResource(R.string.add_workout_date),
            value = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        )
        GInfoRow(
            label = stringResource(R.string.add_workout_time),
            value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
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
        GInfoRow(
            label = stringResource(R.string.add_workout_sets),
            value = sets.toString(),
        )
        GInfoRow(
            label = stringResource(R.string.add_workout_reps),
            value = reps.toString(),
        )
        GInfoRow(
            label = stringResource(R.string.add_workout_weight_kg),
            value = "%.1f".format(weightKg),
        )
        GInfoRow(
            label = stringResource(R.string.add_workout_rest_seconds),
            value = "$restSeconds s",
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
        GText(
            text = stringResource(R.string.add_workout_notes),
            style = token.typography.labelSmall,
            color = token.colors.textSecondary,
            modifier = Modifier.padding(bottom = token.spacing.xxs)
        )
        GText(
            text = notes,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
    }
}
