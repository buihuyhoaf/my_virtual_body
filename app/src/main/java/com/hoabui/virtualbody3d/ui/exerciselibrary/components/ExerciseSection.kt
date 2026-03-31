package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionCardRow
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem

@Composable
fun ExerciseSection(
    modifier: Modifier = Modifier,
    section: ExerciseSectionUiItem,
    onNavigateDetail: (String) -> Unit = {},
    onToggleSelection: ((String) -> Unit)? = null,
    toggleAddContentDescription: String = "",
    toggleRemoveContentDescription: String = "",
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
        onNavigateDetail = onNavigateDetail,
        onToggleSelection = onToggleSelection,
        toggleAddContentDescription = toggleAddContentDescription,
        toggleRemoveContentDescription = toggleRemoveContentDescription,
    )
}
