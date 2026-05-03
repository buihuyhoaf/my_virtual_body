package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionCardRow
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibrarySectionRowUiModel

@Composable
fun ExerciseSection(
    modifier: Modifier = Modifier,
    section: ExerciseLibrarySectionRowUiModel,
    onCardTap: (String) -> Unit = {},
) {
    val regionLabel = stringResource(ExerciseDisplayResources.bodyRegionResId(section.bodyRegion))
    val uiSection = remember(section.bodyRegion, section.items, regionLabel) {
        GExerciseSectionUiModel(
            id = section.bodyRegion.name,
            title = regionLabel,
            items = section.items,
        )
    }
    GExerciseSectionCardRow(
        section = uiSection,
        modifier = modifier,
        onCardTap = onCardTap,
    )
}
