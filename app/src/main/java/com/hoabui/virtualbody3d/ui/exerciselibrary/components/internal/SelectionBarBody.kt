package com.hoabui.virtualbody3d.ui.exerciselibrary.components.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.CartSetsCountStepper
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.CartSetStepperSection
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ActiveExerciseInfo
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryActions
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlin.math.roundToInt

@Composable
internal fun ColumnScope.SelectionBarBody(
    activeExerciseInfo: ActiveExerciseInfo?,
    actions: ExerciseLibraryActions,
) {
    val token = GymTheme.token
    val activeId = activeExerciseInfo?.id
    val activeDraft = activeExerciseInfo?.draft
    if (activeId != null && activeDraft != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = token.spacing.sm,
                    end = token.spacing.sm,
                    top = token.spacing.sm,
                    bottom = token.spacing.xxs,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
            ) {
                activeExerciseInfo.title?.let { title ->
                    GText(
                        text = title,
                        style = token.typography.titleMedium,
                        color = token.colors.textPrimary,
                    )
                }
                if (activeExerciseInfo.estimatedCalories > 0f) {
                    val estimatedCaloriesLabel = stringResource(
                        R.string.exercise_library_estimated_calories,
                        activeExerciseInfo.estimatedCalories.roundToInt(),
                    )
                    GText(
                        text = estimatedCaloriesLabel,
                        style = token.typography.labelSmall,
                        color = token.colors.textSecondary,
                    )
                }
            }
            CartSetsCountStepper(
                exerciseId = activeId,
                setRows = activeDraft.setRows,
                onStepField = actions.onStepCartField,
                onSetFieldManual = actions.onSetCartFieldManual,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth()
                .padding(horizontal = token.spacing.sm)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            CartSetStepperSection(
                exerciseId = activeId,
                setRows = activeDraft.setRows,
                measurementMode = activeExerciseInfo.measurementMode,
                onStepField = actions.onStepCartField,
                onSetFieldManual = actions.onSetCartFieldManual,
            )
        }
    } else {
        activeExerciseInfo?.title?.let { title ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = token.spacing.sm,
                        end = token.spacing.sm,
                        top = token.spacing.sm,
                        bottom = token.spacing.xxs,
                    ),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
            ) {
                GText(
                    text = title,
                    style = token.typography.titleMedium,
                    color = token.colors.textPrimary,
                )
                if (activeExerciseInfo.estimatedCalories > 0f) {
                    val estimatedCaloriesLabel = stringResource(
                        R.string.exercise_library_estimated_calories,
                        activeExerciseInfo.estimatedCalories.roundToInt(),
                    )
                    GText(
                        text = estimatedCaloriesLabel,
                        style = token.typography.labelSmall,
                        color = token.colors.textSecondary,
                    )
                }
            }
        }
    }
}
