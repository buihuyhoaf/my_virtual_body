package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.toImageModel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GMediaInfoCard
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources

/**
 * Reusable header displaying selected exercise information.
 * Layout: horizontal media card with image thumbnail, name, and body region.
 */
@Composable
fun ExerciseHeader(
    exercise: Exercise,
    modifier: Modifier = Modifier
) {
    val resourceProvider = LocalResourceProvider.current
    GMediaInfoCard(
        imageModel = exercise.image.toImageModel(resourceProvider),
        title = exercise.name,
        subtitle = stringResource(ExerciseDisplayResources.bodyRegionResId(exercise.bodyRegion)),
        modifier = modifier.fillMaxWidth(),
    )
}
