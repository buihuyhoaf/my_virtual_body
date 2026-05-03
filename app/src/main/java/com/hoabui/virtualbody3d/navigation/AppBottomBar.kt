package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hoabui.virtualbody3d.core.extensions.rememberBottomBarItemState
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/**
 * [WorkoutCalendarRoute] / [ExerciseLibraryGraphRoute] (and its children) are opened from the coach area;
 * map them to the Cenfit coach bottom bar when computing selection.
 * Map them to the coach bottom bar item when computing selection.
 */
private fun isBottomBarTabSelected(
    destination: AppDestination,
    current: NavDestination?,
): Boolean {
    if (current == null) return false
    if (current.hierarchy.any { it.hasRoute(destination.route::class) }) return true
    if (destination == AppDestination.CenfitCoach) {
        return current.hasRoute(WorkoutCalendarRoute::class) ||
            current.hasRoute(ExerciseLibraryGraphRoute::class) ||
            current.hasRoute(ExerciseDashboardRoute::class) ||
            current.hasRoute(ExerciseLibraryRoute::class) ||
            current.hasRoute(SessionBookingEditorRoute::class)
    }
    return false
}

private fun isBottomBarTabRoute(current: NavDestination?): Boolean {
    if (current == null) return false
    return AppDestination.bottomBarDestinations.any { destination ->
        current.hierarchy.any { it.hasRoute(destination.route::class) }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    destination: AppDestination,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    token: GymToken,
) {
    val bodyToken = token.bodyAnalysis
    val interactionSource = remember { MutableInteractionSource() }
    val itemState = rememberBottomBarItemState(
        selected = selected,
        interactionSource = interactionSource,
        token = token,
        pillWidthCollapsed = bodyToken.bottomBarIconSize,
        pillWidthExpanded = bodyToken.bottomBarSelectionPillWidthExpanded,
    )
    val pillShape = RoundedCornerShape(token.radius.pill)

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = token.colors.primarySoft,
                ),
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.height(bodyToken.bottomBarSelectionPillHeight),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(itemState.pillWidth)
                    .height(bodyToken.bottomBarSelectionPillHeight)
                    .background(
                        color = itemState.pillBackgroundColor,
                        shape = pillShape,
                    )
            )
            Icon(
                imageVector = icon,
                contentDescription = stringResource(destination.labelResId),
                tint = itemState.contentColor,
                modifier = Modifier
                    .scale(itemState.scale)
                    .size(bodyToken.bottomBarIconSize)
            )
        }

        Spacer(modifier = Modifier.height(bodyToken.bottomBarLabelTopSpacing))

        Text(
            text = stringResource(destination.labelResId),
            style = token.typography.labelSmall,
            color = itemState.contentColor,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AppBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val shellShape = RoundedCornerShape(
        topStart = token.radius.xl,
        topEnd = token.radius.xl,
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shape = shellShape,
        color = token.colors.dashboardFloatingNavBackground,
        shadowElevation = token.spacing.xs,
        border = BorderStroke(
            token.borderWidth.thin,
            token.colors.dashboardFloatingNavBorder,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = bodyToken.dashboardFloatingNavHorizontalPadding,
                    vertical = token.spacing.md
                ),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppDestination.bottomBarDestinations.forEach { destination ->
                val tabIcon = destination.bottomBarIcon ?: return@forEach
                val selected = isBottomBarTabSelected(destination, currentDestination)
                BottomBarItem(
                    destination = destination,
                    icon = tabIcon,
                    selected = selected,
                    token = token,
                    onClick = {
                        if (isBottomBarTabRoute(currentDestination)) {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}
