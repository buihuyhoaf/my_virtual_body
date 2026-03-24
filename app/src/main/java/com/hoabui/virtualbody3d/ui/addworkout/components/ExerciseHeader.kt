package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.domain.model.exercise.Difficulty
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GMediaInfoCard
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Reusable header displaying selected exercise information.
 * Layout: horizontal media card with image thumbnail, name, body region, and difficulty badge.
 */
@Composable
fun ExerciseHeader(
    exercise: Exercise,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    GMediaInfoCard(
        imageModel = exercise.imageResId,
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
        modifier = modifier.fillMaxWidth(),
    )
}
