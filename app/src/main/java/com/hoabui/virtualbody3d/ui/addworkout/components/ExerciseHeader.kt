package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.domain.model.exercise.Difficulty
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Reusable header displaying selected exercise information.
 * Layout: Row [ Image (72–88dp) | Column(name, body region, difficulty badge) ].
 * Uses [GymTheme.token] for spacing, typography, colors, radius, elevation.
 */
@Composable
fun ExerciseHeader(
    exercise: Exercise,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val imageHeight = token.spacing.xxl + token.spacing.lg

    GCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .height(imageHeight)
                    .fillMaxWidth(0.25f),
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
                Spacer(modifier = Modifier.height(token.spacing.xs))
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
}
