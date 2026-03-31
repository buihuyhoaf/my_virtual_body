package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GFloatingActionButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.icons.ExerciseLibraryPhosphorIcons
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment

@Composable
fun ExerciseLibraryWorkoutPlanFab(
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val fabIcon = token.bodyAnalysis.exerciseLibraryWorkoutPlanFabIconSize
    val badgeMin = token.bodyAnalysis.exerciseLibraryWorkoutPlanFabBadgeMinSize
    val badgeShape = RoundedCornerShape(token.radius.pill)

    Box(modifier = modifier) {
        GFloatingActionButton(onClick = onClick) {
            GIcon(
                imageVector = ExerciseLibraryPhosphorIcons.workoutPlanFab,
                contentDescription = stringResource(R.string.exercise_library_workout_plan_fab_cd),
                modifier = Modifier.size(fabIcon),
            )
        }
        if (badgeCount > 0) {
            val badgeText = if (badgeCount > FAB_BADGE_MAX_DISPLAY) {
                stringResource(R.string.exercise_library_workout_plan_fab_badge_max, FAB_BADGE_MAX_DISPLAY)
            } else {
                badgeCount.toString()
            }
            GSurface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .defaultMinSize(minWidth = badgeMin, minHeight = badgeMin)
                    .offset(
                        x = token.spacing.sm,
                        y = -token.spacing.xs,
                    ),
                shape = badgeShape,
                color = token.colors.error,
                shadowElevation = token.elevation.level1,
                treatment = GSurfaceTreatment.Flat,
                border = null,
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = token.spacing.xxs),
                    contentAlignment = Alignment.Center,
                ) {
                    GText(
                        text = badgeText,
                        style = token.typography.labelSmall,
                        color = token.colors.onError,
                    )
                }
            }
        } else {
            GSurface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(token.spacing.xs)
                    .offset(
                        x = token.spacing.xxs,
                        y = -token.spacing.xxs,
                    ),
                shape = badgeShape,
                color = token.colors.error,
                shadowElevation = token.elevation.level0,
                treatment = GSurfaceTreatment.Flat,
                border = null,
            ) {
                Box(modifier = Modifier)
            }
        }
    }
}

private const val FAB_BADGE_MAX_DISPLAY = 99
