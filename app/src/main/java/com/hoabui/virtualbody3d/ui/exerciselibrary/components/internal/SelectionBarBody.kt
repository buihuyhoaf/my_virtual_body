package com.hoabui.virtualbody3d.ui.exerciselibrary.components.internal

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hoabui.virtualbody3d.ui.common_ui.organism.exerciselibrary.ActiveExerciseDraftEditorOrganism
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ActiveExerciseInfo
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.WorkoutBuilderActions

@Composable
internal fun ColumnScope.SelectionBarBody(
    activeExerciseInfo: ActiveExerciseInfo?,
    actions: WorkoutBuilderActions,
) {
    ActiveExerciseDraftEditorOrganism(
        activeExerciseInfo = activeExerciseInfo,
        onStepCartField = actions.onStepCartField,
        onSetCartFieldManual = actions.onSetCartFieldManual,
        modifier = Modifier
            .weight(1f, fill = true)
            .fillMaxWidth(),
    )
}
