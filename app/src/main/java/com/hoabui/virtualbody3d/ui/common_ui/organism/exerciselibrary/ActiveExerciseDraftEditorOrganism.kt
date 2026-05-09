package com.hoabui.virtualbody3d.ui.common_ui.organism.exerciselibrary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ActiveExerciseInfo
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

/**
 * Per-exercise rep/set/min (or duration) editor; UI-only — callbacks forward to [ExerciseLibraryViewModel].
 *
 * @param isSelectionBarContext When true (default), uses [Modifier.weight] and an inner [verticalScroll] for the
 *   bounded selection bar. When false, the set block grows with content — use when this organism sits inside a
 *   parent [verticalScroll] (e.g. [SessionBookingEditorScreen]) to avoid infinite height constraints.
 */
@Composable
fun ActiveExerciseDraftEditorOrganism(
    activeExerciseInfo: ActiveExerciseInfo?,
    onStepCartField: (exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) -> Unit,
    onSetCartFieldManual: (exerciseId: String, setIndex: Int, field: CartSetField, value: String) -> Unit,
    isSelectionBarContext: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val activeId = activeExerciseInfo?.id
    val activeDraft = activeExerciseInfo?.draft
    if (activeId != null && activeDraft != null) {
        Column(modifier = modifier.fillMaxWidth()) {
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
                    onStepField = onStepCartField,
                    onSetFieldManual = onSetCartFieldManual,
                )
            }
            val setBlockModifier = if (isSelectionBarContext) {
                Modifier
                    .weight(1f, fill = true)
                    .fillMaxWidth()
                    .padding(horizontal = token.spacing.sm)
                    .verticalScroll(rememberScrollState())
            } else {
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = token.spacing.sm)
            }
            Column(
                modifier = setBlockModifier,
                verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
            ) {
                CartSetStepperSection(
                    exerciseId = activeId,
                    setRows = activeDraft.setRows,
                    measurementKind = activeExerciseInfo.measurementKind,
                    onStepField = onStepCartField,
                    onSetFieldManual = onSetCartFieldManual,
                )
            }
        }
    } else {
        activeExerciseInfo?.title?.let { title ->
            Column(
                modifier = modifier
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

@Preview(showBackground = true, name = "ActiveExerciseDraftEditor — Light")
@Composable
private fun PreviewActiveExerciseDraftEditorLight() {
    GymTheme {
        ActiveExerciseDraftEditorOrganism(
            activeExerciseInfo = null,
            onStepCartField = { _, _, _, _ -> },
            onSetCartFieldManual = { _, _, _, _ -> },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, name = "ActiveExerciseDraftEditor — Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewActiveExerciseDraftEditorDark() {
    GymTheme(darkTheme = true) {
        ActiveExerciseDraftEditorOrganism(
            activeExerciseInfo = null,
            onStepCartField = { _, _, _, _ -> },
            onSetCartFieldManual = { _, _, _, _ -> },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
